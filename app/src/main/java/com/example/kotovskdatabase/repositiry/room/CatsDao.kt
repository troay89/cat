package com.example.kotovskdatabase.repositiry.room

import androidx.room.*
import com.example.kotovskdatabase.repositiry.entity.Cat
import kotlinx.coroutines.flow.Flow

@Dao
interface CatsDao {

    @Query("SELECT * FROM cats_table")
    fun getAll(): List<Cat>

    @Query("SELECT * FROM cats_table ORDER BY name")
    fun getTasksSortedByName(): List<Cat>

    @Query("SELECT * FROM cats_table ORDER BY age")
    fun getTasksSortedByBreed(): List<Cat>

    @Query("SELECT * FROM cats_table ORDER BY created")
    fun getTasksSortedByDateCreated(): List<Cat>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(cat: Cat)

    @Update
    suspend fun update(cat: Cat)

    @Delete
    suspend fun delete(cat: Cat): Int
}