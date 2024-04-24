package com.dicoding.asclepius.api

import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.api.response.NewsCancer
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("v2/top-headlines?q=cancer&category=health&language=en&apiKey=${BuildConfig.API_KEY}")
    fun getNewsCancer(): Call<NewsCancer>
}