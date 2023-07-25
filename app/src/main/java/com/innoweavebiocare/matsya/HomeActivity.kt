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
// File Name: HomeActivity.kt
// File Description: this page content the recycler
// view for the live data and fab button for scanning
// of qrcode and menu for moving to range notification
// and logout
// Author: Anshul Malviya
// Date: April 10, 2023
/////////////////////////////////////////////////////

package com.innoweavebiocare.matsya

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.InputFilter
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.*
import com.amplifyframework.api.ApiException
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.api.rest.RestResponse
import com.amplifyframework.core.Amplify
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.integration.android.IntentIntegrator
import com.innoweavebiocare.matsya.database.DatabaseHelper
import com.innoweavebiocare.matsya.database.model.deviceID
import com.innoweavebiocare.matsya.recyclerviewDataModel.DeviceData
import com.innoweavebiocare.matsya.workers.dataSyncWorker
import com.innoweavebiocare.matsya.database.model.rangeNotificationData
import com.innoweavebiocare.matsya.database.model.sensorData
import com.journeyapps.barcodescanner.CaptureActivity
import kotlinx.android.synthetic.main.dialog_add_pond_activity.*
import org.json.JSONException
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.innoweavebiocare.matsya.workers.makeNotificationwithId
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_carousel_calibration.*
import okio.internal.commonAsUtf8ToByteArray


class HomeActivity() : AppCompatActivity() {
    private var fabBtn:FloatingActionButton? = null
    private var progressBar: ProgressBar? = null
    private var progressDo: TextView? = null
    private var tvTemp: TextView? = null
    private var tvPH: TextView? = null
    private var tvTDS: TextView? = null

    private val dbHandler: DatabaseHelper = DatabaseHelper(this)
    private lateinit var recv: RecyclerView
    private lateinit var pondList:ArrayList<DeviceData>
    private lateinit var deviceDataAdapter: DeviceDataAdapterActivity
    private var swipeRefresh: SwipeRefreshLayout? = null

    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 1000
    var countForAddPondDialog = 0 // For macID validation

