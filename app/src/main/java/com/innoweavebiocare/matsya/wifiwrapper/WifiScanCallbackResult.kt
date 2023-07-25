///////////////////////////////////////////////////
//                                               //
// Copyright 2022-2023                           //
// Notice: Property of Innoweave Biocare         //
// Any part of this code cannot be copied or     //
// redistributed without prior consent of        //
// Innoweave                                     //
//                                               //
///////////////////////////////////////////////////

///////////////////////////////////////////////////
// File Name: WifiScanCallbackResult.kt
// File Description: Interface class for get wifi
// list and it will used by wifi manager.
// Author: Anshul Malviya
// Date: Feb 08, 2023
///////////////////////////////////////////////////

package com.innoweavebiocare.matsya.wifiwrapper

import android.net.wifi.ScanResult

interface WifiScanCallbackResult {
    fun wifiFailureResult(results: MutableList<ScanResult>);
    fun wifiSuccessResult(results: List<ScanResult>)
}