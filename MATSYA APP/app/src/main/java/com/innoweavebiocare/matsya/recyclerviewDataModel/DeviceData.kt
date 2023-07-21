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
// File Name: DeviceData.kt
// File Description: data class for home page recycler
// view
// Author: Anshul Malviya
// Date: April 10, 2023
/////////////////////////////////////////////////////

package com.innoweavebiocare.matsya.recyclerviewDataModel

data class DeviceData(
    val macID : String,
    val pondName: String,
    val dO: Float,
    val temp: Float,
    val pH: Float,
    val tds: Int,
    val dateString: String,
    val timeString: String
)

