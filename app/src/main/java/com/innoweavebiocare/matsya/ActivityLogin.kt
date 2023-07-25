package com.innoweavebiocare.matsya

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ListenableWorker
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation
import com.amplifyframework.api.ApiException
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.api.rest.RestResponse
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.options.AuthConfirmResetPasswordOptions
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.core.Amplify
import com.innoweavebiocare.matsya.database.DatabaseHelper
import com.innoweavebiocare.matsya.database.model.deviceID
import com.innoweavebiocare.matsya.database.model.userData
import com.innoweavebiocare.matsya.workers.*
import okio.internal.commonAsUtf8ToByteArray


class ActivityLogin : AppCompatActivity() {
    private var etEmail:EditText? = null
    private var etPassword:EditText? = null
    private var btnLogin:Button? = null
    private var tvNoAccount:TextView? = null
    private var tvForgetPass:TextView? = null
    private var loadingIcon:ImageView? = null
    private val dbHandler:DatabaseHelper = DatabaseHelper(this)
    private val cognitoUserPool: CognitoUserPool? = null
    private var mForgetPassword: ForgotPasswordContinuation? = null

    @SuppressLint("MissingInflatedId", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val cognito: Cognito = Cognito(applicationContext)

        // It makes transparent navigation bar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        loadingIcon = findViewById(R.id.loadingIcon)

        // It compare if Email and pass is not null then enable Login button
        etPassword?.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(etEmail?.text.toString().isNotEmpty() && etPassword?.text.toString().isNotEmpty()){
                    btnLogin!!.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        btnLogin?.setOnClickListener{
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail?.text.toString()).matches()) {
                if(etPassword?.text.toString().isNotEmpty()){
                    dbHandler.deleteAllDbData() // Clear DB
                    loading() // Start Loading Screen
                    getUserDetailsFromCloud(etEmail?.text.toString()) // Get stored user details from Cloud
                }else{
                    btnLogin!!.isEnabled = false
                    etPassword?.error = "Field can't be empty"
                }
            }else{
                etEmail?.error = "Invalid email, eg: xyz@gmail.com"
            }
        }

        // ToDo: Handle Failure Cases Gracefully

        tvNoAccount = findViewById<TextView>(R.id.txtNotAccount)
        tvNoAccount?.setOnClickListener {
            val intent = Intent(this, ActivitySignUp::class.java)
            startActivity(intent)
            finish()
        }

        tvForgetPass = findViewById(R.id.txtForgetPass)
        tvForgetPass?.setOnClickListener {
            val dialog = Dialog(this@ActivityLogin)
            dialog.setContentView(R.layout.dialog_forgot_password_activity)
            dialog.show()
            dialog.setCanceledOnTouchOutside(false)
            val btnDialogCancel: Button = dialog.findViewById(R.id.btnDialogCancel)
            val btnDialogReset: Button = dialog.findViewById(R.id.btnDialogReset)
            val btnDialogOtp: Button = dialog.findViewById(R.id.btnDialogOtp)
            val etDialogUsername: EditText = dialog.findViewById(R.id.etDialogUsername)
            val etDialogPassword: EditText = dialog.findViewById(R.id.etDialogPassword)
            val etDialogOtp: EditText = dialog.findViewById(R.id.etDialogOtp)
            val etDialogConfirmPassword: EditText = dialog.findViewById(R.id.etDialogConfirmPassword)

            btnDialogOtp.setOnClickListener {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(etDialogUsername.text.toString()).matches()) {
                    Amplify.Auth.resetPassword(etDialogUsername.text.toString(),
                        {
                            Log.i("AuthQuickstart", "Password reset OK: $it")
                        },
                        {
                            Log.e("AuthQuickstart", "Password reset failed", it)
                        }
                    )
                    btnDialogOtp.visibility = View.INVISIBLE
                    etDialogUsername.visibility = View.INVISIBLE
                    etDialogOtp.visibility = View.VISIBLE
                    etDialogPassword.visibility = View.VISIBLE
                    etDialogConfirmPassword.visibility = View.VISIBLE
                    btnDialogReset.visibility = View.VISIBLE
                    btnDialogCancel.visibility = View.VISIBLE

                } else{
                    etDialogUsername.error = "Invalid email, eg: xyz@gmail.com"
                }
            }

