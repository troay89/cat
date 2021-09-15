package com.example.kotovskdatabase

import android.app.Application
import com.example.kotovskdatabase.di.mainViewModel
import com.example.kotovskdatabase.di.roomModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    roomModule,
                    mainViewModel
                )
            )
        }
    }
}