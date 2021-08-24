package com.example.kotovskdatabase.repositiry

import android.content.Context
import com.example.kotovskdatabase.repositiry.entity.Cat
import com.example.kotovskdatabase.repositiry.room.CatsDatabase
import kotlinx.coroutines.flow.Flow


class Repository private constructor(context: Context) {

    private val dao = CatsDatabase.create(context).catsDao


    fun getAll(): Flow<List<Cat>> = dao.getAll()

    suspend fun save(cat: Cat) = dao.add(cat)

    suspend fun update(cat: Cat) = dao.update(cat)

    suspend fun delete(cat: Cat) = dao.delete(cat)

    companion object {

        private var INSTANCE: Repository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = Repository(context)
            }
        }

        fun get(): Repository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}