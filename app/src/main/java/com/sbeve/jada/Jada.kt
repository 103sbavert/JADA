package com.sbeve.jada

import android.app.Application
import android.content.Context

class Jada : Application() {

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
    }

    companion object {
        lateinit var mContext: Context
    }
}
