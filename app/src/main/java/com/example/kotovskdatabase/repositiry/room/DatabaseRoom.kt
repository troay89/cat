package com.example.kotovskdatabase.repositiry.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kotovskdatabase.repositiry.AllDatabase
import com.example.kotovskdatabase.repositiry.cursor.CATS_DATABASE
import com.example.kotovskdatabase.repositiry.entity.Cat


@Database(entities = [Cat::class], version = 1, exportSchema = false)
abstract class DatabaseRoom : RoomDatabase(), AllDatabase {

    companion object {
        fun create(context: Context) = Room
            .databaseBuilder(
                context,
                DatabaseRoom::class.java,
                CATS_DATABASE
            ).allowMainThreadQueries().build()
    }
}