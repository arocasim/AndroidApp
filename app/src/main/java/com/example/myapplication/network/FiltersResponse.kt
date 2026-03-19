package com.example.myapplication.network

data class FiltersResponse(
    val filters: FilterData
)

data class FilterData(
    val categories: List<String>,
    val styles: List<String>,
    val sortOptions: List<String>,
    val onlyNewest: List<Boolean>
)