package com.example.myapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_filters")
data class SearchFilter(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val style: String?,
    val sortBy: String?,
    val onlyNewest: Boolean
)