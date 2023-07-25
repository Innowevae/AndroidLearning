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
// File Name: GraphAdapterActivity.kt
// File Description: adapter class for handling recycler
// view data in dashboard activity.
// Author: Ritvik Sahu
// Date: May 2, 2023
//////////////////////////////////////////////////

package com.innoweavebiocare.matsya

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.innoweavebiocare.matsya.recyclerviewDataModel.GraphviewData
import kotlinx.android.synthetic.main.list_item_recyclerview_dashboard_activity.view.*
import java.text.SimpleDateFormat
import java.time.*
import java.util.*


class GraphAdapterActivity(val c: DashBoardActivity, private var graphList:ArrayList<GraphviewData>): RecyclerView.Adapter<GraphAdapterActivity.UserViewHolder>()
{

    @SuppressLint("ClickableViewAccessibility")
    inner class UserViewHolder(private val v: View):RecyclerView.ViewHolder(v) {
        var tvHead: TextView = v.findViewById(R.id.tvHead)
        var Progressbar: ProgressBar = v.findViewById(R.id.tvProgressBar)
        var progressText: TextView = v.findViewById(R.id.tvProgressText)
        var tvUnit: TextView = v.findViewById(R.id.tvUnit)
        var tvMin: TextView = v.findViewById(R.id.tvMin)
        var tvMinUnit: TextView = v.findViewById(R.id.tvMinUnit)
        var tvMinDate: TextView = v.findViewById(R.id.tvMinDate)
        var tvMinTime: TextView = v.findViewById(R.id.tvMinTime)
        var tvMax: TextView = v.findViewById(R.id.tvMax)
        var tvMaxUnit: TextView = v.findViewById(R.id.tvMaxUnit)
        var tvMaxDate: TextView = v.findViewById(R.id.tvMaxDate)
        var tvMaxTime: TextView = v.findViewById(R.id.tvMaxTime)
        var tvDate: TextView = v.findViewById(R.id.tvDate)
        var tvTime: TextView = v.findViewById(R.id.tvTime)
        // GraphView
        var lineChart: LineChart = v.findViewById(R.id.lineChart)

        init {
            // Set the chart value selected listener
            lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e != null) {
                        val yValue = e.y
                        Toast.makeText(
                            c.applicationContext,
                            "$yValue",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onNothingSelected() {
                    // Handle when nothing is selected (optional)
                }
            })
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.list_item_recyclerview_dashboard_activity,parent,false)
        return UserViewHolder(v)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val newList = graphList[position]
        holder.tvHead.text=newList.tvHead
        holder.progressText.text=newList.progressText
        holder.Progressbar.progress= newList.progressBar.toInt()
        holder.Progressbar.max=newList.progressBarMax
        holder.tvMin.text=newList.tvMin
        holder.tvMinUnit.text=newList.tvMinUnit
        holder.tvMinDate.text=newList.tvMinDate
        holder.tvMinTime.text=newList.tvMinTime
        holder.tvMax.text=newList.tvMax
        holder.tvMaxUnit.text=newList.tvMaxUnit
        holder.tvMaxDate.text=newList.tvMaxDate
        holder.tvMaxTime.text=newList.tvMaxTime
        holder.tvUnit.text=newList.tvUnit
        holder.tvDate.text=newList.tvCurrentDate
        holder.tvTime.text=newList.tvCurrentTime

        if(newList.progressText=="NA"){
            holder.progressText.setTextColor(Color.RED)
        }else{
            holder.progressText.setTextColor(Color.BLACK)
        }
        if(newList.tvMin=="NA"){
            holder.tvMin.setTextColor(Color.RED)
        }else{
            holder.tvMin.setTextColor(Color.BLACK)
        }
        if(newList.tvMinDate=="NA"){
            holder.tvMinDate.setTextColor(Color.RED)
        }else{
            holder.tvMinDate.setTextColor(Color.parseColor("#196F88"))
        }
        if(newList.tvMinTime=="NA"){
            holder.tvMinTime.setTextColor(Color.RED)
        }else{
            holder.tvMinTime.setTextColor(Color.parseColor("#196F88"))
        }
        if(newList.tvMax=="NA"){
            holder.tvMax.setTextColor(Color.RED)
        }else{
            holder.tvMax.setTextColor(Color.BLACK)
        }
        if(newList.tvMaxDate=="NA"){
            holder.tvMaxDate.setTextColor(Color.RED)
        }else{
            holder.tvMaxDate.setTextColor(Color.parseColor("#196F88"))
        }
        if(newList.tvMaxTime=="NA"){
            holder.tvMaxTime.setTextColor(Color.RED)
        }else{
            holder.tvMaxTime.setTextColor(Color.parseColor("#196F88"))
        }

