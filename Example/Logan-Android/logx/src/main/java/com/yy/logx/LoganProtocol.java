

package com.yy.logx;

class LoganProtocol implements LoganProtocolHandler {

    private static LoganProtocol sLoganProtocol;

    private LoganProtocolHandler mCurProtocol;
    private boolean mIsInit;
    private OnLoganProtocolStatus mLoganProtocolStatus;

    private LoganProtocol() {

    }

    public static LoganProtocol newInstance() {
        if (sLoganProtocol == null) {
            synchronized (LoganProtocol.class) {
                sLoganProtocol = new LoganProtocol();
            }
        }
        return sLoganProtocol;
    }

    @Override
    public void logan_flush() {
        if (mCurProtocol != null) {
            mCurProtocol.logan_flush();
        }
    }

    @Override
    public void logan_write(int flag, String log, long local_time, String thread_name, long thread_id, boolean
            is_main) {
        if (mCurProtocol != null) {
            mCurProtocol.logan_write(flag, log, local_time, thread_name, thread_id, is_main);
        }
    }

    @Override
    public void logan_open(String file_name) {
        if (mCurProtocol != null) {
            mCurProtocol.logan_open(file_name);
        }
    }

    @Override
    public void logan_init(String cache_path, String dir_path, int max_file, String encrypt_key_16, String
            encrypt_iv_16) {
        if (mIsInit) {
            return;
        }
        if (CLoganProtocol.isCloganSuccess()) {
            mCurProtocol = CLoganProtocol.newInstance();
            mCurProtocol.setOnLoganProtocolStatus(mLoganProtocolStatus);
            mCurProtocol.logan_init(cache_path, dir_path, max_file, encrypt_key_16, encrypt_iv_16);
            mIsInit = true;
        } else {
            mCurProtocol = null;
        }
    }

    @Override
    public void logan_debug(boolean debug) {
        if (mCurProtocol != null) {
            mCurProtocol.logan_debug(debug);
        }
    }

    @Override
    public void setOnLoganProtocolStatus(OnLoganProtocolStatus listener) {
        mLoganProtocolStatus = listener;
    }
}
