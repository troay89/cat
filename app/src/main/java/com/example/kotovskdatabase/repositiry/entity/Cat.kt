package com.example.kotovskdatabase.repositiry.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "cats_table")

@Parcelize
data class Cat(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val breed: String,
    val age: String,
    val created: Long = System.currentTimeMillis(),
): Parcelable
