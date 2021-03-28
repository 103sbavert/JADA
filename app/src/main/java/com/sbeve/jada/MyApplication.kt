package com.sbeve.jada

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
    }
    
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}
