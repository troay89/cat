package com.example.kotovskdatabase.repositiry.room

import android.util.Log
import com.example.kotovskdatabase.domain.Repository
import com.example.kotovskdatabase.domain.model.CatDomain
import com.example.kotovskdatabase.repositiry.mapper.DomainToEntity
import com.example.kotovskdatabase.repositiry.mapper.EntityFlowToDomainFlow
import com.example.kotovskdatabase.repositiry.model.CatEntity
import kotlinx.coroutines.flow.Flow


class RepositoryImpl(private val dao: CatsDao) : Repository {

    override fun getListCats(typeSort: String): Flow<List<CatDomain>> {

        val getList = when (typeSort) {
            "BY_NAME" -> EntityFlowToDomainFlow.map(dao.getListCatsSortedByName())

            "BY_AGE" -> EntityFlowToDomainFlow.map(dao.getListCatsSortedByAge())

            "BY_DATE" -> EntityFlowToDomainFlow.map(dao.getListCatsSortedByDateCreated())

            else -> EntityFlowToDomainFlow.map(dao.getListCatsSortedByDateCreated())

        }
        return getList
    }

    override suspend fun save(catDomain: CatDomain) {
        val catEntity:CatEntity = DomainToEntity.map(catDomain)
        Log.d("save", catEntity.toString())
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
}