package com.example.myapplication.data.api

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TestApiService(
    private val apiService: ApiService
) : IDataSource {

    companion object {
        private const val API_KEY = "\$2a\$10\$WWqCam0MzPQtv6KN91/guOmw0TT80DW2sTabYDh0ORcN9h8SULTOy"
    }

    override fun getArticles(callback: IDataSource.ArticlesCallback) {
        apiService.getArticles(API_KEY).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    callback.onSuccess(body)
                } else {
                    callback.onFailure("Не вдалося отримати статті з сервера")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                callback.onFailure(t.message ?: "Помилка мережі")
            }
        })
    }

    override fun getFilters(callback: IDataSource.FiltersCallback) {
        apiService.getFilters(API_KEY).enqueue(object : Callback<FiltersResponse> {
            override fun onResponse(
                call: Call<FiltersResponse>,
                response: Response<FiltersResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    callback.onSuccess(body)
                } else {
                    callback.onFailure("Не вдалося отримати фільтри з сервера")
                }
            }

            override fun onFailure(call: Call<FiltersResponse>, t: Throwable) {
                callback.onFailure(t.message ?: "Помилка мережі")
            }
        })
    }
}