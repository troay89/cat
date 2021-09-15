package com.example.kotovskdatabase.repositiry.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.kotovskdatabase.repositiry.model.CatEntity


@Database(entities = [CatEntity::class], version = 1, exportSchema = false)
abstract class DatabaseRoom : RoomDatabase(), AllDatabase