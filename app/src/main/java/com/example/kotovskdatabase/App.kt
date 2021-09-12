package com.example.kotovskdatabase

import android.app.Application
import com.example.kotovskdatabase.domain.usecase.DeleteCatUseCase
import com.example.kotovskdatabase.domain.usecase.GetListCatsUseCase
import com.example.kotovskdatabase.repositiry.room.RepositoryImpl
import com.example.kotovskdatabase.repositiry.cursor.CursorDataBase
import com.example.kotovskdatabase.ui.firstscreen.PreferencesManager

class App : Application() {


    lateinit var preferencesManager: PreferencesManager

    override fun onCreate() {
        super.onCreate()
        RepositoryImpl.initialize(this)
        CursorDataBase.initialize(this)
        preferencesManager = PreferencesManager(this)
    }
}