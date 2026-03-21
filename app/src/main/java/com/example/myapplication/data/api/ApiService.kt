package com.example.myapplication.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {

    @GET("b/69bc3b7db7ec241ddc8350e7?meta=false")
    fun getArticles(
        @Header("X-MASTER-KEY") key: String
    ): Call<ApiResponse>

    @GET("b/69bc3b95aa77b81da9fd2a57?meta=false")
    fun getFilters(
        @Header("X-MASTER-KEY") key: String
    ): Call<FiltersResponse>
}