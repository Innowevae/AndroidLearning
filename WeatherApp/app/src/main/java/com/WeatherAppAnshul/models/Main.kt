package com.WeatherAppAnshul.models

import java.io.Serializable

data class Main(
    val temp: Float,
    val pressure: Int,
    val humidity: Int,
    val tempMin: Int,
    val tempMax: Int
) : Serializable