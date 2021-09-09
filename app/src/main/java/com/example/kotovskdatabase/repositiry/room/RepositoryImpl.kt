package com.example.kotovskdatabase.repositiry.room

import android.content.Context
import com.example.kotovskdatabase.repositiry.Repository
import com.example.kotovskdatabase.repositiry.entity.Cat
import kotlinx.coroutines.flow.Flow


class RepositoryImpl private constructor(context: Context) : Repository {

    private val dao = DatabaseRoom.create(context).catsDao

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

        private var INSTANCE: RepositoryImpl? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = RepositoryImpl(context)
            }
        }

        fun get(): RepositoryImpl =
            INSTANCE ?: throw IllegalStateException("RoomRepository must be initialized")

    }
}