package com.example.kotovskdatabase.ui.mapper

import android.util.Log
import com.example.kotovskdatabase.core.BaseMapper
import com.example.kotovskdatabase.domain.model.CatDomain
import com.example.kotovskdatabase.ui.model.UICat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

object UICatToDomain : BaseMapper<UICat, CatDomain> {

    override fun map(type: UICat?): CatDomain {
        return CatDomain(
            id = type!!.id,
            name = type.name,
            breed = type.breed,
            age = type.age,
            created = type.created
        )
    }
}

object CatDomainFlowToUICatFlow : BaseMapper<Flow<List<CatDomain>>, Flow<List<UICat>>> {

    override fun map(type: Flow<List<CatDomain>>?): Flow<List<UICat>> {
        return type?.map { list ->
            Log.d("EntityListToDomainList", list.size.toString())
            list.map {
                UICat(
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