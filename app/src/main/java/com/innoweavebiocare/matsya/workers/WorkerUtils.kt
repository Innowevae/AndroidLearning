package com.innoweavebiocare.matsya.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.innoweavebiocare.matsya.R

private const val TAG = "WorkerUtils"
private val VERBOSE_NOTIFICATION_CHANNEL_NAME : CharSequence = "Verbose WorkManager Notifications"
private const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications whenever work starts"
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
private val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"
private const val NOTIFICATION_ID = 1
private const val DELAY_TIME_MILLIS: Long = 3000

/**
 * Create a Notification that is shown as a heads-up notification if possible.
 *
 * @param notifyTitle notifyTitle shown notification title
 * @param message Message shown on the notification
 * @param context Context needed to create Toast
 */
@SuppressLint("RestrictedApi")

fun makeNotificationwithId(groupKey: String, notifyID: Int, notifyTitle: String,
                           notifyMessage: String, context: Context){

    // Make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
        val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description

        // Add the channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.createNotificationChannel(channel)
    }

    // Create the notification
    val newMessageNotification1 = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_innoweave)
        .setContentTitle(notifyTitle)
        .setContentText(notifyMessage)
        .setGroup(groupKey)
        .build()

    val summaryNotification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(notifyTitle)
        // Set content text to support devices running API level < 24.
        .setSmallIcon(R.drawable.ic_innoweave)
        // Specify which group this notification belongs to.
        .setGroup(groupKey)
        // Set this notification as the summary for the group.
        .setGroupSummary(true)
        .build()

    NotificationManagerCompat.from(context).apply {
        notify(0, summaryNotification)
        notify(notifyID, newMessageNotification1)
    }
}

/**
 * Method for sleeping for a fixed amount of time to emulate slower work
 */
fun sleep() {
    try {
        Thread.sleep(DELAY_TIME_MILLIS, 0)
    } catch (e: InterruptedException) {
        Log.e(TAG, e.message.toString())
    }
}
