package com.example.kotovskdatabase.domain

import com.example.kotovskdatabase.domain.model.CatDomain
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getListCats(typeSort: String): Flow<List<CatDomain>>

    suspend fun save(catDomain: CatDomain)

    suspend fun delete(catDomain: CatDomain): Int

    suspend fun update(catDomain: CatDomain)
}