            etDialogConfirmPassword.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(etDialogPassword.text.toString().length > 9){
                    }else{
                        etDialogPassword.error = "Length must be at least 10 characters."
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            btnDialogReset.setOnClickListener {
                if(etDialogPassword.text.toString() == etDialogConfirmPassword.text.toString()){
                    Amplify.Auth.confirmResetPassword(
                        etDialogPassword.text.toString(),
                        etDialogOtp.text.toString(),
                        AuthConfirmResetPasswordOptions.defaults(),
                        {
                            Log.i("AuthQuickstart","New password confirmed")
                            Toast.makeText(this, "Password reset successfully, Please login with new password", Toast.LENGTH_LONG).show()
                        },
                        { error: AuthException ->
                            Toast.makeText(this, error.message.toString(), Toast.LENGTH_SHORT).show()
                            Log.e("AuthQuickstart", error.toString())
                        }
                    )
                    dialog.dismiss()
                }
                else{
                    etDialogConfirmPassword.error = "Password do not match"
                }
            }

            btnDialogCancel.setOnClickListener {
                dialog.dismiss()
            }
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

    private fun loading(){
        loadingIcon?.visibility = View.VISIBLE
    }

    private fun cancelLoading(){
        loadingIcon?.visibility = View.INVISIBLE
    }

    private fun onSuccessSignIn(result: AuthSignInResult) {
        Handler(Looper.getMainLooper()).post {
            if(result.isSignInComplete) {
                Log.i("MyAmplifyApp", "Sign in succeeded")
                Toast.makeText(this, "Login Succeeded", Toast.LENGTH_SHORT).show()
                saveLoginDetailsOnCloud(etEmail?.text.toString())
                getPreRegisteredDevicesFromCloud(etEmail?.text.toString()) // Get Pre Registered device data from cloud
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                cancelLoading()
                startActivity(intent)
                finish()
                etEmail?.text?.clear()
                etPassword?.text?.clear()
            }
        }
    }

    private fun saveLoginDetailsOnCloud(emailId: String){
        val sysAndroidID: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val currentTimeStamp = System.currentTimeMillis() / 1000 // get current timeStamp

        try {
            val option = RestOptions.builder()
                .addPath("/apihandler")
                .addBody(
                    "{\"email\":\"$emailId\", \"androidId\":\"$sysAndroidID\", \"timeStamp\":\"$currentTimeStamp\", \"pass\":\"a2d4735b-f520-43d4-965d-9ec86b14ea9a\", \"request\":\"loginDetails\"}"
                        .toByteArray(Charsets.UTF_8)
                )
                .build()

            Amplify.API.post(option,
                { response ->
                    onSuccessSaveLoginDetails(response)
                },
                {
                    onFailureSaveLoginDetails(it)
                })

            ListenableWorker.Result.success()
        } catch (throwable: Throwable) {
            Log.e("saveLoginDetailsOnCloud", "Error Syncing ")
            ListenableWorker.Result.failure()
        }
    }

    private fun onSuccessSaveLoginDetails(response: RestResponse) {
        Handler(Looper.getMainLooper()).post {
            Log.i("onSuccessSaveLoginDetails", response.toString())
            if(response.code.isSuccessful){
                Log.i("onSuccessSaveLoginDetails", "Login Data added successfully")
            }
        }
    }

    private fun onFailureSaveLoginDetails(apiException: ApiException) {
        Log.e("onFailureSaveLoginDetails", "POST failed", apiException)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getUserDetailsFromCloud(emailId: String) {
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
                    onSuccessGetUserDetails(response)
                },
                {
                    onFailureGetUserDetails(it)
                })

            // 10 seconds handler for cancel loading screen
            Handler().postDelayed({
                cancelLoading()
            }, 10000)
            ListenableWorker.Result.success()
        } catch (throwable: Throwable) {
            Log.e("getUserDetailsFromCloud", "Error Syncing ")
            ListenableWorker.Result.failure()
        }
    }

    private fun onSuccessGetUserDetails(response: RestResponse) {
        Log.i("onSuccessGetUserDetails", "Successfully retrieved user details from cloud")

        Handler(Looper.getMainLooper()).post {
            var newUserData: userData? = null
            val jsonObject = response.data.asJSONObject()
            val deviceDataJSONArray = jsonObject.getJSONArray("Items")
            val listData = mutableListOf<userData>()
            val sysAndroidID: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

            for (i in 0 until deviceDataJSONArray.length()) {
                val item = deviceDataJSONArray.getJSONObject(i)
                val phoneNo = item.getString("PHONE")
                val androidID = item.getString("ANDROID_ID")
                val emailID = item.getString("EMAIL_ID")
                val name = item.getString("NAME")

                if (androidID != sysAndroidID) {
                    newUserData = userData(name, emailID, phoneNo, sysAndroidID)
                    Handler(Looper.getMainLooper()).post {
                        onAndroidIDConflict(emailID, name, phoneNo, sysAndroidID, androidID)
                    }
                } else {
                    newUserData = userData(name, emailID, phoneNo, androidID)
                    Handler(Looper.getMainLooper()).post {
                        Amplify.Auth.signIn(
                            etEmail?.text.toString().trim(),
                            etPassword?.text.toString(),
                            { result ->
                                onSuccessSignIn(result)
                            },
                            {
                                onFailureSignIn(it)
                            }
                        )
                    }
                }
                listData.add(newUserData)
                dbHandler.insertUserData(listData)
            }
        }
    }

    private fun onFailureGetUserDetails(apiException: ApiException) {
        Handler(Looper.getMainLooper()).post {
            cancelLoading()
            Toast.makeText(this, "Server error!!", Toast.LENGTH_LONG).show()
        }
        Log.e("onFailureGetUserDetails", "POST failed", apiException)
    }

    // Dialog
    private fun onAndroidIDConflict(
        emailID: String,
        name: String,
        phoneNo: String,
        newAndroidId: String,
        oldAndroidId: String
    ) {
        val dialog = Dialog(this@ActivityLogin)
        dialog.setContentView(R.layout.dialog_login_status)
        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
        val dialogYesBtn: Button = dialog.findViewById(R.id.dialogYesBtn)
        val dialogNoBtn: Button = dialog.findViewById(R.id.dialogNoBtn)
        val tvDialogHeading: TextView = dialog.findViewById(R.id.tvDialogHeading)
        val tvDialogInstruction: TextView = dialog.findViewById(R.id.tvDialogInstruction)
        val tvPleaseWait: TextView = dialog.findViewById(R.id.tvPleaseWait)
        val progressLoading: ProgressBar = dialog.findViewById(R.id.progressLoading)

        dialogYesBtn.isEnabled = true
        dialogNoBtn.isEnabled = true

        dialogYesBtn.setOnClickListener{
            dialogYesBtn.visibility = View.GONE
            dialogNoBtn.visibility = View.GONE
            tvDialogHeading.visibility = View.GONE
            tvDialogInstruction.visibility = View.GONE
            tvPleaseWait.visibility = View.VISIBLE
            progressLoading.visibility = View.VISIBLE

            try {
                val option = RestOptions.builder()
                    .addPath("/apihandler")
                    .addBody(
                        "{\"email\":\"$emailID\", \"name\":\"$name\", \"phone\":\"$phoneNo\", \"androidId\":\"$newAndroidId\", \"oldAndroidId\":\"$oldAndroidId\", \"pass\":\"a2d4735b-f520-43d4-965d-9ec86b14ea9a\", \"request\":\"updateAndroidId\"}"
                            .toByteArray(Charsets.UTF_8)
                    )
                    .build()

                Amplify.API.post(option,
                    { response ->
                        onSuccessModifyUserAndroidID(response, dialog)
                    },
                    {
                        onFailureModifyUserAndroidID(it, dialog)
                    })

                Log.i("onAndroidIDConflict", "Added user data successfully on cloud")
                ListenableWorker.Result.success()
            } catch (throwable: Throwable) {
                Log.e("onAndroidIDConflict", "Error ")
                ListenableWorker.Result.failure()
            }
        }
        dialogNoBtn.setOnClickListener {
            dialog.dismiss()
            cancelLoading()
        }
    }

    private fun onSuccessModifyUserAndroidID(response: RestResponse, dialog: Dialog) {
        Handler(Looper.getMainLooper()).post {
            Log.i("onSuccessModifyUserAndroidID", response.toString())
            if(response.code.isSuccessful){
                Amplify.Auth.signIn(
                    etEmail?.text.toString().trim(),
                    etPassword?.text.toString(),
                    { result ->
                        onSuccessSignIn(result)
                    },
                    {
                        onFailureSignIn(it)
                    }
                )
                Log.i("onSuccessModifyUserAndroidID", "user Data added successfully")
            }
            else if(response.code.isServiceFailure){
                dialog.dismiss()
                Toast.makeText(this, "Server problem, please try after some time", Toast.LENGTH_LONG).show()
                Log.i("onSuccessModifyUserAndroidID", "Server problem, please try after some time")
            }
            else if(response.code.isClientError){
                dialog.dismiss()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
                Log.i("onSuccessModifyUserAndroidID", "Something went wrong")
            }
        }
    }

    private fun onFailureModifyUserAndroidID(apiException: ApiException, dialog: Dialog) {
        Handler(Looper.getMainLooper()).post {
            Log.e("onFailureModifyUserAndroidID", "POST failed", apiException)
            dialog.dismiss()
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getPreRegisteredDevicesFromCloud(emailId: String) {
        try {
             val option = RestOptions.builder()
                    .addPath("/apihandler")
                    .addBody(
                        ("{\"email\": \"" + emailId + "\", " +
                                "\"pass\": \"a2d4735b-f520-43d4-965d-9ec86b14ea9a\", " +
                                "\"request\": \"getRegisteredDevices\"}").commonAsUtf8ToByteArray()
                    )
                    .build()


                Amplify.API.post(option,
                    { response ->
                        onSuccessGetPreRegisteredDevicesFromCloud(response)
                    },
                    {
                        onFailureGetPreRegisteredDevicesFromCloud(it)
                    })

            Log.i("getPreRegisteredDevicesFromCloud", "Successfully get registered device data")
            ListenableWorker.Result.success()
        } catch (throwable: Throwable) {
            Log.e("getPreRegisteredDevicesFromCloud", "Error ")
            ListenableWorker.Result.failure()
        }
    }

    private fun onSuccessGetPreRegisteredDevicesFromCloud(response: RestResponse) {
        var newDeviceData: deviceID? = null
        val jsonObject = response.data.asJSONObject()
        val deviceDataJSONArray = jsonObject.getJSONArray("Items")
        val listData = mutableListOf<deviceID>()
        for (i in 0 until deviceDataJSONArray.length()) {
            val item = deviceDataJSONArray.getJSONObject(i)
            val deviceId = item.getString("DEVICE_ID")
            val pondName = item.getString("POND_NAME")
            val emailId = item.getString("EMAIL_ID")

            newDeviceData = deviceID(deviceId, emailId, pondName)

            Log.i("Device Registration Data", "Data Inserted: ${deviceId}, " +
                        "${pondName}, $emailId")

            listData.add(newDeviceData)

        }
        dbHandler.insertRegisteredDevices(listData)
    }

    private fun onFailureGetPreRegisteredDevicesFromCloud(apiException: ApiException) {
        Log.e("onFailureGetPreRegisteredDevicesFromCloud", "POST failed", apiException)
    }

    private fun onFailureSignIn(err: AuthException) {
        Handler(Looper.getMainLooper()).post {
            cancelLoading()
            Toast.makeText(this, "Please Verify Credentials! ${err.message}", Toast.LENGTH_SHORT)
                .show()
            Log.i("MyAmplifyApp", "Sign in Failed")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            );
        }
    }
}

