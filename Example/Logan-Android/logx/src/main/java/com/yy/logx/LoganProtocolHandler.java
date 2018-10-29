

package com.yy.logx;

public interface LoganProtocolHandler {

    void logan_flush();

    void logan_write(int flag, String log, long local_time, String thread_name,
            long thread_id, boolean is_main);

    void logan_open(String file_name);

    void logan_init(String cache_path, String dir_path, int max_file, String encrypt_key_16,
            String encrypt_iv_16);

    void logan_debug(boolean debug);

    void setOnLoganProtocolStatus(OnLoganProtocolStatus listener);
}
