

package com.dianping.logan;

import android.os.Looper;
import android.text.TextUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentLinkedQueue;

class LoganControlCenter {

    private static LoganControlCenter sLoganControlCenter;

    private ConcurrentLinkedQueue<LoganModel> mCacheLogQueue = new ConcurrentLinkedQueue<>();
    private String mCachePath; // 缓存文件路径
    private String mPath; //文件路径
    private long mSaveTime; //存储时间
    private long mMaxLogFile;//最大文件大小
    private long mMinSDCard;
    private long mMaxQueue; //最大队列数
    private String mEncryptKey16;
    private String mEncryptIv16;
    private LoganThread mLoganThread;
    private SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");

    private LoganControlCenter(LoganConfig config) {
        if (!config.isValid()) {
            throw new NullPointerException("config's param is invalid");
        }

        mPath = config.mPathPath;
        mCachePath = config.mCachePath;
        mSaveTime = config.mDay;
        mMinSDCard = config.mMinSDCard;
        mMaxLogFile = config.mMaxFile;
        mMaxQueue = config.mMaxQueue;
        mEncryptKey16 = new String(config.mEncryptKey16);
        mEncryptIv16 = new String(config.mEncryptIv16);

        init();
    }

    private void init() {
        if (mLoganThread == null) {
            mLoganThread = new LoganThread(mCacheLogQueue, mCachePath, mPath, mSaveTime,
                    mMaxLogFile, mMinSDCard, mEncryptKey16, mEncryptIv16);
            mLoganThread.start();
        }
    }

    static LoganControlCenter instance(LoganConfig config) {
        if (sLoganControlCenter == null) {
            synchronized (LoganControlCenter.class) {
                if (sLoganControlCenter == null) {
                    sLoganControlCenter = new LoganControlCenter(config);
                }
            }
        }
        return sLoganControlCenter;
    }

    void write(String log, int flag) {
        if (TextUtils.isEmpty(log)) {
            return;
        }
        LoganModel model = new LoganModel();
        model.action = LoganModel.Action.WRITE;
        WriteAction action = new WriteAction();
        String threadName = Thread.currentThread().getName();
        long threadLog = Thread.currentThread().getId();
        boolean isMain = false;
        if (Looper.getMainLooper() == Looper.myLooper()) {
            isMain = true;
        }
        action.log = log;
        action.localTime = System.currentTimeMillis();
        action.flag = flag;
        action.isMainThread = isMain;
        action.threadId = threadLog;
        action.threadName = threadName;
        model.writeAction = action;
        if (mCacheLogQueue.size() < mMaxQueue) {
            mCacheLogQueue.add(model);
            if (mLoganThread != null) {
                mLoganThread.notifyRun();
            }
        }
    }

    void send(String dates[], SendLogRunnable runnable) {
        if (TextUtils.isEmpty(mPath) || dates == null || dates.length == 0) {
            return;
        }
        for (String date : dates) {
            if (TextUtils.isEmpty(date)) {
                continue;
            }
            long time = getDateTime(date);
            if (time > 0) {
                LoganModel model = new LoganModel();
                SendAction action = new SendAction();
                model.action = LoganModel.Action.SEND;
                action.date = String.valueOf(time);
                action.sendLogRunnable = runnable;
                model.sendAction = action;
                mCacheLogQueue.add(model);
                if (mLoganThread != null) {
                    mLoganThread.notifyRun();
                }
            }
        }
    }

    void flush() {
        if (TextUtils.isEmpty(mPath)) {
            return;
        }
        LoganModel model = new LoganModel();
        model.action = LoganModel.Action.FLUSH;
        mCacheLogQueue.add(model);
        if (mLoganThread != null) {
            mLoganThread.notifyRun();
        }
    }

    File getDir() {
        return new File(mPath);
    }

    private long getDateTime(String time) {
        long tempTime = 0;
        try {
            tempTime = dataFormat.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tempTime;
    }
}
