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
// File Name: ContactUsActivity.kt
// File Description: This activity content contact
// information and get query from user.
// Author: Anshul Malviya
// Date: June 7, 2023
/////////////////////////////////////////////////////

package com.innoweavebiocare.matsya

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class ContactUsActivity : AppCompatActivity() {

    private var ImgBtn_back_arrow: ImageButton? = null
    private lateinit var etQueryMessage: EditText
    private lateinit var etQuerySubject: EditText
    private lateinit var btnQuerySubmit: Button
    private lateinit var tvMailId: TextView

    val emailId = "innoweavebiocare@gmail.com"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

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

        etQuerySubject = findViewById(R.id.etQuerySubject)
        etQueryMessage = findViewById(R.id.etQueryMessage)
        btnQuerySubmit = findViewById(R.id.btnQuerySubmit)
        ImgBtn_back_arrow = findViewById(R.id.ImgBtn_back_arrow)
        tvMailId = findViewById(R.id.tvMailId)

        // Email id for contact
        tvMailId.text = emailId

        // Back button
        ImgBtn_back_arrow?.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnQuerySubmit.setOnClickListener {
            val message = etQueryMessage.text.toString().trim()
            val subject = etQuerySubject.text.toString().trim()

            if (message.isNotEmpty() && subject.isNotEmpty()) {
                sendEmail(emailId, subject, message)
            } else {
                Toast.makeText(this, "Field can't be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendEmail(email: String, subject: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No email client found", Toast.LENGTH_SHORT).show()
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
}