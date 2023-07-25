package com.innoweavebiocare.matsya.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.amplifyframework.api.ApiException
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.api.rest.RestResponse
import com.amplifyframework.core.Amplify
import com.innoweavebiocare.matsya.database.DatabaseHelper
import com.innoweavebiocare.matsya.database.model.sensorData
import okio.internal.commonAsUtf8ToByteArray
import java.time.LocalDateTime


private const val TAG = "dataSyncWorker"
var currentTimeStamp = 0L
const val prevTimeStamp = 1640995200 // mid night Jan 1st 2022 timeStamp
const val secondsInNinetyDays = 7776000 // Seconds in 90 days
var newSensorData: sensorData? = null
var macId = ""

class dataSyncWorker(ctx: Context, params: WorkerParameters) : Worker(ctx,params){
    private val dbHandler:DatabaseHelper = DatabaseHelper(ctx)
    val deviceId = dbHandler.allRegisteredDeviceIDs
    override fun doWork(): Result {
        currentTimeStamp = System.currentTimeMillis() / 1000 // get current timeStamp
        return try {
            if(dbHandler.registeredDeviceCount != 0){
                for(i in 0 until  deviceId.size) {
                    macId = deviceId[i].id
                    var timeStamp = dbHandler.getLatestTimeStamp(deviceId[i].id)
                    val maxSyncTimeStamp = currentTimeStamp - secondsInNinetyDays // Sync only last 90 days data
                    if(timeStamp < maxSyncTimeStamp){
                         timeStamp = maxSyncTimeStamp
                    }
                    dbHandler.deletePastSensorData(maxSyncTimeStamp) // Delete any data older than ninety days

                    val option = RestOptions.builder()
                        .addPath("/apihandler")
                        .addBody(
                            ("{\"timeStamp\":" + timeStamp.toString() +
                                    ", \"macID\": \"" + macId + "\", " +
                                    "\"pass\": \"a2d4735b-f520-43d4-965d-9ec86b14ea9a\", " +
                                    "\"request\": \"getData\"}").commonAsUtf8ToByteArray()
                        )
                        .build()

                    Amplify.API.post(option,
                        { response ->
                           onSuccessRequest(response)
                        },
                        {
                            onFailureRequest(it)
                        })
                }
            }

            Log.i(TAG, "Successfully synced the database")
            Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error Syncing ")
            Result.failure()
        }
    }
    private fun onSuccessRequest(response: RestResponse) {
        val jsonObject = response.data.asJSONObject()
        val sensorDataJSONArray = jsonObject.getJSONArray("Items")
        val listSensorData = mutableListOf<sensorData>()
        for(i in 0 until sensorDataJSONArray.length()){
            val sensorDataJson = sensorDataJSONArray.getJSONObject(i).getJSONObject("payload");
            val deviceID:String = sensorDataJSONArray.getJSONObject(i).getString("DEVICE_ID");
            if(sensorDataJson.has("TEMP") &&
                sensorDataJson.has("PH") &&
                sensorDataJson.has("DO") &&
                sensorDataJson.has("TDS") &&
                sensorDataJson.has("timestamp"))
            {
                newSensorData = sensorData(
                    deviceID,
                    0.0f,
                    0.0f,
                    0.0f,
                    0.0f,
                    sensorDataJson.getLong("timestamp"),
                )
                Log.i(TAG, "$currentTimeStamp.......................................${sensorDataJson.getLong("timestamp")}")

                if(sensorDataJson.getLong("timestamp") > prevTimeStamp &&
                    sensorDataJson.getLong("timestamp") < currentTimeStamp) {

                    if (sensorDataJson.getDouble("TEMP").toFloat() < 100.0f &&
                        sensorDataJson.getDouble("TEMP").toFloat() > 0.0f) {
                        newSensorData?.setTemp(sensorDataJson.getDouble("TEMP").toFloat())
                    }else { Log.i("Error ", "Data range exceed of TEMP") }

                    if (sensorDataJson.getDouble("PH").toFloat() < 14.0f &&
                        sensorDataJson.getDouble("PH").toFloat() > 0.0f) {
                        newSensorData?.setPH(sensorDataJson.getDouble("PH").toFloat())
                    } else { Log.i("Error ", "Data range exceed of PH") }

                    if (sensorDataJson.getDouble("DO").toFloat() < 50.0f &&
                        sensorDataJson.getDouble("DO").toFloat() > 0.0f) {
                        newSensorData?.setDO(sensorDataJson.getDouble("DO").toFloat())
                    } else { Log.i("Error ", "Data range exceed of DO") }

                    if (sensorDataJson.getDouble("TDS").toFloat() < 100000 &&
                        sensorDataJson.getDouble("TDS").toFloat() > 0) {
                        newSensorData?.setTDS(sensorDataJson.getDouble("TDS").toFloat())
                    } else { Log.i("Error ", "Data range exceed of TDS") }

                    listSensorData.add(newSensorData!!)

                } else{ Log.i("Error ", "unable to upload data because of wrong timeStamp") }

                Log.i("MyAmplifyApp", "Data Inserted: ${newSensorData!!.senseTemp}, " +
                        "${newSensorData!!.sensePH}, ${newSensorData!!.senseDO}, " +
                        "${newSensorData!!.senseTDS}, ${newSensorData!!.senseTimeStamp} ")
            }
        }

        dbHandler.insertSensorData(listSensorData)

        Log.i("MyAmplifyApp", "POST succeeded sensorDataCount = : ${dbHandler.sensorDataCount}")
    }

    private fun onFailureRequest(apiException: ApiException) {
        Log.e("MyAmplifyApp", "POST failed", apiException)
    }
}