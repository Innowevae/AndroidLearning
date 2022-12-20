package sahu.ritvik.dobcalculator

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var tvdate: TextView?=null
    private var tvmin: TextView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvdate =findViewById(R.id.tvdate)
        tvmin =findViewById(R.id.tvmin)
        val button1: Button = findViewById(R.id.mybutton)
        button1.setOnClickListener {
            datepicker()
        }
    }

    fun datepicker(){
        val mycalendar =Calendar.getInstance()
        val year=mycalendar.get(Calendar.YEAR)
        val month=mycalendar.get(Calendar.MONTH)
        val day=mycalendar.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                val selecteddate="$dayOfMonth/ ${month+1} /$year"

                tvdate?.text=selecteddate

                val sdf=SimpleDateFormat("dd/mm/yyyy",Locale.ENGLISH)
                val thedate=sdf.parse(selecteddate)

                val selectedDateInMin = thedate.getTime() / 60000

                val currenttime=sdf.parse(sdf.format(System.currentTimeMillis()))

                val currentDateInMin =currenttime.getTime() /60000

                val difference = currentDateInMin - selectedDateInMin

                tvmin?.text=difference.toString()
            },
            year,
            month,
            day
            ).show()
    }
}