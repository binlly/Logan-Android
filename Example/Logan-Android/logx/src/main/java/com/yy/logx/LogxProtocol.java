

package com.yy.logx;

class LogxProtocol implements LogxProtocolHandler {

    private static LogxProtocol sLogxProtocol;

    private LogxProtocolHandler mCurProtocol;
    private boolean mIsInit;
    private OnLogxProtocolStatus mLogxProtocolStatus;

    private LogxProtocol() {

    }

    public static LogxProtocol newInstance() {
        if (sLogxProtocol == null) {
            synchronized (LogxProtocol.class) {
                sLogxProtocol = new LogxProtocol();
            }
        }
        return sLogxProtocol;
    }

    @Override
    public void logx_flush() {
        if (mCurProtocol != null) {
            mCurProtocol.logx_flush();
        }
    }

    @Override
    public void logx_write(int flag, String log, long local_time, String thread_name, long thread_id, boolean
            is_main) {
        if (mCurProtocol != null) {
            mCurProtocol.logx_write(flag, log, local_time, thread_name, thread_id, is_main);
        }
    }

    @Override
    public void logx_open(String file_name) {
        if (mCurProtocol != null) {
            mCurProtocol.logx_open(file_name);
        }
    }

    @Override
    public void logx_init(String cache_path, String dir_path, int max_file, String encrypt_key_16, String
            encrypt_iv_16) {
        if (mIsInit) {
            return;
        }
        if (CLogxProtocol.isClogxSuccess()) {
            mCurProtocol = CLogxProtocol.newInstance();
            mCurProtocol.setOnLogxProtocolStatus(mLogxProtocolStatus);
            mCurProtocol.logx_init(cache_path, dir_path, max_file, encrypt_key_16, encrypt_iv_16);
            mIsInit = true;
        } else {
            mCurProtocol = null;
        }
    }

    @Override
    public void logx_debug(boolean debug) {
        if (mCurProtocol != null) {
            mCurProtocol.logx_debug(debug);
        }
    }

    @Override
    public void setOnLogxProtocolStatus(OnLogxProtocolStatus listener) {
        mLogxProtocolStatus = listener;
    }
}
