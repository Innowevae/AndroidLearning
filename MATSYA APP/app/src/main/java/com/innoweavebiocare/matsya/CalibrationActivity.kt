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
// File Name: CalibrationActivity.kt
// File Description: Handles http requests for
// calibrating the device.
// Author: Anshul Malviya
// Date: Apr 7, 2023
///////////////////////////////////////////////////

package com.innoweavebiocare.matsya

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.innoweavebiocare.matsya.wifiwrapper.WifiConnectivityCallbackResult
import com.innoweavebiocare.matsya.wifiwrapper.WifiManagerWrapperForQR
import com.innoweavebiocare.matsya.wifiwrapper.WifiScanCallbackResult
import kotlinx.android.synthetic.main.activity_calibration.*
import org.json.JSONObject


class CalibrationActivity : AppCompatActivity() , WifiScanCallbackResult,
    WifiConnectivityCallbackResult {
    private var tv_cal_ph7: TextView? = null
    private var tv_cal_ph4: TextView? = null
    private var tv_cal_ph10: TextView? = null
    private var tv_cal_do0: TextView? = null
    private var tv_cal_doatm: TextView? = null
    private var tv_cal_tdsdry: TextView? = null
    private var tv_cal_tdslow: TextView? = null
    private var tv_cal_tdshigh: TextView? = null
    private var tv_cal_clear: TextView? = null
    private var ImgBtn_back_arrow: ImageButton? = null

    private var progressBarCalpH7: ProgressBar? = null
    private var progressBarCalpH4: ProgressBar? = null
    private var progressBarCalpH10: ProgressBar? = null
    private var progressBarCalDO0: ProgressBar? = null
    private var progressBarCalDoAtm: ProgressBar? = null
    private var progressBarCalTDSDry: ProgressBar? = null
    private var progressBarCalTDSLow: ProgressBar? = null
    private var progressBarCalTDSHigh: ProgressBar? = null
    private var progressBarCalClear: ProgressBar? = null

    private var rightCheckPH7: ImageView? = null
    private var rightCheckPH4: ImageView? = null
    private var rightCheckPH10: ImageView? = null
    private var rightCheckDO0: ImageView? = null
    private var rightCheckDoAtm: ImageView? = null
    private var rightCheckTDSDry: ImageView? = null
    private var rightCheckTDSLow: ImageView? = null
    private var rightCheckTDSHigh: ImageView? = null
    private var rightCheckClear: ImageView? = null

    private var wrongPH7: ImageView? = null
    private var wrongPH4: ImageView? = null
    private var wrongPH10: ImageView? = null
    private var wrongDO0: ImageView? = null
    private var wrongDoAtm: ImageView? = null
    private var wrongTDSDry: ImageView? = null
    private var wrongTDSLow: ImageView? = null
    private var wrongTDSHigh: ImageView? = null
    private var wrongClear: ImageView? = null

    private val calID = "CAL"
    private val calValuePH7 = "CAL_PH_7"
    private val calValuePH4 = "CAL_PH_4"
    private val calValuePH10 = "CAL_PH_10"
    private val calValueDO0 = "CAL_DO_SAT"
    private val calValueDOAtm = "CAL_DO_ATM"
    private val calValueTDSDry = "CAL_TDS_DRY"
    private val calValueTDSLow = "CAL_TDS_LOW"
    private val calValueTDSHigh = "CAL_TDS_HIGH"
    private val calValueClear = "CLEAR_CAL"

    private val HTTP_POST_TIMEOUT = 20 * 1000 // 20sec timeout for post
    private val HTTP_POST_MAX_RETRIES = 0
    var mHandler = Handler()
    private val DELAY_PROGRESS_BAR_UPDATE = 200 // 0.2s sleep time per loop cycle
    private val DELAY_ENABLE_BUTTONS = 22000 // 22sec Delay time for enable button handler
    private val urlHttpPost = "http://192.168.1.1:80/httpPost" // Local address of Matsya device
    private lateinit var wifiScanResultList: List<ScanResult>
    private var wifiManagerWrapper: WifiManagerWrapperForQR? = null
    var clickedDeviceID = ""


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibration)

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

        tv_cal_ph7 = findViewById(R.id.tv_cal_ph7)
        tv_cal_ph4 = findViewById(R.id.tv_cal_ph4)
        tv_cal_ph10 = findViewById(R.id.tv_cal_ph10)
        tv_cal_do0 = findViewById(R.id.tv_cal_do0)
        tv_cal_doatm = findViewById(R.id.tv_cal_doatm)
        tv_cal_tdsdry = findViewById(R.id.tv_cal_tdsdry)
        tv_cal_tdshigh = findViewById(R.id.tv_cal_tdshigh)
        tv_cal_tdslow = findViewById(R.id.tv_cal_tdslow)
        tv_cal_clear = findViewById(R.id.tv_cal_clear)

        ImgBtn_back_arrow = findViewById(R.id.ImgBtn_back_arrow)

        progressBarCalpH7 = findViewById(R.id.progresBarCalpH7)
        progressBarCalpH4 = findViewById(R.id.progresBarCalpH4)
        progressBarCalpH10 = findViewById(R.id.progresBarCalpH10)
        progressBarCalDO0 = findViewById(R.id.progresBarCalDo0)
        progressBarCalDoAtm = findViewById(R.id.progresBarCalDoAtm)
        progressBarCalTDSDry = findViewById(R.id.progresBarCalTdsDry)
        progressBarCalTDSLow = findViewById(R.id.progresBarCalTdsLow)
        progressBarCalTDSHigh = findViewById(R.id.progresBarCalTdsHigh)
        progressBarCalClear = findViewById(R.id.progresBarCalClear)

        rightCheckPH7 = findViewById(R.id.checkRightpH7)
        rightCheckPH4 = findViewById(R.id.checkRightpH4)
        rightCheckPH10 = findViewById(R.id.checkRightpH10)
        rightCheckDO0 = findViewById(R.id.checkRightDo0)
        rightCheckDoAtm = findViewById(R.id.checkRightDoATm)
        rightCheckTDSDry = findViewById(R.id.checkRightTdsDry)
        rightCheckTDSLow = findViewById(R.id.checkRightTdsLow)
        rightCheckTDSHigh = findViewById(R.id.checkRightTdsHigh)
        rightCheckClear = findViewById(R.id.checkRightClear)

        wrongPH7 = findViewById(R.id.WrongPH7)
        wrongPH4 = findViewById(R.id.WrongPH4)
        wrongPH10 = findViewById(R.id.WrongPH10)
        wrongDO0 = findViewById(R.id.WrongDo0)
        wrongDoAtm = findViewById(R.id.WrongDoAtm)
        wrongTDSDry = findViewById(R.id.WrongTdsDry)
        wrongTDSLow = findViewById(R.id.WrongTdsLow)
        wrongTDSHigh = findViewById(R.id.WrongTdsHigh)
        wrongClear = findViewById(R.id.WrongClear)

        // set progressBar progress is 1
        progressBarCalpH7?.progress = 1
        progressBarCalpH4?.progress = 1
        progressBarCalpH10?.progress = 1
        progressBarCalDO0?.progress = 1
        progressBarCalDoAtm?.progress = 1
        progressBarCalTDSDry?.progress = 1
        progressBarCalTDSLow?.progress = 1
        progressBarCalTDSHigh?.progress = 1
        progressBarCalClear?.progress = 1

        wifiManagerWrapper = WifiManagerWrapperForQR() // object of WifimanagerWrapperForQR class
        wifiManagerWrapper!!.wifiManagerInti(applicationContext).startManualWifiScanner(this) // Scan Wifi
        wifiManagerWrapper!!.wifiManagerInti(applicationContext).autoWifiScanner(this) // Scan Wifi

        // Get macId from recycler view
        val macID = intent.getStringExtra("macId")
        if (macID != null) {
            clickedDeviceID = macID
        }

        // back button
        ImgBtn_back_arrow?.setOnClickListener {
            wifiManagerWrapper?.forgetWifi(clickedDeviceID, this) // Forget wifi network if we click on the back button
            val intent = Intent(this@CalibrationActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        tv_cal_ph7?.setOnClickListener {
            startProgress(progressBarCalpH7 ?: return@setOnClickListener)
            postCalRequest(calID, calValuePH7, urlHttpPost)
        }
        tv_cal_ph4?.setOnClickListener {
            startProgress(progressBarCalpH4 ?: return@setOnClickListener)
            postCalRequest(calID, calValuePH4, urlHttpPost)
        }
        tv_cal_ph10?.setOnClickListener {
            startProgress(progressBarCalpH10 ?: return@setOnClickListener)
            postCalRequest(calID, calValuePH10, urlHttpPost)
        }
        tv_cal_do0?.setOnClickListener {
            startProgress(progressBarCalDO0 ?: return@setOnClickListener)
            postCalRequest(calID, calValueDO0, urlHttpPost)
        }
        tv_cal_doatm?.setOnClickListener {
            startProgress(progressBarCalDoAtm ?: return@setOnClickListener)
            postCalRequest(calID, calValueDOAtm, urlHttpPost)
        }
        tv_cal_tdsdry?.setOnClickListener {
            startProgress(progressBarCalTDSDry ?: return@setOnClickListener)
            postCalRequest(calID, calValueTDSDry, urlHttpPost)
        }
        tv_cal_tdslow?.setOnClickListener {
            startProgress(progressBarCalTDSLow ?: return@setOnClickListener)
            postCalRequest(calID, calValueTDSLow, urlHttpPost)
        }
        tv_cal_tdshigh?.setOnClickListener {
            startProgress(progressBarCalTDSHigh ?: return@setOnClickListener)
            postCalRequest(calID, calValueTDSHigh, urlHttpPost)
        }
        tv_cal_clear?.setOnClickListener {
            startProgress(progressBarCalClear ?: return@setOnClickListener)
            postCalRequest(calID, calValueClear, urlHttpPost)
        }
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

    // disable all button
    private fun disableButton() {
        ImgBtn_back_arrow?.setColorFilter(resources.getColor(R.color.gray), PorterDuff.Mode.SRC_ATOP)
        tv_cal_ph4?.setTextColor(resources.getColor(R.color.gray))
        tv_cal_ph7?.setTextColor(resources.getColor(R.color.gray))
        tv_cal_ph10?.setTextColor(resources.getColor(R.color.gray))
        tv_cal_do0?.setTextColor(resources.getColor(R.color.gray))
        tv_cal_tdslow?.setTextColor(resources.getColor(R.color.gray))
        tv_cal_tdsdry?.setTextColor(resources.getColor(R.color.gray))
        tv_cal_doatm?.setTextColor(resources.getColor(R.color.gray))
        tv_cal_tdshigh?.setTextColor(resources.getColor(R.color.gray))
        tv_cal_clear?.setTextColor(resources.getColor(R.color.gray))

        ImgBtn_back_arrow?.isEnabled = false
        tv_cal_ph4?.isEnabled = false
        tv_cal_ph10?.isEnabled = false
        tv_cal_ph7?.isEnabled = false
        tv_cal_do0?.isEnabled = false
        tv_cal_tdslow?.isEnabled = false
        tv_cal_tdsdry?.isEnabled = false
        tv_cal_doatm?.isEnabled = false
        tv_cal_tdshigh?.isEnabled = false
        tv_cal_clear?.isEnabled = false
    }

    // function for increase progressbar progress
    private fun startProgress(proBar: ProgressBar) {
        disableButton()
        enableButtonTimer()
        Thread {
            for (i in 0..100) {
                try {
                    Thread.sleep(DELAY_PROGRESS_BAR_UPDATE.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                mHandler.post(Runnable {
                    proBar.progress = i
                })
            }
        }.start()
    }

    // Function for Post Calibration Values using Volley
    fun postCalRequest(calID: String, calValue: String, urlHttpPost: String) {
        var mRequestQueue = Volley.newRequestQueue(this)
        var mStringRequest =
            object : StringRequest(Method.POST, urlHttpPost,
                Response.Listener { response ->
                    onSuccess(response, calValue)
                },
                Response.ErrorListener { error ->
                    onFailure(error, calValue)
                }) {
                override fun getBodyContentType(): String {
                    return "application/json"
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    val params = HashMap<String, String>()
                    params.put(calID, calValue)
                    return JSONObject(params as Map<*, *>?).toString().toByteArray()
                }
            }
        mStringRequest.retryPolicy = DefaultRetryPolicy(
            HTTP_POST_TIMEOUT, // 20sec timeout for post
            HTTP_POST_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        mRequestQueue!!.add(mStringRequest!!)
    }

    // onSuccess fun if post request is succeed
    fun onSuccess(response: String, calValue: String) {
        if (calValue == calValuePH7) {
            progressBarCalpH7?.progress = 100
            rightCheckPH7?.visibility = View.VISIBLE
            progressBarCalpH7?.visibility = View.INVISIBLE
        }

        if (calValue == calValuePH4) {
            progressBarCalpH4?.progress = 100
            rightCheckPH4?.visibility = View.VISIBLE
            progressBarCalpH4?.visibility = View.INVISIBLE
        }

        if (calValue == calValuePH10) {
            progressBarCalpH10?.progress = 100
            rightCheckPH10?.visibility = View.VISIBLE
            progressBarCalpH10?.visibility = View.INVISIBLE
        }

        if (calValue == calValueDO0) {
            progressBarCalDO0?.progress = 100
            rightCheckDO0?.visibility = View.VISIBLE
            progressBarCalDO0?.visibility = View.INVISIBLE
        }

        if (calValue == calValueDOAtm) {
            progressBarCalDoAtm?.progress = 100
            rightCheckDoAtm?.visibility = View.VISIBLE
            progressBarCalDoAtm?.visibility = View.INVISIBLE
        }

        if (calValue == calValueTDSDry) {
            progressBarCalTDSDry?.progress = 100
            rightCheckTDSDry?.visibility = View.VISIBLE
            progressBarCalTDSDry?.visibility = View.INVISIBLE
        }

        if (calValue == calValueTDSLow) {
            progressBarCalTDSLow?.progress = 100
            rightCheckTDSLow?.visibility = View.VISIBLE
            progressBarCalTDSLow?.visibility = View.INVISIBLE
        }

        if (calValue == calValueTDSHigh) {
            progressBarCalTDSHigh?.progress = 100
            rightCheckTDSHigh?.visibility = View.VISIBLE
            progressBarCalTDSHigh?.visibility = View.INVISIBLE
        }

        if (calValue == calValueClear) {
            progressBarCalClear?.progress = 100
            rightCheckClear?.visibility = View.VISIBLE
            progressBarCalClear?.visibility = View.INVISIBLE
        }

        Toast.makeText(applicationContext, "$response", Toast.LENGTH_SHORT).show()
    }

    // onFailure fun if post request is not succeed
    fun onFailure(error: VolleyError, calValue: String) {
        if (calValue == calValuePH7) {
            progressBarCalpH7?.progress = 100
            wrongPH7?.visibility = View.VISIBLE
            progressBarCalpH7?.visibility = View.INVISIBLE
        }

        if (calValue == calValuePH4) {
            progressBarCalpH4?.progress = 100
            wrongPH4?.visibility = View.VISIBLE
            progressBarCalpH4?.visibility = View.INVISIBLE
        }

        if (calValue == calValuePH10) {
            progressBarCalpH10?.progress = 100
            wrongPH10?.visibility = View.VISIBLE
            progressBarCalpH10?.visibility = View.INVISIBLE
        }

        if (calValue == calValueDO0) {
            progressBarCalDO0?.progress = 100
            wrongDO0?.visibility = View.VISIBLE
            progressBarCalDO0?.visibility = View.INVISIBLE
        }

        if (calValue == calValueDOAtm) {
            progressBarCalDoAtm?.progress = 100
            wrongDoAtm?.visibility = View.VISIBLE
            progressBarCalDoAtm?.visibility = View.INVISIBLE
        }

        if (calValue == calValueTDSDry) {
            progressBarCalTDSDry?.progress = 100
            wrongTDSDry?.visibility = View.VISIBLE
            progressBarCalTDSDry?.visibility = View.INVISIBLE
        }


        if (calValue == calValueTDSLow) {
            progressBarCalTDSLow?.progress = 100
            wrongTDSLow?.visibility = View.VISIBLE
            progressBarCalTDSLow?.visibility = View.INVISIBLE
        }

        if (calValue == calValueTDSHigh) {
            progressBarCalTDSHigh?.progress = 100
            wrongTDSHigh?.visibility = View.VISIBLE
            progressBarCalTDSHigh?.visibility = View.INVISIBLE
        }

        if (calValue == calValueClear) {
            progressBarCalClear?.progress = 100
            wrongClear?.visibility = View.VISIBLE
            progressBarCalClear?.visibility = View.INVISIBLE
        }

        Toast.makeText(
            applicationContext, "Connection Failed, Not able to post! # "
                    + "$error", Toast.LENGTH_SHORT).show()
    }

    // fun for enable button and invisible all wrong and right icon and visible progress bar
    private fun enableButtonTimer() {
        Thread {
                try {
                    Thread.sleep(DELAY_ENABLE_BUTTONS.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                mHandler.post(Runnable {
                    Toast.makeText(this, "Ready to calibrate again....", Toast.LENGTH_SHORT).show()
                    wrongPH7?.visibility = View.INVISIBLE
                    wrongPH4?.visibility = View.INVISIBLE
                    wrongPH10?.visibility = View.INVISIBLE
                    wrongDO0?.visibility = View.INVISIBLE
                    wrongTDSLow?.visibility = View.INVISIBLE
                    wrongTDSDry?.visibility = View.INVISIBLE
                    wrongDoAtm?.visibility = View.INVISIBLE
                    wrongTDSHigh?.visibility = View.INVISIBLE
                    wrongClear?.visibility = View.INVISIBLE

                    rightCheckPH7?.visibility = View.INVISIBLE
                    rightCheckPH4?.visibility = View.INVISIBLE
                    rightCheckPH10?.visibility = View.INVISIBLE
                    rightCheckDO0?.visibility = View.INVISIBLE
                    rightCheckTDSLow?.visibility = View.INVISIBLE
                    rightCheckTDSDry?.visibility = View.INVISIBLE
                    rightCheckDoAtm?.visibility = View.INVISIBLE
                    rightCheckTDSHigh?.visibility = View.INVISIBLE
                    rightCheckClear?.visibility = View.INVISIBLE

                    progressBarCalpH7?.visibility = View.VISIBLE
                    progressBarCalpH4?.visibility = View.VISIBLE
                    progressBarCalpH10?.visibility = View.VISIBLE
                    progressBarCalDO0?.visibility = View.VISIBLE
                    progressBarCalTDSLow?.visibility = View.VISIBLE
                    progressBarCalTDSDry?.visibility = View.VISIBLE
                    progressBarCalDoAtm?.visibility = View.VISIBLE
                    progressBarCalTDSHigh?.visibility = View.VISIBLE
                    progressBarCalClear?.visibility = View.VISIBLE

                    tv_cal_ph4?.isEnabled = true
                    tv_cal_ph10?.isEnabled = true
                    tv_cal_ph7?.isEnabled = true
                    tv_cal_do0?.isEnabled = true
                    tv_cal_tdslow?.isEnabled = true
                    tv_cal_tdsdry?.isEnabled = true
                    tv_cal_doatm?.isEnabled = true
                    tv_cal_tdshigh?.isEnabled = true
                    tv_cal_clear?.isEnabled = true
                    ImgBtn_back_arrow?.isEnabled = true

                    progressBarCalpH7?.progress = 1
                    progressBarCalpH4?.progress = 1
                    progressBarCalpH10?.progress = 1
                    progressBarCalDO0?.progress = 1
                    progressBarCalDoAtm?.progress = 1
                    progressBarCalTDSDry?.progress = 1
                    progressBarCalTDSLow?.progress = 1
                    progressBarCalTDSHigh?.progress = 1
                    progressBarCalClear?.progress = 1

                    ImgBtn_back_arrow?.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
                    tv_cal_ph4?.setTextColor(resources.getColor(R.color.colorPrimary))
                    tv_cal_ph7?.setTextColor(resources.getColor(R.color.colorPrimary))
                    tv_cal_ph10?.setTextColor(resources.getColor(R.color.colorPrimary))
                    tv_cal_do0?.setTextColor(resources.getColor(R.color.colorPrimary))
                    tv_cal_tdslow?.setTextColor(resources.getColor(R.color.colorPrimary))
                    tv_cal_tdsdry?.setTextColor(resources.getColor(R.color.colorPrimary))
                    tv_cal_doatm?.setTextColor(resources.getColor(R.color.colorPrimary))
                    tv_cal_tdshigh?.setTextColor(resources.getColor(R.color.colorPrimary))
                    tv_cal_clear?.setTextColor(resources.getColor(R.color.colorPrimary))
                })
        }.start()
    }

    // Forget wifi network if we clear the current activity
    override fun onPause() {
        super.onPause()
        wifiManagerWrapper?.forgetWifi(clickedDeviceID, this)
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
        Log.i("Calibration", "Wi-fi Failure Result*****************= $results")
        wifiScanResultList = emptyList()
        wifiScanResultList = results
        checkDeviceConnected(wifiScanResultList)

    }

    override fun wifiSuccessResult(results: List<ScanResult>) {
        Log.i("Calibration", "Wi-fi Success Result*****************= $results")
        wifiScanResultList = emptyList()
        wifiScanResultList = results
        //Check Available Devices
        checkDeviceConnected(wifiScanResultList)

    }

    override fun wifiConnectionStatusChangedResult() {
        Log.i("Calibration", "************Connection Status Changed Result************")
        checkDeviceConnected(wifiScanResultList)

    }

    private fun checkDeviceConnected(wifiScanResultListCheck: List<ScanResult>): Boolean? {
        for (index in wifiScanResultListCheck.indices) {
            return if (wifiManagerWrapper?.isConnectedTo(wifiScanResultListCheck[index].SSID)!!) {
                wifiScanResultList[index].capabilities = "Connected"

                Log.i("Calibration", "Connected")

                true
            } else {
                wifiScanResultList[index].capabilities = "Connection not established"
                Log.i("Calibration", "Connection not established")

                false
            }
        }
        return null
    }

}
