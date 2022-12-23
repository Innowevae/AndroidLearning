package com.example.myfirstapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnClickMe = findViewById<Button>(R.id.button)
        val tvMyTextView = findViewById<TextView>(R.id.textView)
        var timesClicked = 0
//        btnClickMe.setOnClickListener{
//            btnClickMe.text = "HAHA you clicked me"
//        }
        btnClickMe.setOnClickListener{
           timesClicked=timesClicked+1
            tvMyTextView.text =timesClicked.toString()
            Toast.makeText(this,"hey Anshul!", Toast.LENGTH_LONG).show()
        }
    }
}