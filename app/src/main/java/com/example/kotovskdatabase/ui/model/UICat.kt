package com.example.kotovskdatabase.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UICat(

    val id: Int = 0,
    val name: String,
    val breed: String,
    val age: Int,
    val created: Long = System.currentTimeMillis(),
): Parcelable
