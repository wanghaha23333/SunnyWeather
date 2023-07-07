package com.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.sunnyweather.android.logic.model.Location

class SunnyWeatherApplication : Application() {

    companion object {
        const val TOKEN = "CNdCTLQlwQRFPbQc"

        var rowId: Long = -1

        var locationDes = Location("", "")

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}