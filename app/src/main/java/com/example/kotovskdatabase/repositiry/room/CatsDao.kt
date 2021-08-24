package com.example.kotovskdatabase.repositiry.room

import androidx.room.*
import com.example.kotovskdatabase.repositiry.entity.Cat
import kotlinx.coroutines.flow.Flow

@Dao
interface CatsDao {

    @Query("SELECT * FROM cats_table")
    fun getAll(): Flow<List<Cat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(cat: Cat)

    @Update
    suspend fun update(cat: Cat)

    @Delete
    suspend fun delete(cat: Cat)
}