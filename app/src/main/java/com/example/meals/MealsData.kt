package com.example.meals

data class MealsData(
    var status : Int,
    var message : String,
    var data : Data
)
data class Data(
    var breakfast : String = "아침",
    var lunch : String = "점심",
    var dinner : String = "저녁",
    var date : String = "날짜"
)