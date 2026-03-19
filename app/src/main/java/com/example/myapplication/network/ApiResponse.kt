package com.example.myapplication.network

import com.example.myapplication.model.Article
import com.example.myapplication.model.SearchFilter

data class ApiResponse(
    val articles: List<Article>,
    val filters: List<SearchFilter>
)