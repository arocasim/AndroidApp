package com.example.myapplication.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {

    private val api: ApiService

    companion object {
        private const val API_KEY = "\$2a\$10\$WWqCam0MzPQtv6KN91/guOmw0TT80DW2sTabYDh0ORcN9h8SULTOy"
    }

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.jsonbin.io/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiService::class.java)
    }

    fun getArticles(callback: (ApiResponse?) -> Unit) {
        api.getArticles(API_KEY).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun getFilters(callback: (FiltersResponse?) -> Unit) {
        api.getFilters(API_KEY).enqueue(object : Callback<FiltersResponse> {
            override fun onResponse(call: Call<FiltersResponse>, response: Response<FiltersResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<FiltersResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}