package com.example.meals

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MealsService {
    @GET("/meal") // API path 적어주기
    fun getMeals(@Query("date") date : String) : Call<MealsData> // ?date=20210315 처럼 '?'뒤로 오는 Query 적어주기
}