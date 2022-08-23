package com.example.news_app.data

import com.example.news_app.models.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("/v2/top-headlines")
    suspend fun getTopHeadLines(@Query("country") country:String ="in",@Query ("apiKey",) key :String = "054851f4b1c94dde957ce22e15de1fdd"): Response<NewsResponse>

    @GET("/v2/top-headlines")
    suspend fun getSearchNes(@Query("q") searchText:String,@Query ("apiKey",) key :String = "054851f4b1c94dde957ce22e15de1fdd"): Response<NewsResponse>

}