    @SuppressLint("InvalidPeriodicWorkRequestInterval", "MissingInflatedId", "ResourceType",
        "SetTextI18n"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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

        fabBtn = findViewById(R.id.fabBtn)
        recv = findViewById(R.id.mRecyclerView)
        progressBar = findViewById(R.id.progress_bar) // Progress round bar
        progressDo = findViewById(R.id.progress_text_do)
        tvTemp = findViewById(R.id.tvTemp)
        tvPH = findViewById(R.id.tvPH)
        tvTDS = findViewById(R.id.tvTDS)
        swipeRefresh= findViewById(R.id.swipeRefresh)

        // One time work request for dataSyncWorker
        val workManager = WorkManager.getInstance(application)
        val oneTimeWorkManager = OneTimeWorkRequest.Builder(dataSyncWorker::class.java)
            .build()
        workManager.enqueue(oneTimeWorkManager)

        // swipe down for one time datasync work request
        swipeRefresh!!.setOnRefreshListener {
            Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
            val oneTimeWorkManager = OneTimeWorkRequest.Builder(dataSyncWorker::class.java) .build()
            WorkManager.getInstance(applicationContext).enqueue(oneTimeWorkManager)
            swipeRefresh!!.isRefreshing = false
        }

        // for set actionbar menu and icon
        setSupportActionBar(findViewById(R.id.my_toolbar))

        // set black color status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Fab Button On click Listener for adding new devices
        fabBtn?.setOnClickListener{
           cameraTask()
        }

        // Recycler View code
        pondList = ArrayList()
        deviceDataAdapter = DeviceDataAdapterActivity(this,pondList) // set Adapter
        recv.layoutManager = LinearLayoutManager(this)// setRecycler view Adapter
        recv.adapter = deviceDataAdapter

        // Click listener for item of an recycler view
        deviceDataAdapter.setOnItemClickListner(object : DeviceDataAdapterActivity.onItemClickListner{
            override fun onItemClick(position: Int) {
                val intent =Intent(this@HomeActivity,DashBoardActivity::class.java)
                intent.putExtra("DEVICEID", pondList.get(position).macID)
                intent.putExtra("DEVICENAME",pondList.get(position).pondName)
                startActivity(intent)
            }
        })

        // If rangeNotifyDB is empty then create and Insert data in db
        val rangeList = dbHandler.allRangeNotifyData
        if(rangeList.isEmpty()) {
            dbHandler.insertRangeNotifyData(rangeNotificationData("1", sensorData.COLUMN_DO, 4.0F, 10.0F))
            dbHandler.insertRangeNotifyData(rangeNotificationData("2", sensorData.COLUMN_PH, 4.0F, 9.0F))
            dbHandler.insertRangeNotifyData(rangeNotificationData("3", sensorData.COLUMN_TDS, 0.0F, 100000.0F))
            dbHandler.insertRangeNotifyData(rangeNotificationData("4", sensorData.COLUMN_TEMP, 20.0F, 40.0F))
        }

        val devices = dbHandler.allRegisteredDeviceIDs
        // Check if the foreground service is already running
        if (!isServiceRunning(ForegroundService::class.java) && devices.isNotEmpty()) {
            val serviceIntent = Intent(this, ForegroundService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }

        //using shared preference to show the walkthrough only once
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val appCarouselSeqCompleted = sharedPreferences.getBoolean("app_walkthrough_sequence_completed", false)
        if (!appCarouselSeqCompleted) {
            // Run the TapTargetSequence if it hasn't been completed yet
            showAppWalkthrough()
        }

        if(dbHandler.allUserData.isNotEmpty()){
            validateUserAndroidID(dbHandler.allUserData[0].email) // get android id for checking if account is logged in other device or not
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun validateUserAndroidID(emailId: String) {
        try {
            val option = RestOptions.builder()
                .addPath("/apihandler")
                .addBody(
                    ("{\"email\": \"" + emailId + "\", " +
                            "\"pass\": \"a2d4735b-f520-43d4-965d-9ec86b14ea9a\", " +
                            "\"request\": \"getUser\"}").commonAsUtf8ToByteArray()
                )
                .build()

            Amplify.API.post(option,
                { response ->
                    onSuccessValidateUserAndroidID(response)
                },
                {
                    onFailureValidateUserAndroidID(it)
                })

            Log.i("validateUserAndroidID", "Successfully get user data")
            ListenableWorker.Result.success()
        } catch (throwable: Throwable) {
            Log.e("validateUserAndroidID", "Error Syncing ")
            ListenableWorker.Result.failure()
        }
    }

    private fun onSuccessValidateUserAndroidID(response: RestResponse) {
        val jsonObject = response.data.asJSONObject()
        val deviceDataJSONArray = jsonObject.getJSONArray("Items")
        val sysAndroidID: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        for (i in 0 until deviceDataJSONArray.length()) {
            val item = deviceDataJSONArray.getJSONObject(i)
            val cloudDBAndroidID = item.getString("ANDROID_ID")
            if(cloudDBAndroidID != sysAndroidID){
                Handler(Looper.getMainLooper()).post {

                    makeNotificationwithId( "Login Status", 500,
                        "Did you just sign in?",
                        "Your account has been logged in on another device.",
                        applicationContext)

                    signOut()
                }
            }
        }
    }

    private fun onFailureValidateUserAndroidID(apiException: ApiException) {
        Log.e("onFailureValidateUserAndroidID", "POST failed", apiException)
    }

    private fun signOut(){
        // Stop foreground services
        val serviceIntent = Intent(this, ForegroundService::class.java)
        applicationContext.stopService(serviceIntent)

        val intent = Intent(this, ActivityLogin::class.java)
        Amplify.Auth.signOut(
            {
                Log.i("amplify-app","Signed out")
                dbHandler.deleteAllDbData()
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                finish()
            },
            {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                Log.e("amplify-app", "Failed to sign out", it)
            }
        )
        startActivity(intent)
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = manager.getRunningServices(Integer.MAX_VALUE)

        for (service in services) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }

        return false
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.item_menu_for_home_activity,menu)
        return true
    }

    // Functioning for Action bar menu item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sensorRange -> {
                val intent = Intent(this, RangeNotificationActivity::class.java)
                startActivity(intent)
            }
            R.id.setting -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
            }
            R.id.signOut -> { // Logout
                signOut()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun hasCameraAccess(): Boolean {
        return EasyPermissions.hasPermissions(this, android.Manifest.permission.CAMERA)
    }

    private fun cameraTask() {

        if (hasCameraAccess()) {

            var qrScanner = IntentIntegrator(this)
            qrScanner.setPrompt("scan a QR code")
            qrScanner.setCameraId(0)
            qrScanner.setOrientationLocked(true)
            qrScanner.setBeepEnabled(true)
            qrScanner.captureActivity = CaptureActivity::class.java
            qrScanner.initiateScan()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your camera for Scanning QR Code.",
                123,
                android.Manifest.permission.CAMERA
            )
        }
    }
    @SuppressLint("InvalidPeriodicWorkRequestInterval")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // TODO: Invalidate QR Code not from Innoweave

        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    val macId = result.contents.toString()

                    validateMacId(macId)

                } catch (exception: JSONException) {
                    Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }
    private fun validateMacId(macId: String) {
        try {
            val option = RestOptions.builder()
                .addPath("/apihandler")
                .addBody(
                    ("{\"macId\": \"" + macId + "\", " +
                            "\"pass\": \"a2d4735b-f520-43d4-965d-9ec86b14ea9a\", " +
                            "\"request\": \"validateDevice\"}").commonAsUtf8ToByteArray()
                )
                .build()

            Amplify.API.post(option,
                { response ->
                    onSuccessValidateMacId(response, macId)
                },
                {
                    onFailureValidateMacId(it)
                })

            Log.i("validateMacId", "Successfully synced the database")
            ListenableWorker.Result.success()
        } catch (throwable: Throwable) {
            Log.e("validateMacId", "Error Syncing ")
            ListenableWorker.Result.failure()
        }
    }

    private fun onSuccessValidateMacId(response: RestResponse, macId: String) {
        var deviceId = ""
        val jsonObject = response.data.asJSONObject()
        val deviceDataJSONArray = jsonObject.getJSONArray("Items")
        val alreadyRegisteredDevice = dbHandler.allRegisteredDeviceIDs

        for (i in 0 until deviceDataJSONArray.length()) {
            val item = deviceDataJSONArray.getJSONObject(i)
            deviceId = item.getString("DEVICE_ID")
        }

        Log.i("onSuccessValidateMacId", "Received MacID from cloud $deviceId")
        Log.i("onSuccessValidateMacId", "Scanned macId $macId")

        if (deviceId.isNotEmpty()) {
            Handler(Looper.getMainLooper()).post {
                if(alreadyRegisteredDevice.isNotEmpty()){
                    val deviceIds = alreadyRegisteredDevice.map { it.id.toString() }
                    if (!deviceIds.contains(deviceId)) {
                        addPondNameDialog(macId)
                    } else{
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(applicationContext, "Device Already Added.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else{
                    addPondNameDialog(macId)
                }
            }
        } else {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(applicationContext, "Invalid QR, Please scan correct QR-CODE", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onFailureValidateMacId(apiException: ApiException) {
        Log.e("onFailureValidateMacId", "POST failed", apiException)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addPondNameDialog(macId:String){
        val dialog = Dialog(this@HomeActivity)
        dialog.setContentView(R.layout.dialog_add_pond_activity)
        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
        val dialogCnBtn: Button = dialog.findViewById(R.id.dialogCnBtn)
        val dialogAddBtn: Button = dialog.findViewById(R.id.dialogAddBtn)
        val pondName: EditText = dialog.findViewById(R.id.pondName)
        val maxLength = 8
        pondName.filters += InputFilter.LengthFilter(maxLength)

        dialogAddBtn.setOnClickListener{
            if (pondName.text.toString() != "") {
                val newDevice = deviceID(macId,  dbHandler.allUserData[0].email.toString(), pondName.text.toString())
                dialog.dismiss()
                registerNewDeviceOnCloud(macId,  dbHandler.allUserData[0].email.toString(), pondName.text.toString(), newDevice)

                // One time work request for datasyncworker
                val initialDataSyncWorkManager = OneTimeWorkRequest.Builder(dataSyncWorker::class.java).build()
                WorkManager.getInstance(applicationContext).enqueue(initialDataSyncWorkManager)

                // Run foreground services
                val serviceIntent = Intent(this, ForegroundService::class.java)
                ContextCompat.startForegroundService(this, serviceIntent)

            }else{
                pondName.error = "Field can't be empty"
            }
        }
        dialogCnBtn.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerNewDeviceOnCloud(macId: String, emailId: String, pondName: String, newDevice: deviceID) {
        try {
            val option = RestOptions.builder()
                .addPath("/apihandler")
                .addBody(
                    "{\"macID\":\"$macId\", \"email\":\"$emailId\", \"pondName\":\"$pondName\", \"pass\":\"a2d4735b-f520-43d4-965d-9ec86b14ea9a\", \"request\":\"registerDevice\"}"
                        .toByteArray(Charsets.UTF_8)
                )
                .build()


            Amplify.API.post(option,
                { response ->
                    onSuccessRegisterNewDeviceOnCloud(response, newDevice)
                },
                {
                    onFailureRegisterNewDeviceOnCloud(it)
                })

            Log.i("registerNewDevice", "Successfully registered new device")
            ListenableWorker.Result.success()
        } catch (throwable: Throwable) {
            Log.e("registerNewDevice", "Error Syncing ")
            ListenableWorker.Result.failure()
        }
    }

    private fun onSuccessRegisterNewDeviceOnCloud(response: RestResponse, newDevice: deviceID) {
        Handler(Looper.getMainLooper()).post {
            Log.i("onSuccessRegisterNewDeviceOnCloud", response.toString())
            if(response.code.isSuccessful){
                dbHandler.insertRegisteredDevice(newDevice)
                Toast.makeText(applicationContext, "Device added successfully", Toast.LENGTH_SHORT).show()
            }
            else if(response.code.isServiceFailure){
                Toast.makeText(applicationContext, "Server problem, please try after some time", Toast.LENGTH_LONG).show()
            }
            else if(response.code.isClientError){
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onFailureRegisterNewDeviceOnCloud(apiException: ApiException) {
        Log.e("onFailureRegisterNewDeviceOnCloud", "POST failed", apiException)
    }

    // this function call in every 10 seconds
    @SuppressLint("InvalidPeriodicWorkRequestInterval", "NotifyDataSetChanged")
    override fun onResume() {
        handler.postDelayed(Runnable {
            runnable?.let { handler.postDelayed(it, delay.toLong()) }

            pondList.clear()
            val deviceList = dbHandler.allRegisteredDeviceIDs
            // Adding each device to the recycler view adapter list
            for(device in deviceList)
            {
                val latestData = dbHandler.getLatestSensorData(device.id)
                // If timeStamp is 0 then it will sent NA to recycler view adapter or else actual time and date
                var time = ""
                var date = ""
                if(latestData.senseTimeStamp.toInt() != 0){
                    date = getDateString(latestData.senseTimeStamp.toInt())
                    time = getTimeString(latestData.senseTimeStamp.toInt())
                } else {
                    date = "NA"
                    time = "NA"
                }

                pondList.add(DeviceData(device.id, device.pondName,
                    ((latestData.senseDO * 100.0).roundToInt() / 100.0).toFloat(), // Converting to 2 decimal for display
                    ((latestData.senseTemp * 100.0).roundToInt() / 100.0).toFloat(), // Converting to 2 decimal for display
                    ((latestData.sensePH * 100.0).roundToInt() / 100.0).toFloat(), // Converting to 2 decimal for display
                    latestData.senseTDS.toInt(),
                    date , // Get the Date in required format
                    time)) // Get the Time in required format

                deviceDataAdapter.notifyDataSetChanged()
            }

        }.also { runnable = it }, delay.toLong())
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        runnable?.let { handler.removeCallbacks(it) } //stop handler when activity not visible super.onPause();
    }

    // convert timeStamp to actual time and date for pond
    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("LLL dd, yyyy")
    private val stf = SimpleDateFormat("HH:mm aaa")
    private fun getDateString(time: Int) : String = sdf.format(time * 1000L)
    private fun getTimeString(time: Int) : String = stf.format(time * 1000L)

    //this function is used to show the app carousel
    private fun showAppWalkthrough() {
        val appCarouselDataCardImg = findViewById<ImageView>(R.id.appCarouselDataCardSample)

        TapTargetSequence(this)
            .target(
                fabBtn?.let { AppWalkthroughTargetsUtils(it,"Click to Add New Device","Add your new device easily by scanning the provided QR code to view data remotely",60) }
            ).target(
                AppWalkthroughTargetsUtils(my_toolbar.getChildAt(2) ,"App Settings","Click here to Set Sensor Range, Profile photo, Change password, etc",60)
            ).target(
                AppWalkthroughTargetsUtils(appCarouselDataCardImg, "Data from the field", "Data from each pond will be displayed and updated here",70)
            ).listener(object : TapTargetSequence.Listener {
                override fun onSequenceFinish() {
                    appCarouselDataCardImg.visibility = View.GONE
                    val intent = Intent(this@HomeActivity, CarouselDashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                    if (lastTarget?.id() == 2 && targetClicked) {
                        appCarouselDataCardImg.visibility = View.VISIBLE
                    }
                }
                override fun onSequenceCanceled(lastTarget: TapTarget?) {
                    appCarouselDataCardImg.visibility = View.GONE
                }
            }
            ).start()
    }
}