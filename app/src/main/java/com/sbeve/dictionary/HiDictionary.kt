package com.sbeve.dictionary

import android.app.Application
import android.content.Context

class HiDictionary : Application() {

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
    }

    companion object {
        lateinit var mContext: Context
    }
}
