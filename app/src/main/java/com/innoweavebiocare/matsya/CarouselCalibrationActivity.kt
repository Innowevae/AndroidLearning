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
// File Name: CarouselCalibrationActivity.kt
// File Description: this activity contains the app
// walkthrough target views for calibration Activity.
// Author: Ritvik Sahu
// Date: June 7, 2023
/////////////////////////////////////////////////////

package com.innoweavebiocare.matsya

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import kotlinx.android.synthetic.main.activity_carousel_calibration.*

class CarouselCalibrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carousel_calibration)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        //target sequence for this activity
        TapTargetSequence(this)
            .target(
                AppWalkthroughTargetsUtils(pseudo_tv_cal_ph10 ,"Calibrate Sensors","Calibrate sensors once in 6 months for better performance",30)
            )
            .target(
                AppWalkthroughTargetsUtils(pseudo_tv_cal_clear ,"Clear Calibration","Clear Previous Calibration Data",30)
                ).listener(object : TapTargetSequence.Listener {
                override fun onSequenceFinish() {
                    // saving true in shared preference so it will not show app carousel more than once
                    sharedPreferences.edit().putBoolean("app_walkthrough_sequence_completed", true).apply()
                    val dialog = Dialog(this@CarouselCalibrationActivity)
                    dialog.setContentView(R.layout.dialog_walkthrough_end)
                    dialog.show()
                    dialog.setCanceledOnTouchOutside(false)
                    val dialogEndBtn: Button = dialog.findViewById(R.id.dialogEndBtn)
                    dialogEndBtn.setOnClickListener{
                        val intent = Intent(this@CarouselCalibrationActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    }
                override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                }
                override fun onSequenceCanceled(lastTarget: TapTarget?) {
                }
            }
            ).start()
    }
}