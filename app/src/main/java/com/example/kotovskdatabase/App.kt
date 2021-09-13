package com.example.kotovskdatabase

import android.app.Application
import com.example.kotovskdatabase.repositiry.room.RepositoryImpl
import com.example.kotovskdatabase.repositiry.cursor.CursorDataBase
import com.example.kotovskdatabase.ui.firstscreen.PreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {


    lateinit var preferencesManager: PreferencesManager

    override fun onCreate() {
        super.onCreate()
        RepositoryImpl.initialize(this)
        CursorDataBase.initialize(this)
        preferencesManager = PreferencesManager(this)
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            modules(
                listOf(

                )
            )
        }
    }
}