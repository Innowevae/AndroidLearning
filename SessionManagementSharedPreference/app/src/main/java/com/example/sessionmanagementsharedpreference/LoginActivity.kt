package com.example.sessionmanagementsharedpreference

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin : Button
    lateinit var session : SessionManagement
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        session = SessionManagement(this)
        if(session.isLoggedin()){
            var i : Intent = Intent(applicationContext, MainActivity::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()
        }
        etEmail = findViewById(R.id.etEmail)
        etPass = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.idBtnLogin)

        btnLogin.setOnClickListener{
            var username = etEmail.text.toString().trim()
            var email = etPass.text.toString().trim()

            if(username.isEmpty() && email.isEmpty()){
                Toast.makeText(this, "Login Failed Pls try again", Toast.LENGTH_SHORT).show()

            }
            else{
                session.createLoginSession(username,email)
                var i : Intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(i)
                finish()

            }
        }
    }
}