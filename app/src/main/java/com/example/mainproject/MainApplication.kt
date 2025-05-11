package com.example.mainproject

import android.app.Application
//import dagger.hilt.android.HiltAndroidApp

//@HiltAndroidApp
import com.google.firebase.FirebaseApp

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}