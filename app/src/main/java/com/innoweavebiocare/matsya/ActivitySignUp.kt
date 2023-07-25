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
import com.amplifyframework.api.ApiException
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.api.rest.RestResponse
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.core.Amplify
import com.hbb20.CountryCodePicker
import com.innoweavebiocare.matsya.database.DatabaseHelper
import com.innoweavebiocare.matsya.database.model.userData

class ActivitySignUp : AppCompatActivity() {
    var etUserName: EditText? = null
    var etEmail: EditText? = null
    var etPass: EditText? = null
    var etRepeatPass: EditText? = null
    var etMobile: EditText? = null
    var btnSignUp: Button? = null
    var btnBackLogin: TextView? = null
    var loadingIcon:ImageView? = null
    var txtOTP: TextView? = null
    var countryCode = ""

    private val dbHandler: DatabaseHelper = DatabaseHelper(this)


    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

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

        etUserName = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPass = findViewById(R.id.etPassword)
        etRepeatPass = findViewById(R.id.etRepeatPass)
        etMobile = findViewById(R.id.etMobile)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnBackLogin = findViewById(R.id.btnBackLogin)
        loadingIcon = findViewById(R.id.loadingIcon)
        txtOTP = findViewById(R.id.txtOTP)
        var CountryCodePicker: CountryCodePicker = findViewById(R.id.countyCodePicker)



        etMobile?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(etUserName?.text.toString().isNotEmpty() && etEmail?.text.toString().isNotEmpty()
                    && etPass?.text.toString().isNotEmpty() && etRepeatPass?.text.toString().isNotEmpty()
                    && etMobile?.text.toString().isNotEmpty()){
                    btnSignUp!!.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        countryCode = CountryCodePicker.selectedCountryCode

        CountryCodePicker.setOnCountryChangeListener {

            val countryName = CountryCodePicker.selectedCountryName
            countryCode = CountryCodePicker.selectedCountryCode
            val countryCodeName = CountryCodePicker.selectedCountryNameCode
            val countryCodeWithPlus = CountryCodePicker.selectedCountryCodeWithPlus

            Toast.makeText(this, "$countryName, $countryCode, $countryCodeName, $countryCodeWithPlus", Toast.LENGTH_SHORT).show()

        }

        btnSignUp!!.setOnClickListener {
            if(etUserName?.text.toString().isNotEmpty()){
            }else{
                cancelLoading()
                etUserName?.error = "Field can't be empty"
            }
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail?.text.toString()).matches()){
            } else{
                cancelLoading()
                etEmail?.error = "Invalid email, eg: xyz@gmail.com"
            }
            if(etPass?.text.toString().length > 9){
            }else{
                cancelLoading()
                etPass?.error = "Length must be at least 10 characters."
            }
            if(etPass?.text.toString() == etRepeatPass?.text.toString()){
            } else{
                cancelLoading()
                etRepeatPass?.error = "Password do not match"
            }
            if(android.util.Patterns.PHONE.matcher(etMobile?.text.toString()).matches()
                && etMobile?.text.toString().length == 10){
            } else {
                cancelLoading()
                if (etMobile?.text.toString().length < 10 || etMobile?.text.toString().length > 10) {
                    etMobile?.error = "Please enter 10 digit phone No."
                } else {
                    cancelLoading()
                    etMobile?.error = "Invalid phone No."
                }
            }

