package com.example.kotovskdatabase.repositiry.room

import android.content.Context
import android.util.Log
import com.example.kotovskdatabase.domain.Repository
import com.example.kotovskdatabase.domain.model.CatDomain
import com.example.kotovskdatabase.repositiry.mapper.DomainToEntity
import com.example.kotovskdatabase.repositiry.mapper.EntityListToDomainList
import kotlinx.coroutines.flow.Flow


class RepositoryImpl private constructor(context: Context) : Repository {

    private val dao = DatabaseRoom.create(context).catsDao

    override fun getListCats(typeSort: String): Flow<List<CatDomain>> {

        val getList = when (typeSort) {
            "BY_NAME" -> {
                Log.d("getListCats", "Я в рум")
                dao.getListCatsSortedByName()
            }

            "BY_AGE" -> {
                Log.d("getListCats", "Я в рум")
                dao.getListCatsSortedByAge()
            }

            "BY_DATE" -> {
                Log.d("getListCats", "Я в рум")
                dao.getListCatsSortedByDateCreated()
            }

            else -> {
                Log.d("getListCats", "Я в рум")
                dao.getListCatsSortedByDateCreated()
            }
        }

        EntityListToDomainList.map(getList)
        return getList
    }

    override suspend fun save(catDomain: CatDomain) {
        Log.d("save", "Я в рум")
        val catEntity = DomainToEntity.map(catDomain)
        dao.add(catEntity)
    }

    override suspend fun update(catDomain: CatDomain) {
        val catEntity = DomainToEntity.map(catDomain)
        dao.update(catEntity)
    }

    override suspend fun delete(catDomain: CatDomain):Int {
        Log.d("delete", "Я в рум")
        val catEntity = DomainToEntity.map(catDomain)
        return dao.delete(catEntity)
    }

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