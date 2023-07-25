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
import android.text.format.DateUtils
import android.transition.Fade
import android.transition.Slide
import android.view.*
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.innoweavebiocare.matsya.recyclerviewDataModel.GraphviewData
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.list_item_recyclerview_dashboard_activity.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class GraphAdapterActivity(val c: DashBoardActivity, private var graphList:ArrayList<GraphviewData>): RecyclerView.Adapter<GraphAdapterActivity.UserViewHolder>()
{
    val sdf = SimpleDateFormat("LLL dd, yyyy")
    val stf = SimpleDateFormat("HH:mm")
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
        var graph: GraphView = v.findViewById(R.id.tvGraph)
        var series = LineGraphSeries<DataPoint>()
        var tvDate: TextView = v.findViewById(R.id.tvDate)
        var tvTime: TextView = v.findViewById(R.id.tvTime)
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
        holder.series=newList.series
        holder.graph.removeAllSeries()
        holder.graph.addSeries(holder.series)
        holder.tvDate.text=newList.tvCurrentDate
        holder.tvTime.text=newList.tvCurrentTime

        holder.series.setOnDataPointTapListener { series, dataPoint->
            val tappedDate = sdf.format(dataPoint.x * 1000L)
            val tappedTime = stf.format(dataPoint.x * 1000L)

            val dialogView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.dialog_graph_touch_data, null)
            val textData = dialogView.findViewById<TextView>(R.id.textData)
            textData.text =
                "${newList.tvHead}: ${((dataPoint.y * 100).roundToInt() / 100.0).toFloat()}" + " at " + tappedDate + " " + tappedTime

            val popupWindow = PopupWindow(
                dialogView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            val enterTransition = Slide()
            val exitTransition=Fade()
            enterTransition.duration = 300 // milliseconds

            popupWindow.setBackgroundDrawable(holder.itemView.context.getDrawable(R.drawable.shape_rectangle_clicked))
            popupWindow.isOutsideTouchable = true
            popupWindow.animationStyle= android.R.style.Animation_Dialog
            popupWindow.enterTransition=enterTransition
            popupWindow.exitTransition=exitTransition
            popupWindow.showAtLocation(holder.itemView, Gravity.CENTER, 0, 0)
        }

        if (newList.isAutoYRange) {
            holder.graph.viewport.isYAxisBoundsManual = false
        } else {
        holder.graph.viewport.isYAxisBoundsManual=true
        holder.graph.viewport.setMinY(newList.minY)
        holder.graph.viewport.setMaxY(newList.maxY)
        }

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

        if(holder.series.isEmpty){
            holder.graph.gridLabelRenderer.labelFormatter =  object : DefaultLabelFormatter() {
                override fun formatLabel(value: Double, isValueX: Boolean): String {
                    if (isValueX) {
                        return "NA"
                    }
                    return super.formatLabel(value, isValueX)
                }
            }
        }else{
            holder.graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
                override fun formatLabel(value: Double, isValueX: Boolean): String {
                    if (isValueX) {
                        // Convert epoch time to milliseconds and format to show only time
                        if (newList.flag == 1) {
                            return DateUtils.formatDateTime(
                                holder.itemView.context, (value.toLong() * 1000),
                                DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_24HOUR
                            )
                        } else {
                            return DateUtils.formatDateTime(
                                holder.itemView.context, (value.toLong() * 1000),
                                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_NUMERIC_DATE
                            )
                        }
                    }
                    return super.formatLabel(value, isValueX)
                }
            }
        }

    }
    override fun getItemCount(): Int {
       return graphList.size
    }
}