            if (etUserName?.text.toString().isNotEmpty() && etMobile?.text.toString().length == 10
                && etEmail?.text.toString().isNotEmpty() && etRepeatPass?.text.toString().isNotEmpty()
                && etUserName?.text.toString().isNotEmpty() && etRepeatPass?.text.toString()
                == etPass?.text.toString())
            {
                btnSignUp!!.isEnabled = true
                txtOTP?.visibility = View.VISIBLE
                    loading()
                    Amplify.Auth.signUp(
                        etEmail?.text.toString().trim(),
                        etPass?.text.toString(),
                        AuthSignUpOptions.builder().userAttribute(
                            AuthUserAttributeKey.email(), etEmail?.text.toString()
                        ).build(),
                        {
                            Looper.prepare()
                            onSuccess()
                            Looper.loop()
                        },
                        {
                            onFailure(it)
                        }
                    )
                }
        }

        // ToDo: Handle Failure Cases Gracefully
        // ToDo: Handle Multiple Sign Up capability from single Email, Use email as Username
        // ToDo: Get Mobile number from the User
        btnBackLogin?.setOnClickListener {
            val intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)
            finish()
        }

        txtOTP?.setOnClickListener {
            onSuccess()
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

    //flashScreen
    private fun loading(){
        loadingIcon?.visibility = View.VISIBLE
    }

    //Invisible FlashScreen
    private fun cancelLoading(){
        loadingIcon?.visibility = View.INVISIBLE
    }

    private fun onSuccess(){
        loadingIcon?.visibility = View.INVISIBLE
        val dialog = Dialog(this@ActivitySignUp)
        dialog.setContentView(R.layout.dialog_otp_activity)
        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
        val btnDialogVerify: Button = dialog.findViewById(R.id.btnDialogVerify)
        val tvResendOTP: TextView = dialog.findViewById(R.id.tvResendOTP)
        val etDialogOTP: EditText = dialog.findViewById(R.id.etDialogOTP)
        btnDialogVerify.setOnClickListener {
            Amplify.Auth.confirmSignUp(
                    etEmail?.text.toString().trim(), etDialogOTP.text.toString(),
                    { result ->
                        onSuccessOTP(result)
                    },
                    {
                        onFailureOTP(it)
                    })
        }
        tvResendOTP.setOnClickListener {
            Amplify.Auth.resendSignUpCode(
                etEmail?.text.toString().trim(),
                { result ->
                    // Handle successful resend
                    Toast.makeText(this, "OTP resent successfully", Toast.LENGTH_SHORT).show()
                },
                { error ->
                    // Handle resend failure
                    Toast.makeText(this, "OTP resend failed", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun onFailure(err: AuthException){
        Looper.prepare()
        cancelLoading()
        Log.e("MyAmplifyApp", "Sign up failed ${err.message}")
        Toast.makeText(this,"${err.message}", Toast.LENGTH_LONG).show()
        Looper.loop()
    }
    
    private fun onSuccessOTP(result : AuthSignUpResult){
        Looper.prepare()
        if (result.isSignUpComplete) {
            Log.i("MyAmplifyApp", "User Activated")
            Toast.makeText(this,"User Activated", Toast.LENGTH_LONG).show()
            insertUserdata()
            val intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)
            finish()

        } else {
            Log.i("MyAmplifyApp","Confirm sign up not complete")
            Toast.makeText(this,"Confirm sign up not complete", Toast.LENGTH_LONG).show()
        }
        Looper.loop()
    }

    fun insertUserdata(){
        val androidId: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val phone = "+" + countryCode + etMobile?.text.toString()
        val userData = userData(etUserName?.text.toString(), etEmail?.text.toString(), phone, androidId)
        val userDataList = mutableListOf<userData>()
        userDataList.add(userData)
        addUserDetailsOnCloud(etUserName?.text.toString(), etEmail?.text.toString(), phone, androidId, userDataList)
    }

    private fun onFailureOTP(it: AuthException){
        Looper.prepare()
        Log.e("MyAmplifyApp", "Failed to confirm sign up", it)
        Toast.makeText(this,"${it.message}", Toast.LENGTH_LONG).show()
        Looper.loop()
    }

    private fun addUserDetailsOnCloud(
        userName: String,
        email: String,
        phoneNo: String,
        androidId: String,
        userDataList: MutableList<userData>
    ) {
        try {
            val option = RestOptions.builder()
                .addPath("/apihandler")
                .addBody(
                    "{\"email\":\"$email\", \"name\":\"$userName\", \"phone\":\"$phoneNo\", \"androidId\":\"$androidId\", \"pass\":\"a2d4735b-f520-43d4-965d-9ec86b14ea9a\", \"request\":\"addUser\"}"
                        .toByteArray(Charsets.UTF_8)
                )
                .build()

            Amplify.API.post(option,
                { response ->
                    onSuccessAddUserDetailsOnCloud(response, userDataList)
                },
                {
                    onFailureAddUserDetailsOnCloud(it)
                })

            Log.i("addUserDataOnCloud", "Added user data successfully on cloud")
            ListenableWorker.Result.success()
        } catch (throwable: Throwable) {
            Log.e("addUserDataOnCloud", "Error ")
            ListenableWorker.Result.failure()
        }
    }

    private fun onSuccessAddUserDetailsOnCloud(response: RestResponse, userDataList: MutableList<userData>) {
        Handler(Looper.getMainLooper()).post {
            Log.i("onSuccessAddUserDataOnCloud", response.toString())
            if(response.code.isSuccessful){
                dbHandler.insertUserData(userDataList)
                Log.i("onSuccessAddUserDataOnCloud", "user Data added successfully")
            }
            else if(response.code.isServiceFailure){
                Log.i("onSuccessAddUserDataOnCloud", "Server problem, please try after some time")
            }
            else if(response.code.isClientError){
                Log.i("onSuccessAddUserDataOnCloud", "Something went wrong")
            }
        }
    }

    private fun onFailureAddUserDetailsOnCloud(apiException: ApiException) {
        Log.e("onFailureAddUserDataOnCloud", "POST failed", apiException)
    }
}