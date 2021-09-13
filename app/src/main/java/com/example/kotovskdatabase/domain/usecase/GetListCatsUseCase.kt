package com.example.kotovskdatabase.domain.usecase

import com.example.kotovskdatabase.domain.Repository
import com.example.kotovskdatabase.domain.model.CatDomain
import kotlinx.coroutines.flow.Flow

class GetListCatsUseCase(private val repository: Repository) {

    fun execute(typeSort: String): Flow<List<CatDomain>> {
        return repository.getListCats(typeSort)
    }
}