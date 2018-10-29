

package com.yy.logx;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LogxTest {
    private static final String TAG = LogxTest.class.getName();
    private static final String FILE_NAME = "logx" + "_vtest";

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private CountDownLatch mLatch;

    @Before
    public void setUp() throws Exception {
        mLatch = new CountDownLatch(1);
    }

    @Test
    public void test001Init() {
        Context applicationContext = InstrumentationRegistry.getTargetContext();
        LogxConfig config = new LogxConfig.Builder()
                .setCachePath(applicationContext.getFilesDir().getAbsolutePath())
                .setPath(applicationContext.getExternalFilesDir(null).getAbsolutePath()
                        + File.separator + FILE_NAME)
                .setEncryptKey16("0123456789012345".getBytes())
                .setEncryptIV16("0123456789012345".getBytes())
                .build();
        Logx.init(config);
        Logx.setDebug(true);
        Logx.setOnLogxProtocolStatus(new OnLogxProtocolStatus() {
            @Override
            public void logxProtocolStatus(String cmd, int code) {
                Log.d(TAG, "clogx > cmd : " + cmd + " | " + "code : " + code);
            }
        });
    }

    @Test
    public void test002LogxW() throws InterruptedException {
        Logx.w("LogX junit test write function", 1);
        assertWriteLog();
    }

    @Test
    public void test003LogxF() {
        Logx.f();
    }

    @Test
    public void test004LogxS() {
        SendLogRunnable sendLogRunnable = new SendLogRunnable() {
            @Override
            public void sendLog(File logFile) {

            }
        };
        Logx.s(getTodayDate(), sendLogRunnable);
    }

    @Test
    public void test005LogxFilesInfo() {
        Map<String, Long> map = Logx.getAllFilesInfo();
        if (map != null) {
            StringBuilder info = new StringBuilder();
            for (Map.Entry<String, Long> entry : map.entrySet()) {
                info.append("文件日期：").append(entry.getKey()).append("  文件大小（bytes）：").append(
                        entry.getValue()).append("\n");
            }
            Log.d(TAG, info.toString());
        }
    }

    // Functions

    private String[] getTodayDate() {
        String d = mDateFormat.format(new Date(System.currentTimeMillis()));
        String[] temp = new String[1];
        temp[0] = d;
        return temp;
    }

    private void assertWriteLog() throws InterruptedException {
        final int[] statusCode = new int[1];
        Logx.setOnLogxProtocolStatus(new OnLogxProtocolStatus() {
            @Override
            public void logxProtocolStatus(String cmd, int code) {
                statusCode[0] = code;
                if (cmd.equals(ConstantCode.ClogxStatus.CLOGX_WRITE_STATUS)) {
                    mLatch.countDown();
                    assertEquals(ConstantCode.ClogxStatus.CLOGX_WRITE_SUCCESS, code);
                }
            }
        });
        mLatch.await(2333, TimeUnit.MILLISECONDS);
        assertEquals("write状态码", ConstantCode.ClogxStatus.CLOGX_WRITE_SUCCESS, statusCode[0]);
    }
}
