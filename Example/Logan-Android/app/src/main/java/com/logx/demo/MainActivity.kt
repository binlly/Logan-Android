package com.logx.demo

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.yy.logx.Logx
import com.yy.logx.LogxConfig
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by binlly on 2018/10/29-16:16
 * @author binlly
 */

class MainActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()
    }

    private fun initData() {
        val config = LogxConfig.Builder()
            .setCachePath(applicationContext.filesDir.absolutePath)
            .setPath(getExternalFilesDir(FILE_NAME)?.absolutePath)
            .setEncryptKey16("0123456789012345".toByteArray())
            .setEncryptIV16("0123456789012345".toByteArray())
            .build()
        Logx.init(config)
        Logx.setDebug(true)
        Logx.setOnLogxProtocolStatus { cmd, code -> Log.d(TAG, "clogx > cmd : $cmd | code : $code") }
    }

    private fun initView() {
        button_write.setOnClickListener { logxTest() }
        button_read.setOnClickListener { logxFilesInfo() }
    }

    private fun logxTest() {
        Thread {
            try {
                for (i in 0..10) {
                    Log.d(TAG, "times : $i")
                    Logx.w("Test ${System.currentTimeMillis()} - $i", i % 3)
                    Thread.sleep(5)
                }
                Log.d(TAG, "write log end")
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun logxFilesInfo() {
        val map = Logx.getAllFilesInfo()
        if (map != null) {
            val info = StringBuilder()
            for ((key, value) in map) {
                info.append("文件日期：")
                    .append(key)
                    .append("  文件大小（bytes）：")
                    .append(value)
                    .append("\n")
            }
            text_info.text = info.toString()
        }
    }

    companion object {
        private const val FILE_NAME = "logx"
        private val TAG = MainActivity::class.java.name
    }
}
