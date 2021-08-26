package com.example.kotovskdatabase

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.example.kotovskdatabase.repositiry.Repository
import com.example.kotovskdatabase.ui.firstscreen.PreferencesManager

class App : Application() {

    var state: SavedStateHandle = SavedStateHandle()
    lateinit var repository: Repository
    lateinit var preferences: PreferencesManager

    override fun onCreate() {
        super.onCreate()
        Repository.initialize(this)
        repository = Repository.get()
        preferences = PreferencesManager(this)
    }
}