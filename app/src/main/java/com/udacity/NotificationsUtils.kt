package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

const val NOTIFICATION_ID = 0
const val FILE_STATUS = "status"
const val FILE_NAME = "filename"

fun NotificationManager.sendNotification(message: String, filename: String, context: Context) {

    val contentIntent = Intent(context, DetailActivity::class.java)
    contentIntent.putExtra(FILE_STATUS, message)
    contentIntent.putExtra(FILE_NAME, filename)

    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.download_notification_channel_id)
    ).setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(message)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(NOTIFICATION_ID, builder.build())
}