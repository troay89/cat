package com.example.kotovskdatabase.repositiry.mapper

import com.example.kotovskdatabase.core.BaseMapper
import com.example.kotovskdatabase.domain.model.CatDomain
import com.example.kotovskdatabase.repositiry.entity.CatEntity

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
            id = type?.id ?: -1,
            name = type?.name ?: "",
            breed = type?.breed ?: "",
            age = type?.age ?: -1,
            created = type?.created ?: -1
        )
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