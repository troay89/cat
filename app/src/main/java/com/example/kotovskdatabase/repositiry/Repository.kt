package com.example.kotovskdatabase.repositiry

import android.content.Context
import android.util.Log
import com.example.kotovskdatabase.repositiry.entity.Cat
import com.example.kotovskdatabase.repositiry.room.DatabaseRoom

import kotlinx.coroutines.flow.Flow


class Repository private constructor(context: Context): RequestsDao {

    private val dao = DatabaseRoom.create(context).catsDao

    override fun getTasks(string: String): List<Cat> =
        when (string) {
            "BY_NAME" -> {
                Log.d("getTasks", "ROOM")
                dao.getTasksSortedByName()}
            "BY_AGE" -> dao.getTasksSortedByBreed()
            "BY_DATE" -> dao.getTasksSortedByDateCreated()
            else -> dao.getAll()
        }

    fun getAll(): List<Cat> = dao.getAll()

    override suspend fun save(cat: Cat) = dao.add(cat)

    override suspend fun update(cat: Cat) = dao.update(cat)

    override suspend fun delete(cat: Cat) = dao.delete(cat)

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