package com.example.kotovskdatabase

import android.app.Application
import com.example.kotovskdatabase.repositiry.Repository
import com.example.kotovskdatabase.repositiry.cursor.CursorDataBase
import com.example.kotovskdatabase.ui.firstscreen.PreferencesManager

class App : Application() {


    lateinit var preferencesManager: PreferencesManager

    override fun onCreate() {
        super.onCreate()
        Repository.initialize(this)
        CursorDataBase.initialize(this)
        preferencesManager = PreferencesManager(this)
    }
}