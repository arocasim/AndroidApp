package com.example.myapplication.data.model

import androidx.room.Entity

@Entity(
    tableName = "filter_category_cross_ref",
    primaryKeys = ["filterId", "categoryName"]
)
data class FilterCategoryCrossRef(
    val filterId: Int,
    val categoryName: String
)