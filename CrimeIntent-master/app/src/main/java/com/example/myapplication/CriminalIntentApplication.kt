package com.example.myapplication

import android.app.Application
import com.example.myapplication.database.CrimeRepository

class CriminalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}