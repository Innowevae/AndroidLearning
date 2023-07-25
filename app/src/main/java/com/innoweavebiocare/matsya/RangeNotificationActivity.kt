package com.innoweavebiocare.matsya

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.RangeSlider
import com.innoweavebiocare.matsya.database.DatabaseHelper
import com.innoweavebiocare.matsya.database.model.rangeNotificationData
import com.innoweavebiocare.matsya.database.model.sensorData.*


class RangeNotificationActivity : AppCompatActivity() {

    private var tvMinDO: TextView? = null
    private var tvMaxDO: TextView? = null
    private var tvMinPH: TextView? = null
    private var tvMaxPH: TextView? = null
    private var tvMinTEMP: TextView? = null
    private var tvMaxTEMP: TextView? = null
    private var tvMinTDS: TextView? = null
    private var tvMaxTDS: TextView? = null
    private var ImgBtn_back_arrow: ImageButton? = null
    private val dbHandler: DatabaseHelper = DatabaseHelper(this)

    private var rangeSliderDO: RangeSlider? = null
    private var rangeSliderPH: RangeSlider? = null
    private var rangeSliderTDS: RangeSlider? = null
    private var rangeSliderTEMP: RangeSlider? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_range_notification)

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

        tvMaxDO = findViewById(R.id.tvMaxDo)
        tvMinDO = findViewById(R.id.tvMinDO)
        tvMaxPH = findViewById(R.id.tvMaxPH)
        tvMinPH = findViewById(R.id.tvMinPH)
        tvMaxTDS = findViewById(R.id.tvMaxTDS)
        tvMinTDS = findViewById(R.id.tvMinTDS)
        tvMaxTEMP = findViewById(R.id.tvMaxTEMP)
        tvMinTEMP = findViewById(R.id.tvMinTEMP)

        ImgBtn_back_arrow = findViewById(R.id.ImgBtn_back_arrow)

        rangeSliderDO = findViewById(R.id.rangeSliderDO)
        rangeSliderPH = findViewById(R.id.rangeSliderPH)
        rangeSliderTDS = findViewById(R.id.rangeSliderTDS)
        rangeSliderTEMP = findViewById(R.id.rangeSliderTEMP)

        // back button
        ImgBtn_back_arrow?.setOnClickListener {
            val intent = Intent(this@RangeNotificationActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        setStartScreen()

        rangeSliderDO!!.addOnChangeListener { sliderData, value, fromUser ->
            setSliderRange(sliderData, COLUMN_DO)
        }

        rangeSliderPH!!.addOnChangeListener { sliderData, value, fromUser ->
            setSliderRange(sliderData, COLUMN_PH)
        }

        rangeSliderTDS!!.addOnChangeListener { sliderData, value, fromUser ->
            setSliderRange(sliderData, COLUMN_TDS)
        }

        rangeSliderTEMP!!.addOnChangeListener { sliderData, value, fromUser ->
            setSliderRange(sliderData, COLUMN_TEMP)
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

    private fun setSliderRange(sliderData: RangeSlider, params: String) {
        if(params == COLUMN_DO) {
            dbHandler.deleteRowRangeNotify(COLUMN_DO) // Delete db table row if user wants to insert new data
            this.rangeSliderDO!!.setValues(sliderData.values[0], sliderData.values[1])
            tvMaxDO?.text = sliderData.values[1].toString()
            tvMinDO?.text = sliderData.values[0].toString()
            dbHandler.insertRangeNotifyData(rangeNotificationData("1", COLUMN_DO, sliderData.values[0], sliderData.values[1]))  // Insert new data in db
        }

        if(params == COLUMN_PH) {
            dbHandler.deleteRowRangeNotify(COLUMN_PH) // Delete db table row if user wants to insert new data
            rangeSliderPH!!.setValues(sliderData.values[0], sliderData.values[1])
            tvMaxPH?.text = sliderData.values[1].toString()
            tvMinPH?.text = sliderData.values[0].toString()
            dbHandler.insertRangeNotifyData(rangeNotificationData("2", COLUMN_PH, sliderData.values[0], sliderData.values[1])) // Insert new data in db
        }

        if(params == COLUMN_TDS) {
            dbHandler.deleteRowRangeNotify(COLUMN_TDS) // Delete db table row if user wants to insert new data
            rangeSliderTDS!!.setValues(sliderData.values[0], sliderData.values[1])
            tvMaxTDS?.text = sliderData.values[1].toString()
            tvMinTDS?.text = sliderData.values[0].toString()
            dbHandler.insertRangeNotifyData(rangeNotificationData("3", COLUMN_TDS, sliderData.values[0], sliderData.values[1])) // Insert new data in db
        }

        if(params == COLUMN_TEMP) {
            dbHandler.deleteRowRangeNotify(COLUMN_TEMP) // Delete db table row if user wants to insert new data
            rangeSliderTEMP!!.setValues(sliderData.values[0], sliderData.values[1])
            tvMaxTEMP?.text = sliderData.values[1].toString()
            tvMinTEMP?.text = sliderData.values[0].toString()
            dbHandler.insertRangeNotifyData(rangeNotificationData("4", COLUMN_TEMP, sliderData.values[0], sliderData.values[1])) // Insert new data in db
        }
    }

    private fun setStartScreen(){

        // Get data from db
        val rangeList = dbHandler.allRangeNotifyData

        // Set slider range value
        rangeSliderDO!!.setValues(rangeList[0].min, rangeList[0].max)
        rangeSliderPH!!.setValues(rangeList[1].min, rangeList[1].max)
        rangeSliderTDS!!.setValues(rangeList[2].min, rangeList[2].max)
        rangeSliderTEMP!!.setValues(rangeList[3].min, rangeList[3].max)

        // Set textview value min and max
        tvMaxDO?.text = rangeList[0].max.toString()
        tvMaxPH?.text = rangeList[1].max.toString()
        tvMaxTDS?.text = rangeList[2].max.toString()
        tvMaxTEMP?.text = rangeList[3].max.toString()

        tvMinDO?.text = rangeList[0].min.toString()
        tvMinPH?.text = rangeList[1].min.toString()
        tvMinTDS?.text = rangeList[2].min.toString()
        tvMinTEMP?.text = rangeList[3].min.toString()
    }
}