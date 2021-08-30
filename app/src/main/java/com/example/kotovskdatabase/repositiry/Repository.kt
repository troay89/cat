package com.example.kotovskdatabase.repositiry

import android.content.Context
import com.example.kotovskdatabase.repositiry.entity.Cat
import com.example.kotovskdatabase.repositiry.room.DatabaseRoom

import kotlinx.coroutines.flow.Flow


class Repository private constructor(context: Context) {

    private val dao = DatabaseRoom.create(context).catsDao

    fun getTasks(string: String): Flow<List<Cat>> =
        when (string) {
            "BY_NAME" ->dao.getTasksSortedByName()
            "BY_AGE" -> dao.getTasksSortedByBreed()
            "BY_DATE" -> dao.getTasksSortedByDateCreated()
            else -> dao.getAll()
    }

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

        fun get(): Repository =
            INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")

    }
}