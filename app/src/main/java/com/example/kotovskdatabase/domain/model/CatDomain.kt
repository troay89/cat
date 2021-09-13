package com.example.kotovskdatabase.domain.model


data class CatDomain(
    val id: Int = 0,
    val name: String,
    val breed: String,
    val age: Int,
    val created: Long = System.currentTimeMillis()
)
