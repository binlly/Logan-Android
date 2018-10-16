

package com.dianping.logan;

import android.text.TextUtils;

public class LoganConfig {

    private static final long DAYS = 24 * 60 * 60 * 1000; //天
    private static final long M = 1024 * 1024; //M
    private static final long DEFAULT_DAY = 3 * DAYS; //默认删除天数
    private static final long DEFAULT_FILE_SIZE = 1 * M;
    private static final long DEFAULT_MIN_SDCARD_SIZE = 50 * M; //最小的SD卡小于这个大小不写入
    private static final int DEFAULT_QUEUE = 500;

    String mCachePath; //mmap缓存路径
    String mPathPath; //file文件路径

    long mMaxFile = DEFAULT_FILE_SIZE; //删除文件最大值
    long mDay = DEFAULT_DAY; //删除天数
    long mMaxQueue = DEFAULT_QUEUE;
    long mMinSDCard = DEFAULT_MIN_SDCARD_SIZE; //最小sdk卡大小

    byte[] mEncryptKey16; //16位ase加密Key
    byte[] mEncryptIv16; //16位aes加密IV

    boolean isValid() {
        boolean valid = false;
        if (!TextUtils.isEmpty(mCachePath) && !TextUtils.isEmpty(mPathPath) && mEncryptKey16 != null && mEncryptIv16
                != null) {
            valid = true;
        }
        return valid;
    }

    LoganConfig() {

    }

    void setCachePath(String cachePath) {
        mCachePath = cachePath;
    }

    void setPathPath(String pathPath) {
        mPathPath = pathPath;
    }

    void setMaxFile(long maxFile) {
        mMaxFile = maxFile;
    }

    void setDay(long day) {
        mDay = day;
    }

    void setMinSDCard(long minSDCard) {
        mMinSDCard = minSDCard;
    }

    void setEncryptKey16(byte[] encryptKey16) {
        mEncryptKey16 = encryptKey16;
    }

    void setEncryptIV16(byte[] encryptIv16) {
        mEncryptIv16 = encryptIv16;
    }

    public static final class Builder {
        String mCachePath; //mmap缓存路径
        String mPath; //file文件路径
        long mMaxFile = DEFAULT_FILE_SIZE; //删除文件最大值
        long mDay = DEFAULT_DAY; //删除天数
        byte[] mEncryptKey16; //16位ase加密Key
        byte[] mEncryptIv16; //16位aes加密IV
        long mMinSDCard = DEFAULT_MIN_SDCARD_SIZE;

        public Builder setCachePath(String cachePath) {
            mCachePath = cachePath;
            return this;
        }

        public Builder setPath(String path) {
            mPath = path;
            return this;
        }

        public Builder setMaxFile(long maxFile) {
            mMaxFile = maxFile * M;
            return this;
        }

        public Builder setDay(long day) {
            mDay = day * DAYS;
            return this;
        }

        public Builder setEncryptKey16(byte[] encryptKey16) {
            mEncryptKey16 = encryptKey16;
            return this;
        }

        public Builder setEncryptIV16(byte[] encryptIv16) {
            mEncryptIv16 = encryptIv16;
            return this;
        }

        public Builder setMinSDCard(long minSDCard) {
            this.mMinSDCard = minSDCard;
            return this;
        }

        public LoganConfig build() {
            LoganConfig config = new LoganConfig();
            config.setCachePath(mCachePath);
            config.setPathPath(mPath);
            config.setMaxFile(mMaxFile);
            config.setMinSDCard(mMinSDCard);
            config.setDay(mDay);
            config.setEncryptKey16(mEncryptKey16);
            config.setEncryptIV16(mEncryptIv16);
            return config;
        }
    }
}
