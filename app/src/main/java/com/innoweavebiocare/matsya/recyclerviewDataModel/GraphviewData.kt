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
// File Name: GraphviewData.kt
// File Description: data class for dashboard activity
// recycler view
// Author: Ritvik Sahu
// Date: May 2, 2023
/////////////////////////////////////////////////////

package com.innoweavebiocare.matsya.recyclerviewDataModel

import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

data class GraphviewData(
    val tvHead: String,
    val progressText:String,
    val progressBar:Float,
    val progressBarMax:Int,
    val tvUnit:String,
    val tvMin:String,
    val tvMinUnit: String,
    val tvMinDate:String,
    val tvMinTime:String,
    val tvMax:String,
    val tvMaxUnit: String,
    val tvMaxDate:String,
    val tvMaxTime:String,
    val series: LineGraphSeries<DataPoint>,
    val flag:Int,
    val minY:Double,
    val maxY:Double,
    val isAutoYRange:Boolean,
    val tvCurrentDate : String,
    val tvCurrentTime: String,
    )