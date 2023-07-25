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
// File Name: LoadingActivity.kt
// File Description: Splash Screen with shimmer effect
// for connecting device wifi.
// Author: Anshul Malviya
// Date: May 30, 2023
/////////////////////////////////////////////////////
package com.innoweavebiocare.matsya

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.innoweavebiocare.matsya.wifiwrapper.WifiConnectivityCallbackResult
import com.innoweavebiocare.matsya.wifiwrapper.WifiManagerWrapperForQR
import com.innoweavebiocare.matsya.wifiwrapper.WifiScanCallbackResult

class LoadingWifiScanActivity : AppCompatActivity() , WifiScanCallbackResult,
    WifiConnectivityCallbackResult {
    private lateinit var shimmer: ShimmerFrameLayout
    // wifi
    private lateinit var wifiScanResultList: List<ScanResult>
    private var wifiManagerWrapper: WifiManagerWrapperForQR? = null
    private val pass = "a28efcba-ab11-4015-bcf7-f4d98df0e045"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shimmer_loading_main)

        // set black color status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Shimmer loading animation
        shimmer = findViewById(R.id.shimmer)
        shimmer.startShimmer()

        // Get macId from recycler view
        var clickedDeviceID = ""
        val macID = intent.getStringExtra("macId")
        if (macID != null) {
            clickedDeviceID = macID
        }

        wifiConnect(clickedDeviceID)
        Log.i("LoadingWifiScan", "MacId $clickedDeviceID")

    }
    private fun wifiConnect(clickedDeviceID: String) {

        // For checking Location permission.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }

        val wifi = getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManagerWrapper = WifiManagerWrapperForQR() // object of WifimanagerWrapperForQR class

        // Check if wifi is not enable then it will enable the wifi and scan the wifi
        if(!wifi.isWifiEnabled) {
            wifi.isWifiEnabled = true
            wifiManagerWrapper!!.wifiManagerInti(applicationContext).startManualWifiScanner(this) // Scan Wifi
        } else{
            wifiManagerWrapper!!.wifiManagerInti(applicationContext).startManualWifiScanner(this) // Scan Wifi
        }

        // Wait for 5 seconds and then try to connect wifi
        Handler().postDelayed({
            try {
                wifiManagerWrapper?.connectWifi(
                    clickedDeviceID,
                    pass,
                    wifiManagerWrapper!!.WPA_WPA2_PSK,
                    this@LoadingWifiScanActivity
                )

                // 5 seconds handler for check if wifi is connected with given ssid or not
                Handler().postDelayed({

                    // If wifi is connected with given ssid then it will move to calibrationActivity otherwise show dialog
                    if (wifiManagerWrapper?.isConnectedTo(clickedDeviceID)!!) {
                        val intent = Intent(this, WiFiScanActivity::class.java)
                        intent.putExtra("macId", clickedDeviceID)
                        startActivity(intent)
                        finish()
                        Toast.makeText(
                            this@LoadingWifiScanActivity,
                            "Device Connected.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val dialog = Dialog(this@LoadingWifiScanActivity) // Dialog
                        dialog.setContentView(R.layout.dialog_not_connected)
                        dialog.show()
                        dialog.setCanceledOnTouchOutside(false)
                        dialog.setCancelable(false)
                        val dialogOkBtn: Button = dialog.findViewById(R.id.dialogOkBtn)

                        dialogOkBtn.setOnClickListener {
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }, 7000)

            } catch (e: Exception) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(this@LoadingWifiScanActivity, "Device Unavailable, Move near the device.", Toast.LENGTH_SHORT).show()
            }        },5000)
    }

    override fun onResume() {
        super.onResume()
        requestDeviceLocationSettings();
    }

    fun requestDeviceLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            val state = locationSettingsResponse.locationSettingsStates
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {

                    exception.startResolutionForResult(
                        this,
                        100
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    var msg = sendEx.message.toString()
                    Toast.makeText(this, " message for catch $msg", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Location Permission
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ===
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun wifiFailureResult(results: MutableList<ScanResult>) {
        Log.i("LoadingWifiScan", "Wi-fi Failure Result*****************= $results")
        wifiScanResultList = emptyList()
        wifiScanResultList = results
        checkDeviceConnected(wifiScanResultList)

    }

    override fun wifiSuccessResult(results: List<ScanResult>) {
        Log.i("LoadingWifiScan", "Wi-fi Success Result*****************= $results")
        wifiScanResultList = emptyList()
        wifiScanResultList = results
        //Check Available Devices
        checkDeviceConnected(wifiScanResultList)

    }

    override fun wifiConnectionStatusChangedResult() {
        Log.i("LoadingWifiScan", "************Connection Status Changed Result************")
        checkDeviceConnected(wifiScanResultList)

    }

    private fun checkDeviceConnected(wifiScanResultListCheck: List<ScanResult>): Boolean? {
        for (index in wifiScanResultListCheck.indices) {
            return if (wifiManagerWrapper?.isConnectedTo(wifiScanResultListCheck[index].SSID)!!) {
                wifiScanResultList[index].capabilities = "Connected"

                Log.i("LoadingWifiScan", "Connected")

                true
            } else {
                wifiScanResultList[index].capabilities = "Connection not established"
                Log.i("LoadingWifiScan","Connection not established")

                false
            }
        }
        return null
    }

}