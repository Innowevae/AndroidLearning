package com.example.a7minutesworkoutapp

import android.app.Application
class WorkOutApp: Application() {

    val db:HistoryDatabase by lazy {
        HistoryDatabase.getInstance(this)
    }
}