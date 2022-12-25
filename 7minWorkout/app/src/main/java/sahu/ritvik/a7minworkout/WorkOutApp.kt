package sahu.ritvik.a7minworkout

import android.app.Application

class WorkOutApp: Application() {
    val db:HistoryDatabase by lazy {
        HistoryDatabase.getInstance(this)
    }
}