package com.sbeve.jada

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        mContext = this.applicationContext

    }

    companion object {
        private lateinit var mContext: Context

        fun getInstance() = mContext
    }

}
