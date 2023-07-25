package com.innoweavebiocare.matsya.workers


import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.innoweavebiocare.matsya.database.DatabaseHelper
import java.text.SimpleDateFormat

private const val TAG = "sensorRangeValidationWorker"

class SensorRangeValidationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx,params) {
    private val dbHandler: DatabaseHelper = DatabaseHelper(ctx)
    val deviceId = dbHandler.allRegisteredDeviceIDs

    @SuppressLint("SimpleDateFormat")
    override fun doWork(): Result {
        val appContext = applicationContext

        for (i in 0 until deviceId.size) {
            val sensorDataList = ArrayList<String>()
            val latestData = dbHandler.getLatestSensorData(deviceId[i].id)
            val rangeList = dbHandler.allRangeNotifyData

            if(rangeList[0].min >= latestData.senseDO || latestData.senseDO >= rangeList[0].max){
                sensorDataList.add("DO: ${latestData.senseDO}")
            }
            if(rangeList[1].min >= latestData.sensePH || latestData.sensePH >= rangeList[1].max){
                sensorDataList.add("pH: ${latestData.sensePH}")
            }
            if(rangeList[2].min >= latestData.senseTDS || latestData.senseTDS >= rangeList[2].max){
                sensorDataList.add("TDS: ${latestData.senseTDS}")
            }
            if(rangeList[3].min >= latestData.senseTemp || latestData.senseTemp >= rangeList[3].max){
                sensorDataList.add("Temp: ${latestData.senseTemp}")
            }

            // Convert timeStamp to actual time and date
            val sdf = SimpleDateFormat("LLL dd") // Date format
            val stf = SimpleDateFormat("h:mm aaa") // Time format
            fun getDateString(time: Long) : String = sdf.format(time * 1000L)
            fun getTimeString(time: Long) : String = stf.format(time * 1000L)

            if(sensorDataList.isNotEmpty() && latestData.senseTimeStamp.toInt() != 0){
                makeNotificationwithId( "Alarm Start", i+1,
                    "${deviceId[i].pondName} : ${getDateString(latestData.senseTimeStamp)}, " +
                            getTimeString(latestData.senseTimeStamp), "$sensorDataList", appContext)
            }
        }
        // Task result
        return Result.success()
    }
}