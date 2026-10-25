package com.example.app_kotlin

import android.app.Application
import com.example.app_kotlin.data.local.DatosSemilla
import com.example.app_kotlin.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WimApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)

        CoroutineScope(Dispatchers.IO).launch {
            DatosSemilla.poblarSiVacia(container.database, applicationContext)
        }
    }
}