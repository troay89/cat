package com.example.kotovskdatabase.repositiry

import com.example.kotovskdatabase.repositiry.entity.Cat
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getListCats(typeSort: String): Flow<List<Cat>>

    suspend fun save(cat: Cat)

    suspend fun delete(cat: Cat): Int

    suspend fun update(cat: Cat)


}