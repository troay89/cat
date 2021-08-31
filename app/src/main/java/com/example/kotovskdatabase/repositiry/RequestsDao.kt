package com.example.kotovskdatabase.repositiry

import com.example.kotovskdatabase.repositiry.entity.Cat

interface RequestsDao {

    fun getAll(): List<Cat>

    suspend fun save(cat: Cat)

    suspend fun delete(cat: Cat): Int

    suspend fun update(cat: Cat)


}