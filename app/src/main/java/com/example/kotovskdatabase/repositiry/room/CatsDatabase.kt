package com.example.kotovskdatabase.repositiry.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kotovskdatabase.repositiry.entity.Cat

const val CATS_DATABASE = "CATS_DATABASE"

@Database(entities = [Cat::class], version = 1, exportSchema = false)
abstract class CatsDatabase : RoomDatabase(), AllDatabase {

    companion object {
        fun create(context: Context) = Room
            .databaseBuilder(
                context,
                CatsDatabase::class.java,
                CATS_DATABASE
            ).build()
    }
}