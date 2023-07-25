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
// File Name: SettingActivity.kt
// File Description: This activity content all setting
// option like change password, about, privacy policy,
// contact us.
// Author: Anshul Malviya
// Date: June 7, 2023
/////////////////////////////////////////////////////

package com.innoweavebiocare.matsya

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.options.AuthConfirmResetPasswordOptions
import com.amplifyframework.core.Amplify
import com.innoweavebiocare.matsya.database.DatabaseHelper
import java.io.ByteArrayOutputStream

class SettingActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
        private const val PREFS_NAME = "MyPrefs"
        private const val PROFILE_IMAGE_KEY = "profileImage"
    }

    private lateinit var ImageViewProfilePicture: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private val dbHandler: DatabaseHelper = DatabaseHelper(this)

    private var ImgBtn_back_arrow: ImageButton? = null
    private var layoutChangePassword: View? = null
    private var layoutSubscription: View? = null
    private var layoutPrivacyPolicy : View? = null
    private var layoutContactUs: View? = null
    private var layoutAbout: View? = null
    private var tvUsername: TextView? = null
    private var tvEmail: TextView? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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

        ImgBtn_back_arrow = findViewById(R.id.ImgBtn_back_arrow)
        layoutChangePassword = findViewById(R.id.layoutChangePassword)
        layoutSubscription = findViewById(R.id.layoutSubscription)
        layoutPrivacyPolicy = findViewById(R.id.layoutPrivacyPolicy)
        layoutContactUs = findViewById(R.id.layoutContactUs)
        layoutAbout = findViewById(R.id.layoutAbout)
        tvUsername = findViewById(R.id.tvUsername)
        tvEmail = findViewById(R.id.tvEmail)


        // set black color status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        ImageViewProfilePicture = findViewById(R.id.ImageViewProfilePicture)

        val btnPickProfileImageFromGallery = findViewById<ImageView>(R.id.btnPickProfileImageFromGallery)
        btnPickProfileImageFromGallery.setOnClickListener {
            dispatchPickFromGalleryIntent()
        }

        // Load saved profile image, if any
        val savedImage = sharedPreferences.getString(PROFILE_IMAGE_KEY, null)
        savedImage?.let {
            val bitmap = decodeBase64(savedImage)
            ImageViewProfilePicture.setImageBitmap(bitmap)
        }

        val userData = dbHandler.allUserData // Get userData from db
        tvUsername!!.text = userData[0].username
        tvEmail!!.text = userData[0].email

        // Back button
        ImgBtn_back_arrow?.setOnClickListener {
            val intent = Intent(this@SettingActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Change password
        layoutChangePassword?.setOnClickListener {
            val dialog = Dialog(this@SettingActivity)
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

            btnDialogReset.visibility = View.VISIBLE
            btnDialogCancel.visibility = View.VISIBLE
            etDialogUsername.focusable = View.NOT_FOCUSABLE
            btnDialogOtp.visibility = View.GONE
            etDialogUsername.visibility = View.GONE
            btnDialogOtp.visibility = View.INVISIBLE
            etDialogUsername.visibility = View.INVISIBLE
            etDialogOtp.visibility = View.VISIBLE
            etDialogConfirmPassword.visibility = View.VISIBLE
            etDialogPassword.visibility = View.VISIBLE

            if (android.util.Patterns.EMAIL_ADDRESS.matcher(userData[0].email.toString()).matches()) {
                Amplify.Auth.resetPassword(userData[0].email.toString(),
                    {
                        Log.i("AuthQuickstart", "Password reset OK: $it")
                    },
                    {
                        Log.e("AuthQuickstart", "Password reset failed", it)
                    }
                )

            } else{
                etDialogPassword.error = "Invalid email, eg: xyz@gmail.com"
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
                            // Stop foreground services
                            val serviceIntent = Intent(this, ForegroundService::class.java)
                            applicationContext.stopService(serviceIntent)

                            val intent = Intent(this, ActivityLogin::class.java)
                            Amplify.Auth.signOut(
                                {
                                    Log.i("amplify-app","Signed out")
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

        // Subscription
        layoutSubscription?.setOnClickListener {
            val intent = Intent(this@SettingActivity, SubscriptionActivity::class.java)
            startActivity(intent)
        }

        // Privacy policy
        layoutPrivacyPolicy?.setOnClickListener {
            val intent = Intent(this@SettingActivity, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        // Contact us
        layoutContactUs?.setOnClickListener {
            val intent = Intent(this@SettingActivity, ContactUsActivity::class.java)
            startActivity(intent)
        }

        // About
        layoutAbout?.setOnClickListener {
            val intent = Intent(this@SettingActivity, AboutActivity::class.java)
            startActivity(intent)
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

    private fun dispatchPickFromGalleryIntent() {
        val pickFromGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickFromGalleryIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(pickFromGalleryIntent, REQUEST_IMAGE_PICK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    val imageUri = data?.data
                    val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    ImageViewProfilePicture.setImageBitmap(imageBitmap)
                    saveImageToSharedPreferences(imageBitmap)
                }
            }
        }
    }

    private fun saveImageToSharedPreferences(bitmap: Bitmap) {
        val editor = sharedPreferences.edit()
        val encodedImage = encodeToBase64(bitmap)
        editor.putString(PROFILE_IMAGE_KEY, encodedImage)
        editor.apply()
    }

    private fun encodeToBase64(imageBitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun decodeBase64(encodedImage: String): Bitmap {
        val decodedByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
    }
}
