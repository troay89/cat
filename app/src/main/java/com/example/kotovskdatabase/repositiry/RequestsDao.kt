package com.example.kotovskdatabase.repositiry

import com.example.kotovskdatabase.repositiry.cursor.CatListener
import com.example.kotovskdatabase.repositiry.entity.Cat
import kotlinx.coroutines.flow.Flow

interface RequestsDao {

    fun getAll(): List<Cat>

    suspend fun save(cat: Cat)

    suspend fun delete(cat: Cat): Int

    suspend fun update(cat: Cat)

    fun addListener(listener: CatListener)

    fun removeListener(listener: CatListener)
}