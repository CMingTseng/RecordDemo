package com.angcyo.record

import android.os.Bundle
import android.os.SystemClock
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    val rRecord: RRecord by lazy {
        RRecord(application, "/sdcard/_record_test/")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "...stopPlayback", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            rRecord.stopPlayback()
        }

        play.setOnClickListener { view ->
            Snackbar.make(view, "开始回放", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            rRecord.sampleFile?.let {
                if (it.exists()) {
                    rRecord.startPlayback(it.absolutePath, 0f)
                }
            }
        }

        rRecord.setOnRecordListener(object : RRecord.OnRecordListener() {
            override fun onStateChanged(state: Int) {
                super.onStateChanged(state)
                text_view1.text = "状态:->$state"
            }

            override fun onPlayProgress(time: Int, progress: Float) {
                super.onPlayProgress(time, progress)
                text_view2.text = "播放进度:->$time   $progress\n ${rRecord.playFilePath}"
            }

            override fun onRecordProgress(time: Int) {
                super.onRecordProgress(time)
                text_view2.text = "录制时间:->$time \n ${rRecord.sampleFile.absolutePath}"
            }
        })

        record.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    rRecord.startRecord("${SystemClock.elapsedRealtime()}_test")
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    rRecord.stopRecord()
                }
            }
            record.text = "${event.actionMasked}\n按住录音"
            true
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO),
            999
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        rRecord.release()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
