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
// File Name: CarouselDashboardActivity.kt
// File Description: this activity contains the app
// walkthrough target views for dashboard Activity.
// Author: Ritvik Sahu
// Date: June 7, 2023
/////////////////////////////////////////////////////

package com.innoweavebiocare.matsya

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_carousel_dashboard.*
import java.text.SimpleDateFormat
import java.util.*

class CarouselDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carousel_dashboard)

        val graphView = findViewById<GraphView>(R.id.pseudo_tvGraph)

        val dataPoints = ArrayList<DataPoint>()
        val random = Random()
        val calendar = Calendar.getInstance()
        for (i in 1..10) {
            calendar.add(Calendar.MINUTE, 30) // Increment time by 30 minutes
            val x = calendar.timeInMillis.toDouble() / 1000 // Convert milliseconds to seconds
            val y = random.nextDouble() * 10
            dataPoints.add(DataPoint(x, y))
        }
        val series = LineGraphSeries<DataPoint>(dataPoints.toTypedArray())
        graphView.addSeries(series)

        val gridLabelRenderer = graphView.gridLabelRenderer
        gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                if (isValueX) {
                    // Format X-axis labels as time
                    val timeInMillis = (value * 1000).toLong()
                    val simpleDateFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
                    return simpleDateFormat.format(Date(timeInMillis))
                }
                return super.formatLabel(value, isValueX)
            }
        }

        //target sequence for this activity
        TapTargetSequence(this)
            .target(
                AppWalkthroughTargetsUtils(pseudo_tvToday ,"Select Date Range","See detailed data like Min, Max, data trends for each parameter in the selected date range.",60)
            ).target(
                AppWalkthroughTargetsUtils(pseudo_tvExport ,"Export Data","Export Data in CSV format",60)
            ).target(
                AppWalkthroughTargetsUtils(pseudo_tvProgressBar ,"Latest Data","Latest data for the particular sensor",50)
            ).target(
                AppWalkthroughTargetsUtils(pseudo_tvGraph ,"Graph Analysis","You can see trend of the data here",140)
            ).target(
                AppWalkthroughTargetsUtils(pseudo_my_toolbar1.getChildAt(2),"Offline Mode","Connect with device locally to provide WiFi credentials and Calibrate sensors",60)
            ).listener(object : TapTargetSequence.Listener {
                override fun onSequenceFinish() {
                    val intent = Intent(this@CarouselDashboardActivity, CarouselCalibrationActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                }

                override fun onSequenceCanceled(lastTarget: TapTarget?) {
                }
            }
            )
            .start()
    }
}