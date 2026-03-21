package com.example.myapplication.data.api

interface IDataSource {

    fun getArticles(callback: ArticlesCallback)

    fun getFilters(callback: FiltersCallback)

    interface ArticlesCallback {
        fun onSuccess(response: ApiResponse)
        fun onFailure(message: String)
    }

    interface FiltersCallback {
        fun onSuccess(response: FiltersResponse)
        fun onFailure(message: String)
    }
}