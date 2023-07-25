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
// File Name: SubscriptionActivity.kt
// File Description: This activity content subscription
// details.
// Author: Anshul Malviya
// Date: June 7, 2023
/////////////////////////////////////////////////////

package com.innoweavebiocare.matsya

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class SubscriptionActivity : AppCompatActivity() {
    var expiredTimestamp = 1711929600L
    val rechargedTimestamp = 1680339376L
    val currentTime = Instant.now().epochSecond

    private var ImgBtn_back_arrow: ImageButton? = null
    private var tvDaysLeft: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)

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

        tvDaysLeft = findViewById(R.id.tvDaysLeft)

        ImgBtn_back_arrow = findViewById(R.id.ImgBtn_back_arrow)
        // Back button
        ImgBtn_back_arrow?.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
            finish()
        }

        var daysLeft = expiredTimestamp - currentTime
        val days = convertSecondsToDays(daysLeft)
        Log.i("Subscription","$daysLeft seconds is equal to $days days.")

        tvDaysLeft!!.text = days.toString()
    }
    fun convertSecondsToDays(seconds: Long): Long {
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        return days
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