        if(newList.progressText == "NA"){
            holder.Progressbar.progressTintList= ColorStateList.valueOf(Color.parseColor("#FFDDDDDD"))
        }

        // Set up the line chart data and properties
        val dataSet = LineDataSet(newList.lineChartEntries, "Data").apply {
            color = Color.parseColor("#196F88")
            valueTextColor = Color.BLACK
            setDrawCircles(false)
            setDrawValues(false)
            lineWidth = 2f
        }

        val lineData = LineData(dataSet)
        holder.lineChart.data = lineData

        val xAxis: XAxis = holder.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMaximum = newList.xAxisRange.toFloat()
        xAxis.axisMinimum = -0.1f // Set axisMinimum to include 0 value
        xAxis.granularity = newList.granularityForXAxis.toFloat()
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                var xAxisValue = ""
                if(newList.flag == 1){
                    val hour = value.toInt() % 24
                    val isAM = hour < 12
                    val hourText = if (hour == 0 || hour == 12) "12" else (hour % 12).toString()
                    val amPmText = if (isAM) "AM" else "PM"
                    xAxisValue = "$hourText $amPmText"
                }
                else if(newList.flag == 2){
                    val daysAgo = when (value) {
                        0.0f -> 6
                        30.0f -> 5
                        60.0f -> 4
                        90.0f -> 3
                        120.0f -> 2
                        150.0f -> 1
                        180.0f -> 0
                        else -> -1
                    }

                    if (daysAgo != -1) {
                        xAxisValue = getDateString(getEpochTimestampDaysAgo(daysAgo.toLong()))
                    }
                }
                else if(newList.flag == 3){
                    val daysAgo = when (value) {
                        0.0f -> 30
                        20.0f -> 25
                        40.0f -> 20
                        60.0f -> 15
                        80.0f -> 10
                        100.0f -> 5
                        120.0f -> 0
                        else -> -1
                    }

                    if (daysAgo != -1) {
                        xAxisValue = getDateString(getEpochTimestampDaysAgo(daysAgo.toLong()))
                    }
                }
                else if(newList.flag == 4){
                    val daysAgo = when (value) {
                        0.0f -> 90
                        20.0f -> 72
                        40.0f -> 54
                        60.0f -> 36
                        80.0f -> 18
                        100.0f -> 0

                        else -> -1
                    }

                    if (daysAgo != -1) {
                        xAxisValue = getDateString(getEpochTimestampDaysAgo(daysAgo.toLong()))
                    }
                }
               return xAxisValue
            }
        }

        val yAxisLeft: YAxis = holder.lineChart.axisLeft
        yAxisLeft.axisMaximum = newList.yAxisRange.toFloat()
        yAxisLeft.axisMinimum = 0f

        val yAxisRight: YAxis = holder.lineChart.axisRight
        yAxisRight.isEnabled = false

        holder.lineChart.description.isEnabled = false
        holder.lineChart.isDragEnabled = false
        holder.lineChart.setScaleEnabled(false)
        holder.lineChart.setPinchZoom(false)
        holder.lineChart.setDrawGridBackground(false)
        holder.lineChart.legend.isEnabled = false

        holder.lineChart.invalidate()

    }

    override fun getItemCount(): Int {
       return graphList.size
    }

    fun getEpochTimestampDaysAgo(daysAgo: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, (-daysAgo).toInt())
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis / 1000L
    }

    private val sdf = SimpleDateFormat("dd/LLL")
    private fun getDateString(time: Long) : String = sdf.format(time * 1000L)
}