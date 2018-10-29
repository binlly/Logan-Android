

package com.yy.logx;

public interface LogxProtocolHandler {

    void logx_flush();

    void logx_write(int flag, String log, long local_time, String thread_name,
            long thread_id, boolean is_main);

    void logx_open(String file_name);

    void logx_init(String cache_path, String dir_path, int max_file, String encrypt_key_16,
            String encrypt_iv_16);

    void logx_debug(boolean debug);

    void setOnLogxProtocolStatus(OnLogxProtocolStatus listener);
}
