package com.example.newsflow.networking

import com.example.newsflow.models.WeatherResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // Get current weather data
    @GET("weather/")
    fun getCurrentWeather(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = ApiConfig.API_KEY
    ): Call<WeatherResponse>

    // Get weather data by geographic coordinates
    @GET("weather/")
    fun getWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = ApiConfig.API_KEY
    ): Call<WeatherResponse>
}