package com.example.kotovskdatabase.repositiry

import com.example.kotovskdatabase.repositiry.room.CatsDao

interface AllDatabase {

    val catsDao: CatsDao
}