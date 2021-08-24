package com.example.kotovskdatabase

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.example.kotovskdatabase.repositiry.Repository

class App : Application() {

    lateinit var state: SavedStateHandle
    lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()
        Repository.initialize(this)
        state = SavedStateHandle()
        repository = Repository.get()
    }
}