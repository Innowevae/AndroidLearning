////////////////////////////////////////////////////
//                                                //
// Copyright 2022-2023                            //
// Notice: Property of Innoweave Biocare  //
// Any part of this code cannot be copied or      //
// redistributed without prior consent of         //
// Innoweave                                      //
//                                                //
////////////////////////////////////////////////////

////////////////////////////////////////////////////
// File Name: WifiManagerWrapper.kt
// File Description: This manager class for scanning
// and handling wifi.
// Author: Anshul Malviya
// Date: Feb 08, 2023
////////////////////////////////////////////////////


package com.innoweavebiocare.matsya.wifiwrapper

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.innoweavebiocare.matsya.CalibrationActivity
import com.innoweavebiocare.matsya.WiFiScanActivity

class WifiManagerWrapper() {
    private val TAG: String? = "com.wifimanagerwrapper"
    private lateinit var context: Context
    private lateinit var wifiManager: WifiManager
    private lateinit var scanListenerCallback: WifiScanCallbackResult
    private lateinit var connectivityListenerCallback: WifiConnectivityCallbackResult
    private var wifiScanReceiver: BroadcastReceiver? = null


    fun wifiManagerInti(context: Context): WifiManagerWrapper {
        this.context = context
        wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        Log.d(TAG, "ConnectionInfo :" + wifiManager.connectionInfo)
        return this
    }


    fun autoWifiScanner(wifiScanCallbackResult: WifiScanCallbackResult) {
        this.scanListenerCallback = wifiScanCallbackResult
        if (wifiManager.isWifiEnabled) {
            autoStartStopWifiScanner()
            Log.d(TAG, "WiFi is Enabled")
        } else {
            wifiManager.isWifiEnabled = true
            if (wifiManager.isWifiEnabled) {
                Log.d(TAG, "WiFi is Enabled")
                autoStartStopWifiScanner()
            } else {
                Log.d(TAG, "Unable to enable WiFi, Make sure your hotspot in disable.")
            }
        }
    }

    fun forgetWifi(
        networkSSID: String,
        wifiConnectivityCallbackResult: WiFiScanActivity
    ) {
        this.connectivityListenerCallback = wifiConnectivityCallbackResult
        val wm: WifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiConfig: WifiConfiguration? = getWiFiConfig(networkSSID)
        if (wifiConfig != null) {
            wm.disableNetwork(wifiConfig.networkId)
            wm.removeNetwork(wifiConfig.networkId)
            wm.saveConfiguration()
            Log.d(TAG, "Network SSID is removed successfully")
            connectionStatusChanged()
        }
    }

    fun startManualWifiScanner(wifiScanCallbackResult: WifiScanCallbackResult) {
        this.scanListenerCallback = wifiScanCallbackResult
        // Create Instance for broadcast receiver Wi-Fi Scanner
        wifiScannerBroadcastReceiverInstance()
        // Register broadcast receiver for Wi-Fi Scanner
        registerWifiScannerBroadcastReceiver()
        val success = wifiManager.startScan()
        if (!success) {
            // scan failure handling
            unregisterWifiScannerBroadcastReceiver()
            Log.d(TAG, "Scan failure handling")
            scanFailure()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getWiFiConfig(networkSSID: String): WifiConfiguration? {
        val wm: WifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiList = wm.configuredNetworks

        for (item in wifiList) {
            if (item.SSID != null && item.SSID == String.format("\"%s\"", networkSSID)) {
                Log.d(TAG, "Network SSID is Available in WiFiManger")
                return item
            }
        }
        Log.d(TAG, "Network SSID is Not Available in WiFiManger")
        return null
    }

    private fun connectionStatusChanged() {
        // Connection Success, Wi-Fi connection established
        // or Either
        // Connection Failure, Wi-Fi connection not yet established
        connectivityListenerCallback.wifiConnectionStatusChangedResult()
    }


    private fun autoStartStopWifiScanner() {
        // Create Instance for broadcast receiver Wi-Fi Scanner
        wifiScannerBroadcastReceiverInstance()
        // Register broadcast receiver for Wi-Fi Scanner
        registerWifiScannerBroadcastReceiver()
        val success = wifiManager.startScan()
        if (!success) {
            // scan failure handling
            unregisterWifiScannerBroadcastReceiver()
            Log.d(TAG, "Scan failure handling")
            scanFailure()
        }
    }


    private fun unregisterWifiScannerBroadcastReceiver() {
        if (wifiScanReceiver != null) {
            Log.d(TAG, "Unregister Wifi Scanner BroadcastReceiver")
            context.unregisterReceiver(wifiScanReceiver)
        }
    }

    private fun registerWifiScannerBroadcastReceiver() {
        if (wifiScanReceiver != null) {
            Log.d(TAG, "Register Wifi Scanner BroadcastReceiver")
            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            context.registerReceiver(wifiScanReceiver, intentFilter)
        }
    }

    private fun wifiScannerBroadcastReceiverInstance() {
        if (wifiScanReceiver == null) {
            wifiScanReceiver = object : BroadcastReceiver() {

                @RequiresApi(Build.VERSION_CODES.M)
                override fun onReceive(context: Context, intent: Intent) {
                    val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                    if (success) {
                        Log.d(TAG, "Scan success handling with result list.")
                        scanSuccess()
                    } else {
                        Log.d(TAG, "Scan failure handling")
                        scanFailure()
                    }
                }
            }
        }
    }

    private fun scanSuccess() {
        val results = wifiManager.scanResults
        scanListenerCallback.wifiSuccessResult(results)
    }

    private fun scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        val results = wifiManager.scanResults
        scanListenerCallback.wifiFailureResult(results)
    }

}
