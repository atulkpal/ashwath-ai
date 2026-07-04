package com.ashwathai.ashwathai.app

import android.app.Application

import com.ashwathai.ashwathai.di.ServiceLocator

class AshwathApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
