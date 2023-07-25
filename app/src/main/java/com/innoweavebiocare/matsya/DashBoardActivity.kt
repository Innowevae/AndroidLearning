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
// File Name: DashBoardActivity.kt
// File Description: this page contains the recycler
// view for graphview and buttons for showing the
// graph in those ranges. It also has a export button
// which exports the data in csv file.
// Author: Ritvik Sahu
// Date:May 2, 2023
/////////////////////////////////////////////////////

package com.innoweavebiocare.matsya

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.Entry
import com.google.android.gms.location.*
import com.innoweavebiocare.matsya.database.DatabaseHelper
import com.innoweavebiocare.matsya.database.model.sensorData
import com.innoweavebiocare.matsya.recyclerviewDataModel.GraphviewData
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.list_item_recyclerview_dashboard_activity.*
import kotlinx.android.synthetic.main.list_item_recyclerview_home_activity.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class DashBoardActivity : AppCompatActivity() {

    private val dbHandler: DatabaseHelper = DatabaseHelper(this)
    private lateinit var recviewGraph: RecyclerView
    private lateinit var graphList:ArrayList<GraphviewData>
    private lateinit var graphAdapter: GraphAdapterActivity

    //buttons
    private var tvToday: TextView? = null
    private var tv7d: TextView? = null
    private var tv30d: TextView? = null
    private var tv90d: TextView? = null
    private var tvExport: TextView? = null
    var clickedDeviceid =""
    var deviceName=""
    var timeCount = 0
    var epochTime = 0
    var granularityForXAxis = 0
    var flag = 0
    var currentTimeStamp : Long = System.currentTimeMillis() / 1000L
//    val TwentyFoursHoursInSec = 86400L
    val SevenDaysInSec = 604800L
    val ThirtyDaysInSec = 2592000L
    val NinetyDaysInSec = 7776000L
    var startTime = 0L

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        var deviceid = intent.getStringExtra("DEVICEID")
        if (deviceid != null) {
            clickedDeviceid = deviceid
        }
        var devicename=intent.getStringExtra("DEVICENAME")
        if (devicename != null) {
            deviceName = devicename
        }

        // It makes transparent navigation bar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        // set black color status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        setSupportActionBar(findViewById(R.id.my_toolbar1))

        val view: View = my_toolbar1.getChildAt(1)
        view.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        my_toolbar1.title=deviceName

        // code for recyclerView
        recviewGraph = findViewById(R.id.RecyclerViewGraph)
        graphList = ArrayList()
        graphAdapter = GraphAdapterActivity(this,graphList) // set Adapter
        recviewGraph.layoutManager = LinearLayoutManager(this)// setRecycler view Adapter
        recviewGraph.adapter = graphAdapter

        tvToday=findViewById(R.id.tvToday)
        tv7d=findViewById(R.id.tv7days)
        tv30d=findViewById(R.id.tv30days)
        tv90d=findViewById(R.id.tv90days)
        tvExport=findViewById(R.id.tvExport)

        tvToday?.setOnClickListener {
            tvToday?.setBackgroundResource(R.drawable.shape_rectangle_clicked)
            tv7d?.setBackgroundResource(R.drawable.shape_rectangle)
            tv30d?.setBackgroundResource(R.drawable.shape_rectangle)
            tv90d?.setBackgroundResource(R.drawable.shape_rectangle)
            tvExport?.setBackgroundResource(R.drawable.shape_rectangle)
            tvToday?.setTextColor(Color.parseColor("#196F88"))
            tv7d?.setTextColor(Color.parseColor("#000000"))
            tv30d?.setTextColor(Color.parseColor("#000000"))
            tv90d?.setTextColor(Color.parseColor("#000000"))
            tvExport?.setTextColor(Color.parseColor("#000000"))
            timeCount = 24
            epochTime = 3600
            granularityForXAxis = 1
            val epochStartOfDay = getEpochStartOfDay()
            flag = 1
            graphRecyclerviewData(epochStartOfDay, timeCount, epochTime, granularityForXAxis, flag)

        }
        tv7d?.setOnClickListener {
            tvToday?.setBackgroundResource(R.drawable.shape_rectangle)
            tv7d?.setBackgroundResource(R.drawable.shape_rectangle_clicked)
            tv30d?.setBackgroundResource(R.drawable.shape_rectangle)
            tv90d?.setBackgroundResource(R.drawable.shape_rectangle)
            tvExport?.setBackgroundResource(R.drawable.shape_rectangle)
            tvToday?.setTextColor(Color.parseColor("#000000"))
            tv7d?.setTextColor(Color.parseColor("#196F88"))
            tv30d?.setTextColor(Color.parseColor("#000000"))
            tv90d?.setTextColor(Color.parseColor("#000000"))
            tvExport?.setTextColor(Color.parseColor("#000000"))
            timeCount = 180
            epochTime = 3600
            granularityForXAxis = 1
            startTime = currentTimeStamp - SevenDaysInSec
            flag = 2
            graphRecyclerviewData(startTime, timeCount, epochTime, granularityForXAxis, flag)
            startTime = 0L

        }
        tv30d?.setOnClickListener {
            tvToday?.setBackgroundResource(R.drawable.shape_rectangle)
            tv7d?.setBackgroundResource(R.drawable.shape_rectangle)
            tv30d?.setBackgroundResource(R.drawable.shape_rectangle_clicked)
            tv90d?.setBackgroundResource(R.drawable.shape_rectangle)
            tvExport?.setBackgroundResource(R.drawable.shape_rectangle)
            tvToday?.setTextColor(Color.parseColor("#000000"))
            tv7d?.setTextColor(Color.parseColor("#000000"))
            tv30d?.setTextColor(Color.parseColor("#196F88"))
            tv90d?.setTextColor(Color.parseColor("#000000"))
            tvExport?.setTextColor(Color.parseColor("#000000"))
            timeCount = 120
            epochTime = 21600
            granularityForXAxis = 1
            startTime = currentTimeStamp - ThirtyDaysInSec
            flag = 3
            graphRecyclerviewData(startTime, timeCount, epochTime, granularityForXAxis, flag)
            startTime = 0L

        }
        tv90d?.setOnClickListener {
            tvToday?.setBackgroundResource(R.drawable.shape_rectangle)
            tv7d?.setBackgroundResource(R.drawable.shape_rectangle)
            tv30d?.setBackgroundResource(R.drawable.shape_rectangle)
            tv90d?.setBackgroundResource(R.drawable.shape_rectangle_clicked)
            tvExport?.setBackgroundResource(R.drawable.shape_rectangle)
            tvToday?.setTextColor(Color.parseColor("#000000"))
            tv7d?.setTextColor(Color.parseColor("#000000"))
            tv30d?.setTextColor(Color.parseColor("#000000"))
            tv90d?.setTextColor(Color.parseColor("#196F88"))
            tvExport?.setTextColor(Color.parseColor("#000000"))
            timeCount = 100
            epochTime = 86400
            granularityForXAxis = 1
            startTime = currentTimeStamp - NinetyDaysInSec
            flag = 4
            graphRecyclerviewData(startTime, timeCount, epochTime, granularityForXAxis, flag)
            startTime = 0L

        }
        tvExport?.setOnClickListener {
            tvToday?.setBackgroundResource(R.drawable.shape_rectangle)
            tv7d?.setBackgroundResource(R.drawable.shape_rectangle)
            tv30d?.setBackgroundResource(R.drawable.shape_rectangle)
            tv90d?.setBackgroundResource(R.drawable.shape_rectangle)
            tvExport?.setBackgroundResource(R.drawable.shape_rectangle_clicked)
            tvToday?.setTextColor(Color.parseColor("#000000"))
            tv7d?.setTextColor(Color.parseColor("#000000"))
            tv30d?.setTextColor(Color.parseColor("#000000"))
            tv90d?.setTextColor(Color.parseColor("#000000"))
            tvExport?.setTextColor(Color.parseColor("#196F88"))
            exportCsv()
            Handler().postDelayed({
                // Revert the color of tvExport back to the default
                tvExport?.setBackgroundResource(R.drawable.shape_rectangle)
                tvExport?.setTextColor(Color.parseColor("#000000"))
            }, 1000)
        }
        tvToday?.performClick()
    }

    fun getEpochStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis / 1000L
    }

    private val shf = SimpleDateFormat("HH")
    private fun getHourString(time: Long) : String = shf.format(time * 1000L)

    // This code is used to set or clear a specific flag on the window attributes
    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    fun exportSensorDataToCSV(fileName: String?): Uri? {
        val sensorDataList: List<sensorData> = dbHandler.getSensorData(clickedDeviceid, startTime, currentTimeStamp)
        if (sensorDataList == null || sensorDataList.isEmpty()) {
            return null
        }

        // create the CSV file header
        val header = "Temperature, pH, DO, TDS, Timestamp\n"

        // create the CSV file content
        val sb = StringBuilder()
        sb.append(header)
        val formatter = SimpleDateFormat("LLL dd", Locale.getDefault())
        for (data in sensorDataList) {
            sb.append(data.senseTemp).append(",")
            sb.append(data.sensePH).append(",")
            sb.append(data.senseDO).append(",")
            sb.append(data.senseTDS).append(",")
            sb.append(data.senseTimeStamp).append("\n")
        }

        // write the CSV file to the app's cache directory
        return try {
            val file = File(this.cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            outputStream.write(sb.toString().toByteArray())
            outputStream.close()

            // create a content URI for the file using FileProvider
            FileProvider.getUriForFile(this, this.packageName + ".fileprovider", file)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun exportCsv(){
        val sdf = SimpleDateFormat("yyyy_MM_dd")
        val currentTime = sdf.format(currentTimeStamp * 1000L)
        val uriString = exportSensorDataToCSV("SENSORDATA_${deviceName}_${currentTime}.csv")
        if(uriString != null){
            val uri = Uri.parse(uriString.toString())
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/csv"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(intent, "Share CSV file"))
        }else{
            Toast.makeText(this,"NO DATA IN GIVEN RANGE",Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun graphRecyclerviewData(
        startTime: Long,
        timeCount: Int,
        epochTime: Int,
        granularityForXAxis: Int,
        flag: Int
    ){

        val sdf = SimpleDateFormat("LLL dd, yyyy")
        val stf = SimpleDateFormat("HH:mm aaa")
        val sensorDatalistss =dbHandler.getLatestSensorData(clickedDeviceid)
        var latestDo = ((sensorDatalistss.senseDO * 100.0).roundToInt() / 100.0).toFloat().toString()
        var latestTemp = ((sensorDatalistss.senseTemp * 100.0).roundToInt() / 100.0).toFloat().toString()
        var latestPh = ((sensorDatalistss.sensePH * 100.0).roundToInt() / 100.0).toFloat().toString()
        var latestTds = ((sensorDatalistss.senseTDS * 100.0).roundToInt() / 100.0).toInt().toString()
        var latestTimeStamp = ((sensorDatalistss.senseTimeStamp))
        var latestDate = sdf.format(latestTimeStamp * 1000L)
        var latestTime = stf.format(latestTimeStamp * 1000L)

        var progressBarDo= 0F
        var progressBarTemp= 0F
        var progressBarPh= 0F
        var progressBarTds= 0F

        if(latestDo.toFloat() <= 50 && latestDo.toFloat() > 0) {
            progressBarDo=latestDo.toFloat()
        } else {
            latestDo="NA"
        }
        if(latestTemp.toFloat() > 0) {
            progressBarTemp=latestTemp.toFloat()
        } else {
            latestTemp="NA"
        }
        if(latestPh.toFloat() <= 14 && latestPh.toFloat() >0) {
            progressBarPh=latestPh.toFloat()
        }else {
            latestPh="NA"
        }
        if(latestTds.toFloat() < 100000 && latestTds.toFloat() > 0) {
            progressBarTds=latestTds.toFloat()
        } else {
            latestTds="NA"
        }

        var MinDoAndTime=dbHandler.getMinSensorDataWithTime(clickedDeviceid, sensorData.COLUMN_DO, startTime, currentTimeStamp)
        var MinTempAndTime=dbHandler.getMinSensorDataWithTime(clickedDeviceid,sensorData.COLUMN_TEMP, startTime, currentTimeStamp)
        var MinPhAndTime=dbHandler.getMinSensorDataWithTime(clickedDeviceid,sensorData.COLUMN_PH, startTime, currentTimeStamp)
        var MinTdsAndTime=dbHandler.getMinSensorDataWithTime(clickedDeviceid,sensorData.COLUMN_TDS, startTime, currentTimeStamp)
        var MaxDoAndTime=dbHandler.getMaxSensorDataWithTime(clickedDeviceid,sensorData.COLUMN_DO, startTime, currentTimeStamp)
        var MaxTempAndTime=dbHandler.getMaxSensorDataWithTime(clickedDeviceid,sensorData.COLUMN_TEMP, startTime, currentTimeStamp)
        var MaxPhAndTime=dbHandler.getMaxSensorDataWithTime(clickedDeviceid,sensorData.COLUMN_PH, startTime, currentTimeStamp)
        var MaxTdsAndTime=dbHandler.getMaxSensorDataWithTime(clickedDeviceid,sensorData.COLUMN_TDS, startTime, currentTimeStamp)
        var MinDateDo = sdf.format(MinDoAndTime.second * 1000L)
        var MinTimeDo = stf.format(MinDoAndTime.second * 1000L)
        var MinDateTemp = sdf.format(MinTempAndTime.second * 1000L)
        var MinTimeTemp = stf.format(MinTempAndTime.second * 1000L)
        var MinDatePh = sdf.format(MinPhAndTime.second * 1000L)
        var MinTimePh = stf.format(MinPhAndTime.second * 1000L)
        var MinDateTds = sdf.format(MinTdsAndTime.second * 1000L)
        var MinTimeTds = stf.format(MinTdsAndTime.second * 1000L)
        var MaxDateDo = sdf.format(MaxDoAndTime.second * 1000L)
        var MaxTimeDo = stf.format(MaxDoAndTime.second * 1000L)
        var MaxDateTemp = sdf.format(MaxTempAndTime.second * 1000L)
        var MaxTimeTemp = stf.format(MaxTempAndTime.second * 1000L)
        var MaxDatePh = sdf.format(MaxPhAndTime.second * 1000L)
        var MaxTimePh = stf.format(MaxPhAndTime.second * 1000L)
        var MaxDateTds = sdf.format(MaxTdsAndTime.second * 1000L)
        var MaxTimeTds = stf.format(MaxTdsAndTime.second * 1000L)
        var minDo =""
        var minTemp=""
        var minPh=""
        var minTds=""
        var maxDo =""
        var maxTemp=""
        var maxPh=""
        var maxTds=""

        //min Condition
        if(MinDoAndTime.first <= 0.0 || MinDoAndTime.first >= 100.0 ) {
            minDo="NA"
            MinDateDo="NA"
            MinTimeDo="NA"
        }
        else{
            minDo= ((MinDoAndTime.first * 100.0).roundToInt() / 100.0).toFloat().toString()
        }
        if(MinTempAndTime.first <= 0.0 || MinTempAndTime.first >= 100.0){
            minTemp="NA"
            MinDateTemp="NA"
            MinTimeTemp="NA"
        }else{
            minTemp=((MinTempAndTime.first * 100.0).roundToInt() / 100.0).toFloat().toString()
        }
        if(MinPhAndTime.first <= 0.0 || MinPhAndTime.first >= 14.0){
            minPh="NA"
            MinDatePh="NA"
            MinTimePh="NA"
        }else{
            minPh=((MinPhAndTime.first * 100.0).roundToInt() / 100.0).toFloat().toString()
        }
        if(MinTdsAndTime.first <= 0.0 || MinTdsAndTime.first >= 50000.0){
            minTds="NA"
            MinDateTds="NA"
            MinTimeTds="NA"
        }else{
            minTds=((MinTdsAndTime.first * 100.0).roundToInt() / 100.0).toFloat().toString()
        }

        //max condition
        if(MaxDoAndTime.first <= 0.0 || MaxDoAndTime.first >= 100.0 ) {
            maxDo="NA"
            MaxDateDo="NA"
            MaxTimeDo="NA"
        }else{
            maxDo=((MaxDoAndTime.first * 100.0).roundToInt() / 100.0).toFloat().toString()
        }
        if(MaxTempAndTime.first <= 0.0 || MaxTempAndTime.first >= 100.0){
            maxTemp="NA"
            MaxDateTemp="NA"
            MaxTimeTemp="NA"
        }else{
            maxTemp=((MaxTempAndTime.first * 100.0).roundToInt() / 100.0).toFloat().toString()
        }
        if(MaxPhAndTime.first <= 0.0 || MaxPhAndTime.first >= 14.0 ){
            maxPh="NA"
            MaxDatePh="NA"
            MaxTimePh="NA"
        }else{
            maxPh=((MaxPhAndTime.first * 100.0).roundToInt() / 100.0).toFloat().toString()
        }
        if(MaxTdsAndTime.first <= 0.0 || MaxTdsAndTime.first >= 100000.0){
            maxTds="NA"
            MaxDateTds="NA"
            MaxTimeTds="NA"
        }else{
            maxTds=((MaxTdsAndTime.first * 100.0).roundToInt() / 100.0).toFloat().toString()
        }

        // lineChart
        val currentTime = getHourString(currentTimeStamp)
        var doSensorEntries = ArrayList<Entry>()
        var tdsSensorEntries = ArrayList<Entry>()
        var tempSensorEntries = ArrayList<Entry>()
        var phSensorEntries = ArrayList<Entry>()
        var setAutoTdsYRange = 1000

        for (i in 1..timeCount) {
            val newTime = epochTime * i
            val stopTimeEpoch = startTime + newTime
            val startTimeEpoch = stopTimeEpoch - epochTime

            var doTotalCount = 0
            var tdsTotalCount = 0
            var tempTotalCount = 0
            var phTotalCount = 0

            var doSensorData = 0.0
            var tdsSensorData = 0.0
            var tempSensorData = 0.0
            var phSensorData = 0.0

            val sensorDataList = dbHandler.getSensorData(clickedDeviceid, startTimeEpoch, stopTimeEpoch)
            if (sensorDataList.size != 0) {
                for (j in 0 until sensorDataList.size) {

                    // DO sensor data
                    if (sensorDataList[j].senseDO != 0F) {
                        doSensorData += sensorDataList[j].senseDO
                        doTotalCount++
                    }

                    if (sensorDataList[j].senseTemp != 0F) {
                        tempSensorData += sensorDataList[j].senseTemp
                        tempTotalCount++
                    }

                    if (sensorDataList[j].senseTDS != 0F) {
                        tdsSensorData += sensorDataList[j].senseTDS
                        tdsTotalCount++
                    }

                    if (sensorDataList[j].sensePH != 0F) {
                        phSensorData += sensorDataList[j].sensePH
                        phTotalCount++
                    }
                }
            }

            var doAverageData = 0F
            var tdsAverageData = 0F
            var tempAverageData = 0F
            var phAverageData = 0F

            if (doTotalCount > 0) {
                doAverageData = (doSensorData / doTotalCount).toFloat()
            }

            if (tempTotalCount > 0) {
                tempAverageData = (tempSensorData / tempTotalCount).toFloat()
            }

            if (tdsTotalCount > 0) {
                tdsAverageData = (tdsSensorData / tdsTotalCount).toFloat()
            }

            if (phTotalCount > 0) {
                phAverageData = (phSensorData / phTotalCount).toFloat()
            }

            if (sensorDataList.isEmpty()) {
                doAverageData = 0F
                tempAverageData = 0F
                tdsAverageData = 0F
                phAverageData = 0F

            }

            if(flag == 1){
                if(i < currentTime.toInt()+1){
                    doSensorEntries.add(Entry(i.toFloat(), doAverageData))
                    tempSensorEntries.add(Entry(i.toFloat(), tempAverageData))
                    tdsSensorEntries.add(Entry(i.toFloat(), tdsAverageData))
                    phSensorEntries.add(Entry(i.toFloat(), phAverageData))
                }
            } else{
                doSensorEntries.add(Entry(i.toFloat(), doAverageData))
                tempSensorEntries.add(Entry(i.toFloat(), tempAverageData))
                tdsSensorEntries.add(Entry(i.toFloat(), tdsAverageData))
                phSensorEntries.add(Entry(i.toFloat(), phAverageData))
            }

            sensorDataList.clear()
        }

        // set auto y range of tds
        var largestNumber = tdsSensorEntries[0].y
        for (i in 1 until tdsSensorEntries.size) {
            val currentNumber = tdsSensorEntries[i].y
            if (currentNumber > largestNumber) {
                largestNumber = currentNumber
            }
        }
        if (largestNumber > 0 && largestNumber  < 1000) {
            setAutoTdsYRange = 1000
        } else if (largestNumber >= 1000 && largestNumber < 10000) {
            setAutoTdsYRange = 10000
        } else if (largestNumber >= 10000 && largestNumber < 50000) {
            setAutoTdsYRange = 50000
        } else if (largestNumber >= 50000 && largestNumber < 100000) {
            setAutoTdsYRange = 100000
        }


        graphList.clear()
        graphList.add(GraphviewData("Dissolved Oxygen", latestDo,progressBarDo,10,"mg/dl", minDo,"mg/dl",
            MinDateDo,MinTimeDo, maxDo, "mg/dl", MaxDateDo,MaxTimeDo,latestDate,latestTime,doSensorEntries,10, timeCount, granularityForXAxis, flag))
        graphList.add(GraphviewData("Temperature", latestTemp,progressBarTemp,60,"°C", minTemp,"°C",
            MinDateTemp,MinTimeTemp, maxTemp, "°C", MaxDateTemp,MaxTimeTemp,latestDate,latestTime,tempSensorEntries, 60,timeCount, granularityForXAxis, flag))
        graphList.add(GraphviewData("pH", latestPh,progressBarPh,14,"", minPh,"",
            MinDatePh,MinTimePh, maxPh, "", MaxDatePh,MaxTimePh,latestDate,latestTime,phSensorEntries,14,timeCount, granularityForXAxis, flag))
        graphList.add(GraphviewData("TDS", latestTds,progressBarTds,1000,"ppm", minTds,"ppm",
            MinDateTds,MinTimeTds, maxTds, "ppm", MaxDateTds,MaxTimeTds,latestDate,latestTime,tdsSensorEntries,setAutoTdsYRange,timeCount, granularityForXAxis, flag))
        graphAdapter.notifyDataSetChanged()

    }

    // Functioning for Action bar menu item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.calibration -> {
                val intent = Intent(this@DashBoardActivity, LoadingCalibrationActivity::class.java)
                intent.putExtra("macId", clickedDeviceid)
                startActivity(intent)
                finish()
            }
            R.id.wifiCredentials -> {
                val intent = Intent(this@DashBoardActivity, LoadingWifiScanActivity::class.java)
                intent.putExtra("macId", clickedDeviceid)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.item_menu_for_dashboard,menu)
        return true
    }

}