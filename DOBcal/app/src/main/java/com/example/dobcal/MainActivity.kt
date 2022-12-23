package com.example.dobcal

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
   private var tvSelectDate: TextView? = null
    private var tvAgeInMinute: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btndatepicker : Button = findViewById(R.id.btndatepicker)
        tvSelectDate = findViewById(R.id.tvSelectDate)
        tvAgeInMinute = findViewById(R.id.tvAgeInMinute)
        btndatepicker.setOnClickListener{
            clickdatepicker()
        }

    }
   private fun clickdatepicker(){
       val mycalendar= Calendar.getInstance()
        val year= mycalendar.get(Calendar.YEAR)
        val month= mycalendar.get(Calendar.MONTH)
        val day= mycalendar.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this,
        DatePickerDialog.OnDateSetListener{
            view, year, month, dayOfMonth ->

            val selectDate ="$dayOfMonth/${month+1}/$year"
            tvSelectDate?.text = selectDate

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val theDate = sdf.parse(selectDate)
            val selectedDateInMinutes = theDate.time / 60000
            val currentDate = sdf.parse(sdf.format(System.currentTimeMillis()))
            val currentDateInMinutes = currentDate.time / 60000
            val differenceInMinutes = currentDateInMinutes-selectedDateInMinutes
            tvAgeInMinute?.text = differenceInMinutes.toString()
        }, year, month, day).show()

    }
}