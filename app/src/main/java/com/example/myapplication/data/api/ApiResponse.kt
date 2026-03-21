package com.example.myapplication.data.api

import com.example.myapplication.data.model.Article
import com.example.myapplication.data.model.SearchFilter

data class ApiResponse(
    val articles: List<Article>,
    val filters: List<SearchFilter>
)