

package test.logan.dianping.com.logan;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dianping.logan.Logan;
import com.dianping.logan.LoganConfig;
import com.dianping.logan.OnLoganProtocolStatus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class MainActivity extends Activity {

    private static final String FILE_NAME = "logan_v1";
    private static final String TAG = MainActivity.class.getName();

    private TextView mTvInfo;
    private EditText mEditIp;
    private RealSendLogRunnable mSendLogRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        LoganConfig config = new LoganConfig.Builder().setCachePath(getApplicationContext().getFilesDir()
                .getAbsolutePath())
                .setPath(getApplicationContext().getExternalFilesDir(null)
                        .getAbsolutePath() + File.separator + FILE_NAME)
                .setEncryptKey16("0123456789012345".getBytes())
                .setEncryptIV16("0123456789012345".getBytes())
                .build();
        Logan.init(config);
        Logan.setDebug(true);
        Logan.setOnLoganProtocolStatus(new OnLoganProtocolStatus() {
            @Override
            public void loganProtocolStatus(String cmd, int code) {
                Log.d(TAG, "clogan > cmd : " + cmd + " | " + "code : " + code);
            }
        });
        mSendLogRunnable = new RealSendLogRunnable();
    }

    private void initView() {
        Button button = (Button) findViewById(R.id.write_btn);
        Button sendBtn = (Button) findViewById(R.id.send_btn);
        Button logFileBtn = (Button) findViewById(R.id.show_log_file_btn);
        mTvInfo = (TextView) findViewById(R.id.info);
        mEditIp = (EditText) findViewById(R.id.send_ip);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loganTest();
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loganSend();
            }
        });
        logFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loganFilesInfo();
            }
        });
    }

    private void loganTest() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    for (int i = 0; i < 900; i++) {
                        Log.d(TAG, "times : " + i);
                        Logan.w("2018-10-16 11:38:04.365 26912-26931/test.logan.dianping.com.logan W/nping.com.loga: " +
                                "" + "Accessing hidden method Ljava/lang/Runtime;->loadLibrary0" +
                                "(Ljava/lang/ClassLoader;" + "Ljava/lang/String;)V (light greylist, reflection)", i %
                                3);
                        Thread.sleep(5);
                    }
                    Log.d(TAG, "write log end");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void loganSend() {
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
        String d = dataFormat.format(new Date(System.currentTimeMillis()));
        String[] temp = new String[1];
        temp[0] = d;
        String ip = mEditIp.getText().toString().trim();
        if (!TextUtils.isEmpty(ip)) {
            mSendLogRunnable.setIp(ip);
        }
        Logan.s(temp, mSendLogRunnable);
    }

    private void loganFilesInfo() {
        Map<String, Long> map = Logan.getAllFilesInfo();
        if (map != null) {
            StringBuilder info = new StringBuilder();
            for (Map.Entry<String, Long> entry : map.entrySet()) {
                info.append("文件日期：")
                        .append(entry.getKey())
                        .append("  文件大小（bytes）：")
                        .append(entry.getValue())
                        .append("\n");
            }
            mTvInfo.setText(info.toString());
        }
    }
}
