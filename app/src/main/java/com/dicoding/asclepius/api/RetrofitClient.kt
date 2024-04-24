package com.dicoding.asclepius.api

import com.dicoding.asclepius.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitClient {
//    val example = "https://newsapi.org/v2/top-headlines?q=cancer&category=health&language=en&apiKey=${BuildConfig.API_KEY}"
    private const val BASE_URL = "https://newsapi.org/"
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}