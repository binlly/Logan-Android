

package com.yy.logx;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Logx {

    private static OnLogxProtocolStatus sLogxProtocolStatus;
    private static LogxControlCenter sLogxControlCenter;
    static boolean sDebug = false;

    /**
     * @brief Logx初始化
     */
    public static void init(LogxConfig logxConfig) {
        sLogxControlCenter = LogxControlCenter.instance(logxConfig);
    }

    /**
     * @param log  表示日志内容
     * @param type 表示日志类型
     * @brief Logx写入日志
     */
    public static void w(String log, int type) {
        sLogxControlCenter.write(log, type);
        sLogxControlCenter.flush();
    }

    /**
     * @brief 立即写入日志文件
     */
    public static void f() {
        sLogxControlCenter.flush();
    }

    /**
     * @param dates    日期数组，格式：“2018-07-27”
     * @param runnable 发送操作
     * @brief 发送日志
     */
    public static void s(String dates[], SendLogRunnable runnable) {
        sLogxControlCenter.send(dates, runnable);
    }

    /**
     * @brief 返回所有日志文件信息
     */
    public static Map<String, Long> getAllFilesInfo() {
        File dir = sLogxControlCenter.getDir();
        if (!dir.exists()) {
            return null;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return null;
        }
        Map<String, Long> allFilesInfo = new HashMap<>();
        for (File file : files) {
            try {
                allFilesInfo.put(Util.getDateStr(Long.parseLong(file.getName())), file.length());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return allFilesInfo;
    }

    /**
     * @brief Logx Debug开关
     */
    public static void setDebug(boolean debug) {
        Logx.sDebug = debug;
    }

    static void onListenerLogWriteStatus(String name, int status) {
        if (sLogxProtocolStatus != null) {
            sLogxProtocolStatus.logxProtocolStatus(name, status);
        }
    }

    public static void setOnLogxProtocolStatus(OnLogxProtocolStatus listener) {
        sLogxProtocolStatus = listener;
    }
}
