package com.tfgrecorder

import android.app.Application
import com.tfgrecorder.di.TFGAppServiceLocator

class TFGApp: Application() {
    lateinit var serviceLocator: TFGAppServiceLocator

    override fun onCreate() {
        super.onCreate()
        serviceLocator = TFGAppServiceLocator(applicationContext)
    }
}