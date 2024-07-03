package com.example.newsflow.ui.weather
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import retrofit2.Response
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import java.io.IOException
import retrofit2.Callback
import retrofit2.Call
import com.example.newsflow.models.WeatherResponse
import com.example.newsflow.networking.ApiConfig

class WeatherViewModel() : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> get() = _weatherData

    private var _cityName= MutableLiveData<String>()
    val cityName: LiveData<String> get() = _cityName
    var errorMessage: String = ""
        private set

    fun getCurrentLocationWeather(context: Context, activity: Activity) {
        Log.d("getCurrentWeather", "got in")
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    try {
                        val lat = it.latitude
                        val lon = it.longitude
                        val client = ApiConfig.getApiService().getWeatherByCoordinates(latitude = lat, longitude = lon)
                        handleApiResponse(client)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        onError("Geocoder Service Error")
                    }
                } ?: onError("Location Error")
            }.addOnFailureListener { e ->
                e.printStackTrace()
                onError("Failed to get Location")
            }
        }
    }

    fun getWeatherData() {
        val client = ApiConfig.getApiService().getCurrentWeather(city = _cityName.value!!)
        handleApiResponse(client)
    }

    fun setCity(city: String) {
        _cityName.value = city
    }

    private fun handleApiResponse( client: Call<WeatherResponse>) {
        client.enqueue(object : Callback<WeatherResponse> {

            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                val responseBody = response.body()
                if (!response.isSuccessful || responseBody == null) {
                    onError("Data Processing Error")
                    return
                }
                _weatherData.postValue(responseBody!!)
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                onError(t.message)
                t.printStackTrace()
            }

        })
    }

    private fun onError(inputMessage: String?) {

        val message = if (inputMessage.isNullOrBlank() or inputMessage.isNullOrEmpty()) "Unknown Error"
        else inputMessage

        errorMessage = StringBuilder("ERROR: ")
            .append("$message some data may not displayed properly").toString()
    }
}