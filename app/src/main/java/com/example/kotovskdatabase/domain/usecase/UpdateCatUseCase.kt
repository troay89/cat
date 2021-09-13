package com.example.kotovskdatabase.domain.usecase

import com.example.kotovskdatabase.domain.Repository
import com.example.kotovskdatabase.domain.model.CatDomain

class UpdateCatUseCase(private val repository: Repository) {

    suspend fun execute(catDomain: CatDomain) {
        repository.update(catDomain)
    }
}