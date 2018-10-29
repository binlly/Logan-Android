

package com.yy.logx;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class CLogxProtocol implements LogxProtocolHandler {

    private static final String LIBRARY_NAME = "logx";

    private static CLogxProtocol sCLogxProtocol;
    private static boolean sIsClogxOk;

    private boolean mIsLogxInit;
    private boolean mIsLogxOpen;
    private OnLogxProtocolStatus mLogxProtocolStatus;
    private Set<Integer> mArraySet = Collections.synchronizedSet(new HashSet<Integer>());

    static {
        try {
            if (!Util.loadLibrary(LIBRARY_NAME, CLogxProtocol.class)) {
                System.loadLibrary(LIBRARY_NAME);
            }
            sIsClogxOk = true;
        } catch (Throwable e) {
            e.printStackTrace();
            sIsClogxOk = false;
        }
    }

    public static boolean isClogxSuccess() {
        return sIsClogxOk;
    }

    public static CLogxProtocol newInstance() {
        if (sCLogxProtocol == null) {
            synchronized (CLogxProtocol.class) {
                if (sCLogxProtocol == null) {
                    sCLogxProtocol = new CLogxProtocol();
                }
            }
        }
        return sCLogxProtocol;
    }

    /**
     * 初始化Clogx
     *
     * @param dir_path 目录路径
     * @param max_file 最大文件值
     */
    private native int clogx_init(String cache_path, String dir_path, int max_file, String encrypt_key_16, String
            encrypt_iv_16);

    private native int clogx_open(String file_name);

    private native void clogx_debug(boolean is_debug);

    /**
     * @param flag        日志类型
     * @param log         日志内容
     * @param local_time  本地时间
     * @param thread_name 线程名称
     * @param thread_id   线程ID
     * @param is_main     是否主线程
     */
    private native int clogx_write(int flag, String log, long local_time, String thread_name, long thread_id, int
            is_main);

    private native void clogx_flush();

    @Override
    public void logx_init(String cache_path, String dir_path, int max_file, String encrypt_key_16, String
            encrypt_iv_16) {
        if (mIsLogxInit) {
            return;
        }
        if (!sIsClogxOk) {
            logxStatusCode(ConstantCode.ClogxStatus.CLOGX_LOAD_SO, ConstantCode.ClogxStatus.CLOGX_LOAD_SO_FAIL);
            return;
        }

        try {
            int code = clogx_init(cache_path, dir_path, max_file, encrypt_key_16, encrypt_iv_16);
            mIsLogxInit = true;
            logxStatusCode(ConstantCode.ClogxStatus.CLGOAN_INIT_STATUS, code);
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            logxStatusCode(ConstantCode.ClogxStatus.CLGOAN_INIT_STATUS, ConstantCode.ClogxStatus
                    .CLOGX_INIT_FAIL_JNI);
        }
    }

    @Override
    public void logx_debug(boolean debug) {
        if (!mIsLogxInit || !sIsClogxOk) {
            return;
        }
        try {
            clogx_debug(debug);
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOnLogxProtocolStatus(OnLogxProtocolStatus listener) {
        mLogxProtocolStatus = listener;
    }

    @Override
    public void logx_open(String file_name) {
        if (!mIsLogxInit || !sIsClogxOk) {
            return;
        }
        try {
            int code = clogx_open(file_name);
            mIsLogxOpen = true;
            logxStatusCode(ConstantCode.ClogxStatus.CLOGX_OPEN_STATUS, code);
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            logxStatusCode(ConstantCode.ClogxStatus.CLOGX_OPEN_STATUS, ConstantCode.ClogxStatus
                    .CLOGX_OPEN_FAIL_JNI);
        }
    }

    @Override
    public void logx_flush() {
        if (!mIsLogxOpen || !sIsClogxOk) {
            return;
        }
        try {
            clogx_flush();
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }

    }

    @Override
    public void logx_write(int flag, String log, long local_time, String thread_name, long thread_id, boolean
            is_main) {
        if (!mIsLogxOpen || !sIsClogxOk) {
            return;
        }
        try {
            int isMain = is_main ? 1 : 0;
            int code = clogx_write(flag, log, local_time, thread_name, thread_id, isMain);
            if (code != ConstantCode.ClogxStatus.CLOGX_WRITE_SUCCESS || Logx.sDebug) {
                logxStatusCode(ConstantCode.ClogxStatus.CLOGX_WRITE_STATUS, code);
            }
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            logxStatusCode(ConstantCode.ClogxStatus.CLOGX_WRITE_STATUS, ConstantCode.ClogxStatus
                    .CLOGX_WRITE_FAIL_JNI);
        }
    }

    private void logxStatusCode(String cmd, int code) {
        if (code < 0) {
            if (ConstantCode.ClogxStatus.CLOGX_WRITE_STATUS.endsWith(cmd) && code != ConstantCode.ClogxStatus
                    .CLOGX_WRITE_FAIL_JNI) {
                if (mArraySet.contains(code)) {
                    return;
                } else {
                    mArraySet.add(code);
                }
            }
            if (mLogxProtocolStatus != null) {
                mLogxProtocolStatus.logxProtocolStatus(cmd, code);
            }
        }
    }
}
