package com.example.kotovskdatabase.repositiry.room

import androidx.room.*
import com.example.kotovskdatabase.repositiry.entity.CatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatsDao {

    @Query("SELECT * FROM cats_table")
    fun getAll(): Flow<List<CatEntity>>

    @Query("SELECT * FROM cats_table ORDER BY name")
    fun getListCatsSortedByName(): Flow<List<CatEntity>>

    @Query("SELECT * FROM cats_table ORDER BY age")
    fun getListCatsSortedByAge(): Flow<List<CatEntity>>

    @Query("SELECT * FROM cats_table ORDER BY created")
    fun getListCatsSortedByDateCreated(): Flow<List<CatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(catEntity: CatEntity)

    @Update
    suspend fun update(catEntity: CatEntity)

    @Delete
    suspend fun delete(catEntity: CatEntity): Int
}