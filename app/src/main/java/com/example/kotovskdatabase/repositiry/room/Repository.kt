package com.example.kotovskdatabase.repositiry.room

import androidx.room.*
import com.example.kotovskdatabase.repositiry.entity.Cat
import kotlinx.coroutines.flow.Flow

@Dao
interface Repository {

    @Query("SELECT * FROM cats_table")
    fun getAll(): Flow<List<Cat>>

    @Query("SELECT * FROM cats_table ORDER BY name")
    fun getListCatsSortedByName(): Flow<List<Cat>>

    @Query("SELECT * FROM cats_table ORDER BY age")
    fun getListCatsSortedByAge(): Flow<List<Cat>>

    @Query("SELECT * FROM cats_table ORDER BY created")
    fun getListCatsSortedByDateCreated(): Flow<List<Cat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(cat: Cat)

    @Update
    suspend fun update(cat: Cat)

    @Delete
    suspend fun delete(cat: Cat): Int
}