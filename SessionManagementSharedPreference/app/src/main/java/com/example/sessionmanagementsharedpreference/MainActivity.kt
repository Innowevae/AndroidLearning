package com.example.sessionmanagementsharedpreference

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView



class MainActivity : AppCompatActivity() {
    private lateinit var tvusername : TextView
    private lateinit var btnLogout : Button
    lateinit var session: SessionManagement
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        session = SessionManagement(this)
        tvusername = findViewById(R.id.idTVWelcome)
        btnLogout = findViewById(R.id.btnLogout)
        session.checkLogin()

        var user: HashMap<String, String> = session.getUserDetail()
        var username = user.get(SessionManagement.KEY_USERNAME)
        tvusername.setText(username)
        btnLogout.setOnClickListener{
            session.LogoutUser()
        }

    }

}