/////////////////////////////////////////////////////
//                                                 //
// Copyright 2022-2023                             //
// Notice: Property of Innoweave Biocare           //
// Any part of this code cannot be copied or       //
// redistributed without prior consent of          //
// Innoweave                                       //
//                                                 //
/////////////////////////////////////////////////////

/////////////////////////////////////////////////////
// File Name: ForegroundService.kt
// File Description: this Foreground service is stays
// alive even when the app is terminated. and give us
// sensor range notification and sync sensor data.
// Author: Anshul Malviya
// Date: May 31, 2023
/////////////////////////////////////////////////////

package com.innoweavebiocare.matsya

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.innoweavebiocare.matsya.database.DatabaseHelper
import com.innoweavebiocare.matsya.workers.SensorRangeValidationWorker
import com.innoweavebiocare.matsya.workers.dataSyncWorker
import java.util.concurrent.TimeUnit

class ForegroundService : Service() {

    private val CHANNEL_ID = "ForegroundServiceChannel"
    private val NOTIFICATION_ID = 2147483647

    @SuppressLint("RemoteViewLayout")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel() // It Make a notification channel if necessary

        val dbHandler: DatabaseHelper = DatabaseHelper(this)
        val devices = dbHandler.allRegisteredDeviceIDs

        // Sticky notification content
        val notificationLayout = RemoteViews(packageName, R.layout.custom_notification)
        notificationLayout.setTextViewText(R.id.notificationMessageD1, "${devices.size}") // Total pond connected
        notificationLayout.setTextViewText(R.id.notificationMessageD2, "YES") // Data syncing

        // Create an Intent for the activity you want to start
        val resultIntent = Intent(this, HomeActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_innoweave)
            .setContent(notificationLayout)
            .setContentIntent(resultPendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification) // Start foreground

        // Periodic DataSync WorkManager task here
        val dataSyncWorkerConstraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(false)
            .setRequiredNetworkType(NetworkType.CONNECTED) // Set the desired network type here
            .build()

        val dataSyncWorkRequest = PeriodicWorkRequest.Builder(dataSyncWorker::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(dataSyncWorkerConstraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(dataSyncWorkRequest)

        // Periodic work manager request for SensorRangeValidationWorker
        val sensorRangeValidationWorkerConstraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(false)
            .setRequiredNetworkType(NetworkType.CONNECTED) // Set the desired network type here
            .build()

        val sensorRangeValidationWorkRequest = PeriodicWorkRequest.Builder(SensorRangeValidationWorker::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(sensorRangeValidationWorkerConstraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(sensorRangeValidationWorkRequest)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}