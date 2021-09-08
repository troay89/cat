package com.example.kotovskdatabase.repositiry

import android.content.Context
import com.example.kotovskdatabase.repositiry.entity.Cat
import com.example.kotovskdatabase.repositiry.room.DatabaseRoom
import kotlinx.coroutines.flow.Flow


class Repository private constructor(context: Context) : RequestsDao {

    private val dao = DatabaseRoom.create(context).repositoryImpl

    override fun getListCats(typeSort: String): Flow<List<Cat>> =
        when (typeSort) {
            "BY_NAME" -> dao.getListCatsSortedByName()

            "BY_AGE" -> dao.getListCatsSortedByAge()

            "BY_DATE" -> dao.getListCatsSortedByDateCreated()

            else -> dao.getListCatsSortedByDateCreated()
    }

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
            INSTANCE ?: throw IllegalStateException("RoomRepository must be initialized")

    }
}