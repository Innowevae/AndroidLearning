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
// File Name: WiFiScanActivity.kt
// File Description: Wifi Scan Activity for Scan WiFi
// and post customer WiFi credentials to the device
// Author: Anshul Malviya
// Date: May 3, 2023
/////////////////////////////////////////////////////

package com.innoweavebiocare.matsya

//outside library
import android.Manifest
import org.json.JSONObject
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

//inside library
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.wifi.ScanResult
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_wifiscan.*
import kotlinx.android.synthetic.main.dialog_wifi_post_activity.*
import kotlinx.android.synthetic.main.list_item_wifi_recycle_view.*

import com.innoweavebiocare.matsya.wifiwrapper.WifiConnectivityCallbackResult
import com.innoweavebiocare.matsya.wifiwrapper.WifiScanCallbackResult
import com.innoweavebiocare.matsya.wifiwrapper.WifiManagerWrapper
import kotlinx.android.synthetic.main.activity_login.*


class WiFiScanActivity : AppCompatActivity(), WifiScanCallbackResult,
    WifiConnectivityCallbackResult {

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var wifiScanResultList: List<ScanResult>
    private var wifiManagerWrapper: WifiManagerWrapper? = null
    private var ImgBtn_back_arrow: ImageButton? = null
    var count = 0
    var clickedDeviceID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifiscan)

        swipeRefresh= findViewById(R.id.swipeRefresh)
        ImgBtn_back_arrow = findViewById(R.id.ImgBtn_back_arrow)

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

        wifiManagerWrapper = WifiManagerWrapper()
        wifiManagerWrapper!!.wifiManagerInti(this).startManualWifiScanner(this) // For Scanning wifi
        wifiManagerWrapper!!.wifiManagerInti(applicationContext).autoWifiScanner(this) // Scan Wifi


        // swipe down refresh
        swipeRefresh.setOnRefreshListener {
            Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
            wifiManagerWrapper = WifiManagerWrapper()
            wifiManagerWrapper!!.wifiManagerInti(this).autoWifiScanner(this)
                swipeRefresh.isRefreshing = false
            }

        // Get macId from recycler view
        val macID = intent.getStringExtra("macId")
        if (macID != null) {
            clickedDeviceID = macID
        }

        // back button
        ImgBtn_back_arrow?.setOnClickListener {
            wifiManagerWrapper?.forgetWifi(clickedDeviceID, this) // Forget wifi network if we click on the back button
            val intent = Intent(this@WiFiScanActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Forget wifi network if we clear the current activity
    override fun onPause() {
        super.onPause()
        wifiManagerWrapper?.forgetWifi(clickedDeviceID, this)
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
    
    override fun wifiFailureResult(results: MutableList<ScanResult>) {
        wifiScanResultList = emptyList()
        wifiScanResultList = results
        setRecycleViewAdapter(wifiScanResultList)
    }

    override fun wifiSuccessResult(results: List<ScanResult>) {
        wifiScanResultList = emptyList()
        wifiScanResultList = results
        setRecycleViewAdapter(wifiScanResultList)
        if(swipeRefresh.isRefreshing){
            swipeRefresh.isRefreshing = false
        }
    }

    private fun setRecycleViewAdapter(
        arrayList: List<ScanResult>
    ) {
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.adapter = WifiRecyclerViewAdapter(arrayList)
        recycleView.animation
        initOnItemTouchListener()
    }

    //dialog box function that get password from user and post data to Matsya Device
    @SuppressLint("SuspiciousIndentation")
    fun dialogBox(select : String) {
        var selectWifiSSID = select
        val urlHttpPost = "http://192.168.1.1:80/httpPost"
        val dialog = Dialog(this@WiFiScanActivity)
        if (dialog != null && dialog.isShowing) {
            dialog.dismiss()
        } else {
            dialog.setContentView(R.layout.dialog_wifi_post_activity)
            val dialogWifiNameTv: TextView = dialog.findViewById(R.id.dialogwifiname)
            val dialogSSIDEt: EditText = dialog.findViewById(R.id.dialogSSIDEt)
            if(selectWifiSSID != "") {
                dialogWifiNameTv.text = selectWifiSSID
            } else{
                dialogSSIDEt.visibility = View.VISIBLE
                dialogWifiNameTv.visibility = View.GONE
                selectWifiSSID = dialogSSIDEt.text.toString()
            }
            val cancelButton: Button = dialog.findViewById(R.id.dialogCancelBtn)
            dialog.show()
            dialog.setCanceledOnTouchOutside(false)

            cancelButton.setOnClickListener {
                dialog.dismiss()
                count--
            }

            val connectButton: Button = dialog.findViewById(R.id.dialogConnectBtn)

            connectButton.setOnClickListener {
                count--
                val dialogPassEt: EditText = dialog.findViewById(R.id.dialogPasswEt)
                val jsonObject = JSONObject()
                val credObject = JSONObject()
                dialog.dismiss()
                val mRequestQueue = Volley.newRequestQueue(this)
                val HTTP_POST_TIMEOUT = 20 * 1000 // 20sec timeout for post
                val HTTP_POST_MAX_RETRIES = 0
                val mStringRequest =
                    object : StringRequest(Method.POST, urlHttpPost, Response.Listener { response ->
                        Toast.makeText(
                            applicationContext,
                            "Wifi Credentials Updated Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                    }, Response.ErrorListener { error ->
                        Toast.makeText(
                            applicationContext,
                            "Could not update Wi-Fi Credentials. Please try again after some time, if problem persists, contact administrator.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        override fun getBodyContentType(): String {
                            return "application/json"
                        }

                        override fun getBody(): ByteArray {
                            credObject.put("SSID", selectWifiSSID)
                            credObject.put("PASS", dialogPassEt.text.toString())
                            return jsonObject.put("WIFI_STA_SETTING", credObject).toString()
                                .toByteArray()
                        }
                    }
                mStringRequest.setRetryPolicy(
                    DefaultRetryPolicy(
                        HTTP_POST_TIMEOUT, // 20sec timeout for post
                        HTTP_POST_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
                )
                mRequestQueue!!.add(mStringRequest!!)
            }
        }
    }

    // Function for Recycler Touch Listener
    private fun initOnItemTouchListener() {
        recycleView.addOnItemTouchListener(
            WifiRecyclerTouchListener(
                applicationContext,
                recycleView,
                object : WifiRecyclerTouchListener.ClickListener {
                    @SuppressLint("SuspiciousIndentation", "ResourceType")
                    override fun onClick(view: View?, position: Int) {
                      val select = wifiScanResultList[position].SSID.toString()
                        if(count<1) {
                            count++
                            dialogBox(select)

                        }
                    }
                    override fun onLongClick(view: View?, position: Int) {
                    }
                })
        )
    }

    override fun wifiConnectionStatusChangedResult() {
        setRecycleViewAdapter(wifiScanResultList)
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
}
