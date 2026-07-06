package com.ashwathai.ashwathai.app

import android.app.Application

import com.ashwathai.ashwathai.core.HfTokenProvider
import com.ashwathai.ashwathai.core.Prefs
import com.ashwathai.ashwathai.di.ServiceLocator

class AshwathApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
        HfTokenProvider.init(this)
        Prefs.init(this)
    }
}
