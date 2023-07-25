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
// File Name: PrivacyPolicyActivity.kt
// File Description: This activity content privacy
// policy term and condition.
// Author: Anshul Malviya
// Date: June 7, 2023
/////////////////////////////////////////////////////

package com.innoweavebiocare.matsya

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView

class PrivacyPolicyActivity : AppCompatActivity() {

    var tvPrivacyPolicy: TextView? = null
    private var ImgBtn_back_arrow: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

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

        ImgBtn_back_arrow = findViewById(R.id.ImgBtn_back_arrow)
        // Back button
        ImgBtn_back_arrow?.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
            finish()
        }

        tvPrivacyPolicy = findViewById(R.id.tvPrivacyPolicy)

        // Set the privacy policy text
        val privacyPolicyText = """
            Information We Collect
            
            1.1. Personal Information:
            When you use the Matsya Application, we may collect personal information from you, such as your name, email address, phone number, and other contact details that you voluntarily provide to us during the registration or account setup process.
            
            1.2. Device Information:
            In order to connect and manage data from your Matsya product, the Application may collect device information, including but not limited to the device ID, firmware version, and network information.
            
            1.3. Sensor Data:
            The Matsya product collects sensor data related to water conditions for fish farming. When you use the Matsya Application, it may collect and store this sensor data, including water temperature, pH levels, oxygen levels, and other relevant information.
            
            Use of Information
            
            2.1. Providing Services:
            We use the collected information to provide you with the functionalities and services offered by the Matsya Application. This includes connecting to your Matsya product, monitoring and displaying sensor data, and enabling you to manage and control your fish farming environment.
            
            2.2. Communication:
            We may use your contact information to communicate with you regarding your use of the Matsya Application, provide updates, respond to your inquiries, and send you important notifications related to your Matsya product.
            
            2.3. Improving the Application:
            We may analyze the collected information to improve the functionality, performance, and user experience of the Matsya Application. This may include troubleshooting, debugging, and enhancing the features and capabilities of the Application.
            
            2.4. Aggregated Data:
            We may aggregate and anonymize the collected information to create statistical data and analytics that help us understand usage patterns, trends, and preferences. This aggregated data will not contain personally identifiable information.
            
            Information Sharing
            
            We respect your privacy and do not sell, trade, or rent your personal information to third parties for marketing purposes. However, we may share your information in the following circumstances:
            
            3.1. Service Providers:
            We may engage trusted third-party service providers who assist us in operating and maintaining the Matsya Application. These service providers have access to your personal information only to the extent necessary to perform their services on our behalf and are obligated to keep it confidential.
            
            3.2. Compliance with Laws:
            We may disclose your personal information if required by law, regulation, legal process, or enforceable governmental request.
            
            3.3. Business Transfers:
            In the event of a merger, acquisition, or sale of all or a portion of our assets, your personal information may be transferred to the acquiring entity or its advisors as part of the transaction. We will notify you of any such transfer and any choices you may have regarding your personal information.
            
            Data Security
            
            We implement reasonable security measures to protect your personal information from unauthorized access, alteration, disclosure, or destruction. However, no method of transmission over the internet or electronic storage is 100% secure, and we cannot guarantee absolute security.
            
            Data Retention
            
            We retain your personal information for as long as necessary to fulfill the purposes outlined in this Privacy Policy, unless a longer retention period is required or permitted by law. We will securely delete or anonymize your personal information when it is no longer needed for these purposes.
            
            Your Rights
            
            You have the right to access, correct, or delete your personal information held by us. You may also have the right to restrict or object to the processing of your personal information in certain circumstances. Please contact us using the information provided below for any requests or inquiries.
            """.trimIndent()

        tvPrivacyPolicy?.text = privacyPolicyText

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