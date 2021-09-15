package com.example.kotovskdatabase.di

import android.app.Application
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import com.example.kotovskdatabase.domain.Repository
import com.example.kotovskdatabase.repositiry.cursor.CATS_DATABASE
import com.example.kotovskdatabase.repositiry.cursor.CursorDataBase
import com.example.kotovskdatabase.repositiry.room.CatsDao
import com.example.kotovskdatabase.repositiry.room.DatabaseRoom
import com.example.kotovskdatabase.repositiry.room.RepositoryImpl
import com.example.kotovskdatabase.ui.firstscreen.CatListViewModel
import com.example.kotovskdatabase.ui.firstscreen.PreferencesManager
import com.example.kotovskdatabase.ui.secondscreen.CatViewModel
import org.koin.android.compat.ScopeCompat.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val roomModule = module {

    fun provideRoomInstance(application: Application): DatabaseRoom = Room
        .databaseBuilder(
            application,
            DatabaseRoom::class.java,
            CATS_DATABASE
        ).build()

    fun provideDao(dataBase: DatabaseRoom): CatsDao = dataBase.catsDao

    single { provideRoomInstance(androidApplication()) }

    single { provideDao(get()) }

}

val mainViewModel = module {

//    fun provideCursorInstance(application: Application) = CursorDataBase.getInstance(application)
    fun preferencesManager(application: Application) = PreferencesManager(application)
    fun repository(dao: CatsDao) = RepositoryImpl(dao)

    factory<Repository> { repository(get()) }
//    factory <Repository> { provideCursorInstance(androidApplication()) }
    factory { preferencesManager(androidApplication()) }


    viewModel {
        CatListViewModel(
            preferencesManager = get(),
            repositoryRoom = get(),
        )
    }

    viewModel {
        CatViewModel(get())
    }

}





