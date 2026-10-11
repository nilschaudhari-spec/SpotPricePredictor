package com.spotpricepredictor.pro

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SpotPricePredictorApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
