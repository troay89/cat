package com.example.kotovskdatabase.repositiry.mapper

import android.util.Log
import com.example.kotovskdatabase.core.BaseMapper
import com.example.kotovskdatabase.domain.model.CatDomain
import com.example.kotovskdatabase.repositiry.model.CatEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

object EntityToDomain : BaseMapper<CatEntity, CatDomain> {

    override fun map(type: CatEntity?): CatDomain {
        return CatDomain(
            id = type!!.id,
            name = type.name,
            breed = type.breed,
            age = type.age,
            created = type.created
        )
    }
}

object DomainToEntity : BaseMapper<CatDomain, CatEntity> {

    override fun map(type: CatDomain?): CatEntity {
        return CatEntity(
            id = type?.id ?: 0,
            name = type?.name ?: "",
            breed = type?.breed ?: "",
            age = type?.age ?: -1,
            created = type?.created ?: -1
        )
    }
}

object EntityFlowToDomainFlow : BaseMapper<Flow<List<CatEntity>>, Flow<List<CatDomain>>> {

    override fun map(type: Flow<List<CatEntity>>?): Flow<List<CatDomain>> {
        return type?.map { list ->
            Log.d("EntityListToDomainList", list.size.toString())
            list.map {
                CatDomain(
                    id = it.id,
                    name = it.name,
                    breed = it.breed,
                    age = it.age,
                    created = it.created
                )
            }
        } ?: flow { }
    }
}

object EntityListToDomainList : BaseMapper<List<CatEntity>, List<CatDomain>> {
    override fun map(type: List<CatEntity>?): List<CatDomain> {
        return type?.map {
            CatDomain(
                id = it.id,
                name = it.name,
                breed = it.breed,
                age = it.age,
                created = it.created
            )
        } ?: emptyList()
    }
}
