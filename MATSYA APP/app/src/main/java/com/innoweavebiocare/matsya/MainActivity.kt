package com.innoweavebiocare.matsya

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.amplifyframework.core.Amplify
import com.innoweavebiocare.matsya.database.DatabaseHelper
import com.innoweavebiocare.matsya.database.model.rangeNotificationData
import com.innoweavebiocare.matsya.database.model.sensorData.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cognito: Cognito = Cognito(applicationContext)
        val dbHandler: DatabaseHelper = DatabaseHelper(applicationContext)

        // Disable (Dark Mode/Night mode) even it is enabled.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val currentUser = Amplify.Auth.currentUser
        val intent: Intent = if (currentUser == null) {
            // Go to the login screen
            Intent(applicationContext, ActivityLogin::class.java)
        } else {
            // Go to the Home screen
            Intent(applicationContext, HomeActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}