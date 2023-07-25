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
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.google.android.gms.location.*
import com.innoweavebiocare.matsya.database.DatabaseHelper
import com.innoweavebiocare.matsya.database.model.sensorData
import com.innoweavebiocare.matsya.recyclerviewDataModel.GraphviewData
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.*
import com.jjoe64.graphview.series.LineGraphSeries
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
    var flag = 0
    var currentTimeStamp : Long = System.currentTimeMillis() / 1000L
    val TwentyFoursHoursInSec = 86400L
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
            flag = 1
            startTime = currentTimeStamp - TwentyFoursHoursInSec
            graphRecyclerviewData(startTime, flag)
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
            flag = 0
            startTime = currentTimeStamp - SevenDaysInSec
            graphRecyclerviewData(startTime,flag)

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
            flag = 0
            startTime = currentTimeStamp - ThirtyDaysInSec
            graphRecyclerviewData(startTime,flag)

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
            flag = 0
            startTime = currentTimeStamp - NinetyDaysInSec
            graphRecyclerviewData(startTime,flag)

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
    fun graphRecyclerviewData(startTime: Long, flag:Int){
        val seriesDo = LineGraphSeries<DataPoint>()
        val seriesTemp = LineGraphSeries<DataPoint>()
        val seriesPh = LineGraphSeries<DataPoint>()
        val seriesTds = LineGraphSeries<DataPoint>()
        seriesDo.setThickness(4)
        seriesDo.color = Color.parseColor("#196F88")
        seriesTemp.setThickness(4)
        seriesTemp.color = Color.parseColor("#196F88")
        seriesPh.setThickness(4)
        seriesPh.color = Color.parseColor("#196F88")
        seriesTds.setThickness(4)
        seriesTds.color = Color.parseColor("#196F88")
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
        if(latestTds.toFloat() < 1000 && latestTds.toFloat() > 0) {
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

        var sensorDataList = dbHandler.getSensorData(clickedDeviceid, startTime, currentTimeStamp)
        sensorDataList.sortBy { it.getSenseTimeStamp() }

        var i = 0
        while (i < sensorDataList.size - 8) { // window size 9 for smoothing of graph
            val sumDo = sensorDataList[i].senseDO + sensorDataList[i + 1].senseDO + sensorDataList[i + 2].senseDO + sensorDataList[i + 3].senseDO + sensorDataList[i + 4].senseDO +
                    sensorDataList[i + 5].senseDO + sensorDataList[i + 6].senseDO + sensorDataList[i + 7].senseDO + sensorDataList[i + 8].senseDO
            val sumTemp = sensorDataList[i].senseTemp + sensorDataList[i + 1].senseTemp + sensorDataList[i + 2].senseTemp + sensorDataList[i + 3].senseTemp + sensorDataList[i + 4].senseTemp +
                    sensorDataList[i + 5].senseTemp + sensorDataList[i + 6].senseTemp + sensorDataList[i + 7].senseTemp + sensorDataList[i + 8].senseTemp
            val sumPh = sensorDataList[i].sensePH + sensorDataList[i + 1].sensePH + sensorDataList[i + 2].sensePH + sensorDataList[i + 3].sensePH + sensorDataList[i + 4].sensePH +
                    sensorDataList[i + 5].sensePH + sensorDataList[i + 6].sensePH + sensorDataList[i + 7].sensePH + sensorDataList[i + 8].sensePH
            val sumTds = sensorDataList[i].senseTDS + sensorDataList[i + 1].senseTDS + sensorDataList[i + 2].senseTDS + sensorDataList[i + 3].senseTDS + sensorDataList[i + 4].senseTDS +
                    sensorDataList[i + 5].senseTDS + sensorDataList[i + 6].senseTDS + sensorDataList[i + 7].senseTDS + sensorDataList[i + 8].senseTDS
            val averageDo = sumDo / 9.0
            val averageTemp = sumTemp / 9.0
            val averagePh = sumPh / 9.0
            val averageTds = sumTds / 9.0

            if (averageDo <= 50) { // Skip values greater than 50
                val dataPoint = DataPoint(sensorDataList[i].getSenseTimeStamp().toDouble(), averageDo)
                seriesDo.appendData(dataPoint, true, sensorDataList.size)

                // Check if there is a gap in the data and add null data points
                val timeDiff = sensorDataList[i + 8].getSenseTimeStamp() - sensorDataList[i].getSenseTimeStamp() // time difference between ninth data point and first data point in the current batch
                if (timeDiff > 600) { // If time gap is greater than 10 minutes (600 seconds)
                    val nanDataPoint = DataPoint(sensorDataList[i].getSenseTimeStamp().toDouble() + 1, Double.NaN)
                    seriesDo.appendData(nanDataPoint, true, sensorDataList.size)
                }
            }
            if(averageTemp > 0){ //skip values less than 0
                val dataPoint = DataPoint(sensorDataList[i].getSenseTimeStamp().toDouble(), averageTemp)
                seriesTemp.appendData(dataPoint, true, sensorDataList.size)
                // Check if there is a gap in the data and add null data points
                val timeDiff = sensorDataList[i + 8].getSenseTimeStamp() - sensorDataList[i].getSenseTimeStamp() // time difference between ninth data point and first data point in the current batch
                if (timeDiff > 600) { // If time gap is greater than 10 minutes (600 seconds)
                    val nanDataPoint = DataPoint(sensorDataList[i].getSenseTimeStamp().toDouble() + 1, Double.NaN)
                    seriesTemp.appendData(nanDataPoint, true, sensorDataList.size)
                }
            }
            if(averagePh <= 14){ //skip values greater than 14
                val dataPoint = DataPoint(sensorDataList[i].getSenseTimeStamp().toDouble(), averagePh)
                seriesPh.appendData(dataPoint, true, sensorDataList.size)
                // Check if there is a gap in the data and add null data points
                val timeDiff = sensorDataList[i + 8].getSenseTimeStamp() - sensorDataList[i].getSenseTimeStamp() // time difference between ninth data point and first data point in the current batch
                if (timeDiff > 600) { // If time gap is greater than 10 minutes (600 seconds)
                    val nanDataPoint = DataPoint(sensorDataList[i].getSenseTimeStamp().toDouble() + 1, Double.NaN)
                    seriesPh.appendData(nanDataPoint, true, sensorDataList.size)
                }
            }
            if(averageTds <= 50000){ //skip value greater than 50000
                val dataPoint = DataPoint(sensorDataList[i].getSenseTimeStamp().toDouble(), averageTds)
                seriesTds.appendData(dataPoint, true, sensorDataList.size)
                // Check if there is a gap in the data and add null data points
                val timeDiff = sensorDataList[i + 8].getSenseTimeStamp() - sensorDataList[i].getSenseTimeStamp() // time difference between ninth data point and first data point in the current batch
                if (timeDiff > 600) { // If time gap is greater than 10 minutes (600 seconds)
                    val nanDataPoint = DataPoint(sensorDataList[i].getSenseTimeStamp().toDouble() + 1, Double.NaN)
                    seriesTds.appendData(nanDataPoint, true, sensorDataList.size)
                }
            }
            i += 9
        }

        graphList.clear()
        graphList.add(GraphviewData("Dissolved Oxygen", latestDo,progressBarDo,10,"mg/dl", minDo,"mg/dl",
            MinDateDo,MinTimeDo, maxDo, "mg/dl", MaxDateDo,MaxTimeDo, seriesDo,flag,0.0,10.0,false,latestDate,latestTime))
        graphList.add(GraphviewData("Temperature", latestTemp,progressBarTemp,60,"°C", minTemp,"°C",
            MinDateTemp,MinTimeTemp, maxTemp, "°C", MaxDateTemp,MaxTimeTemp,seriesTemp,flag,0.0,60.0,false,latestDate,latestTime))
        graphList.add(GraphviewData("pH", latestPh,progressBarPh,14,"", minPh,"",
            MinDatePh,MinTimePh, maxPh, "", MaxDatePh,MaxTimePh,seriesPh,flag,4.0,10.0,false,latestDate,latestTime))
        graphList.add(GraphviewData("TDS", latestTds,progressBarTds,1000,"ppm", minTds,"ppm",
            MinDateTds,MinTimeTds, maxTds, "ppm", MaxDateTds,MaxTimeTds,seriesTds,flag,0.0,1000.0,true,latestDate,latestTime))
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