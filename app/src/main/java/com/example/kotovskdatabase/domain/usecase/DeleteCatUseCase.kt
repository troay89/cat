package com.example.kotovskdatabase.domain.usecase

import com.example.kotovskdatabase.domain.Repository
import com.example.kotovskdatabase.domain.model.CatDomain
import com.example.kotovskdatabase.repositiry.entity.CatEntity

class DeleteCatUseCase(private val repository: Repository) {

    suspend fun execute(catDomain: CatDomain): Int {
        return repository.delete(catDomain)
    }
}