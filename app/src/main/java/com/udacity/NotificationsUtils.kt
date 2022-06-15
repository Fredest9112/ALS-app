package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(message: String, context: Context) {

    val contentIntent = Intent(context, MainActivity::class.java)

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
        .setContentTitle("Test")
        .setContentText(message)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(NOTIFICATION_ID, builder.build())
}