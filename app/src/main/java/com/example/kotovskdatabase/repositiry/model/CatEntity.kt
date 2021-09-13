package com.example.kotovskdatabase.repositiry.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat


@Entity(tableName = "cats_table")
data class CatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val breed: String,
    val age: Int,
    val created: Long = System.currentTimeMillis(),
) {
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)
}
