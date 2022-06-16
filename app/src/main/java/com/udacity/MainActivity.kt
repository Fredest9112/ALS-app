package com.udacity

import android.app.DownloadManager
import android.app.DownloadManager.COLUMN_STATUS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var url = ""
    private var filename = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        custom_button.setOnClickListener {
            when {
                url.isEmpty() -> {
                    Toast.makeText(this, getString(R.string.no_selection), Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    custom_button.buttonState = ButtonState.Loading
                    download()
                }
            }
        }

        glideRB.setOnClickListener {
            url = GLIDE
            filename = glideRB.text.toString()
        }
        loadappRB.setOnClickListener {
            url = LOAD_APP
            filename = loadappRB.text.toString()
        }
        retrofitRB.setOnClickListener {
            url = RETROFIT
            filename = retrofitRB.text.toString()
        }

        createChannel(
            getString(R.string.download_notification_channel_id),
            getString(R.string.download_notification_channel_name)
        )
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)
            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    //Got some help from
    //https://github.com/jiangxiaocn/LoadApp/blob/main/starter/app/src/main/java/com/udacity/MainActivity.kt
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {

            val action = intent?.action
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager
            if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val cursor = downloadManager.query(
                    DownloadManager
                        .Query()
                        .setFilterById(downloadID)
                )
                if (cursor.moveToFirst() && cursor.count > 0) {
                    val status = cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        custom_button.buttonState = ButtonState.Completed
                        notificationManager.sendNotification(
                            context.getString(R.string.download_success),
                            filename,
                            context
                        )
                    } else {
                        custom_button.buttonState = ButtonState.Completed
                        notificationManager.sendNotification(
                            context.getString(R.string.download_failure),
                            filename,
                            context
                        )
                    }
                } else {
                    custom_button.buttonState = ButtonState.Completed
                    notificationManager.sendNotification(
                        context.getString(R.string.download_failure),
                        filename,
                        context
                    )
                }
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val LOAD_APP =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val RETROFIT =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
    }

}
