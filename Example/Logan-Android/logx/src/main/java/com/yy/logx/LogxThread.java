

package com.yy.logx;

import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class LogxThread extends Thread {

    private static final String TAG = "LogxThread";
    private static final int MINUTE = 60 * 1000;
    private static final long LONG = 24 * 60 * 60 * 1000;
    private static final int CACHE_SIZE = 1024;

    private final Object sync = new Object();
    private final Object sendSync = new Object();
    private volatile boolean mIsRun = true;

    private long mCurrentDay;
    private boolean mIsWorking;
    private File mFileDirectory;
    private boolean mIsSDCard;
    private long mLastTime;
    private LogxProtocol mLogxProtocol;
    private ConcurrentLinkedQueue<LogxModel> mCacheLogQueue;
    private String mCachePath; // 缓存文件路径
    private String mPath; //文件路径
    private long mSaveTime; //存储时间
    private long mMaxLogFile;//最大文件大小
    private long mMinSDCard;
    private String mEncryptKey16;
    private String mEncryptIv16;
    private int mSendLogStatusCode;
    // 发送缓存队列
    private ConcurrentLinkedQueue<LogxModel> mCacheSendQueue = new ConcurrentLinkedQueue<>();
    private ExecutorService mSingleThreadExecutor = Executors.newSingleThreadExecutor();

    public LogxThread(ConcurrentLinkedQueue<LogxModel> cacheLogQueue, String cachePath, String path, long saveTime,
                      long maxLogFile, long minSDCard, String encryptKey16, String encryptIv16) {
        mCacheLogQueue = cacheLogQueue;
        mCachePath = cachePath;
        mPath = path;
        mSaveTime = saveTime;
        mMaxLogFile = maxLogFile;
        mMinSDCard = minSDCard;
        mEncryptKey16 = encryptKey16;
        mEncryptIv16 = encryptIv16;
    }

    public void notifyRun() {
        if (!mIsWorking) {
            synchronized (sync) {
                sync.notify();
            }
        }
    }

    public void quit() {
        mIsRun = false;
        if (!mIsWorking) {
            synchronized (sync) {
                sync.notify();
            }
        }
    }

    @Override
    public void run() {
        super.run();
        while (mIsRun) {
            synchronized (sync) {
                mIsWorking = true;
                try {
                    LogxModel model = mCacheLogQueue.poll();
                    if (model == null) {
                        mIsWorking = false;
                        sync.wait();
                        mIsWorking = true;
                    } else {
                        doNetworkLog(model);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mIsWorking = false;
                }
            }
        }
    }

    private void doNetworkLog(LogxModel model) {
        if (mLogxProtocol == null) {
            mLogxProtocol = LogxProtocol.newInstance();
            mLogxProtocol.setOnLogxProtocolStatus(new OnLogxProtocolStatus() {
                @Override
                public void logxProtocolStatus(String cmd, int code) {
                    Logx.onListenerLogWriteStatus(cmd, code);
                }
            });
            mLogxProtocol.logx_init(mCachePath, mPath, (int) mMaxLogFile, mEncryptKey16, mEncryptIv16);
            mLogxProtocol.logx_debug(Logx.sDebug);
        }

        if (model == null || !model.isValid()) {
            return;
        }

        if (model.action == LogxModel.Action.WRITE) {
            doWriteLog2File(model.writeAction);
        } else if (model.action == LogxModel.Action.SEND) {
            if (model.sendAction.sendLogRunnable != null) {
                // 是否正在发送
                synchronized (sendSync) {
                    if (mSendLogStatusCode == SendLogRunnable.SENDING) {
                        mCacheSendQueue.add(model);
                    } else {
                        doSendLog2Net(model.sendAction);
                    }
                }
            }
        } else if (model.action == LogxModel.Action.FLUSH) {
            doFlushLog2File();
        }
    }

    private void doFlushLog2File() {
        if (Logx.sDebug) {
            Log.d(TAG, "Logx flush start");
        }
        if (mLogxProtocol != null) {
            mLogxProtocol.logx_flush();
        }
    }

    private boolean isDay() {
        long currentTime = System.currentTimeMillis();
        return mCurrentDay < currentTime && mCurrentDay + LONG > currentTime;
    }

    private void deleteExpiredFile(long deleteTime) {
        File dir = new File(mPath);
        if (dir.isDirectory()) {
            String[] files = dir.list();
            if (files != null) {
                for (String item : files) {
                    try {
                        if (TextUtils.isEmpty(item)) {
                            continue;
                        }
                        String[] longStrArray = item.split("\\.");
                        if (longStrArray.length > 0) {  //小于时间就删除
                            long longItem = Long.valueOf(longStrArray[0]);
                            if (longItem <= deleteTime && longStrArray.length == 1) {
                                new File(mPath, item).delete(); //删除文件
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void doWriteLog2File(WriteAction action) {
        if (Logx.sDebug) {
            Log.d(TAG, "Logx write start");
        }
        if (mFileDirectory == null) {
            mFileDirectory = new File(mPath);
        }

        if (!isDay()) {
            long tempCurrentDay = Util.getCurrentTime();
            //save时间
            long deleteTime = tempCurrentDay - mSaveTime;
            deleteExpiredFile(deleteTime);
            mCurrentDay = tempCurrentDay;
            mLogxProtocol.logx_open(String.valueOf(mCurrentDay));
        }

        long currentTime = System.currentTimeMillis(); //每隔1分钟判断一次
        if (currentTime - mLastTime > MINUTE) {
            mIsSDCard = isCanWriteSDCard();
        }
        mLastTime = System.currentTimeMillis();

        if (!mIsSDCard) { //如果大于50M 不让再次写入
            return;
        }
        mLogxProtocol.logx_write(action.flag, action.log, action.localTime, action.threadName, action.threadId,
                action.isMainThread);
    }

    private void doSendLog2Net(SendAction action) {
        if (Logx.sDebug) {
            Log.d(TAG, "Logx send start");
        }
        if (TextUtils.isEmpty(mPath) || action == null || !action.isValid()) {
            return;
        }
        boolean success = prepareLogFile(action);
        if (!success) {
            if (Logx.sDebug) {
                Log.d(TAG, "Logx prepare log file failed, can't find log file");
            }
            return;
        }
        action.sendLogRunnable.setSendAction(action);
        action.sendLogRunnable.setCallBackListener(new SendLogRunnable.OnSendLogCallBackListener() {
            @Override
            public void onCallBack(int statusCode) {
                synchronized (sendSync) {
                    mSendLogStatusCode = statusCode;
                    if (statusCode == SendLogRunnable.FINISH) {
                        mCacheLogQueue.addAll(mCacheSendQueue);
                        mCacheSendQueue.clear();
                        notifyRun();
                    }
                }
            }
        });
        mSendLogStatusCode = SendLogRunnable.SENDING;
        mSingleThreadExecutor.execute(action.sendLogRunnable);
    }

    /**
     * 发送日志前的预处理操作
     */
    private boolean prepareLogFile(SendAction action) {
        if (Logx.sDebug) {
            Log.d(TAG, "prepare log file");
        }
        if (isFile(action.date)) { //是否有日期文件
            String src = mPath + File.separator + action.date;
            if (action.date.equals(String.valueOf(Util.getCurrentTime()))) {
                doFlushLog2File();
                String des = mPath + File.separator + action.date + ".copy";
                if (copyFile(src, des)) {
                    action.uploadPath = des;
                    return true;
                }
            } else {
                action.uploadPath = src;
                return true;
            }
        } else {
            action.uploadPath = "";
        }
        return false;
    }

    private boolean copyFile(String src, String des) {
        boolean back = false;
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(new File(src));
            outputStream = new FileOutputStream(new File(des));
            byte[] buffer = new byte[CACHE_SIZE];
            int i;
            while ((i = inputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, i);
                outputStream.flush();
            }
            back = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return back;
    }

    private boolean isCanWriteSDCard() {
        boolean item = false;
        try {
            StatFs stat = new StatFs(mPath);
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            long total = availableBlocks * blockSize;
            if (total > mMinSDCard) { //判断SDK卡
                item = true;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return item;
    }

    private boolean isFile(String name) {
        boolean isExist = false;
        if (TextUtils.isEmpty(mPath)) {
            return false;
        }
        File file = new File(mPath + File.separator + name);
        if (file.exists() && file.isFile()) {
            isExist = true;
        }
        return isExist;
    }
}
