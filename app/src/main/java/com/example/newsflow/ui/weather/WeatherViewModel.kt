package com.example.newsflow.ui.weather
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import retrofit2.Response
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewModelScope
import com.androdocs.httprequest.HttpRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import retrofit2.Callback
import retrofit2.Call
import com.example.newsflow.models.WeatherResponse
import com.example.newsflow.networking.ApiConfig

//TODO: Delete this
import android.util.Log


class WeatherViewModel() : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> get() = _weatherData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> get() = _isError

    var errorMessage: String = ""
        private set
//    fun requestLocationPermissions(activity: Activity) {
//        if (ActivityCompat.checkSelfPermission(
//                activity,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                activity,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                activity,
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        } else {
//            fetchCurrentLocation(activity)
//        }
//    }
//    fun fetchWeatherByCity(city: String) {
//        viewModelScope.launch {
//            try {
//                Log.d("WeatherViewModel", "Fetching weather for city: $city")
//                val response =
//                    HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$API_KEY")
//                Log.d("WeatherViewModel", "response: $response")
//                withContext(Dispatchers.Main) {
//                    handleResponse(response)
//                }
//            } catch (e: IOException) {
//                withContext(Dispatchers.Main) {
//                    error.value = "Network error: ${e.message}"
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    error.value = "Error: ${e.message}"
//                }
//            }
//        }
//    }
//
//    private fun fetchCurrentLocation(context: Context) {
//        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//        try {
//            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                if (location != null) {
//                    val lat = location.latitude
//                    val lon = location.longitude
//                    fetchWeatherByLocation(lat, lon)
//                } else {
//                    error.value = "Unable to get current location"
//                }
//            }.addOnFailureListener { e ->
//                error.value = "Failed to get location: ${e.message}"
//            }
//        } catch (e: SecurityException) {
//            error.value = "SecurityException: ${e.message}"
//        }
//    }
//
//    private fun fetchWeatherByLocation(lat: Double, lon: Double) {
//        viewModelScope.launch {
//            try {
//                val response = HttpRequest.excuteGet(
//                    "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=metric&appid=$API_KEY"
//                )
//                withContext(Dispatchers.Main) {
//                    handleResponse(response)
//                }
//            } catch (e: IOException) {
//                error.value = "Network error: ${e.message}"
//            } catch (e: Exception) {
//                error.value = "Error: ${e.message}"
//            }
//        }
//    }

    fun getWeatherData(city: String) {

        _isLoading.value = true
        _isError.value = false

        val client = ApiConfig.getApiService().getCurrentWeather(city = city)

        // Send API request using Retrofit
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

                _isLoading.value = false
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

        _isError.value = true
        _isLoading.value = false
    }


//    private fun handleResponse(response: String) {
//        try {
//            val jsonObj = JSONObject(response)
//            val main = jsonObj.getJSONObject("main")
//            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
//            val wind = jsonObj.getJSONObject("wind")
//            val sys = jsonObj.getJSONObject("sys")
//
//            val cityName = jsonObj.getString("name")
//            val countryName = sys.getString("country")
//            val updatedAt = jsonObj.getLong("dt")
//            val updatedAtText = "Last Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt * 1000))
//            val temperature = main.getString("temp")
//            val cast = weather.getString("description")
//            val humidity = main.getString("humidity")
//            val tempMin = main.getString("temp_min")
//            val tempMax = main.getString("temp_max")
//            val pressure = main.getString("pressure")
//            val windSpeed = wind.getString("speed")
//            val rise = sys.getLong("sunrise")
//            val sunrise = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(rise * 1000))
//            val set = sys.getLong("sunset")
//            val sunset = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(set * 1000))
//
//            weatherData.value = WeatherData(
//                cityName, countryName, updatedAtText, temperature, cast, humidity, tempMin,
//                tempMax, pressure, windSpeed, sunrise, sunset
//            )
//        } catch (e: Exception) {
//            error.value = "Error: ${e.message}"
//        }
//    }

//    companion object {
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
//    }
}
//data class WeatherData(
//    val cityName: String,
//    val countryName: String,
//    val updatedAtText: String,
//    val temperature: String,
//    val cast: String,
//    val humidity: String,
//    val tempMin: String,
//    val tempMax: String,
//    val pressure: String,
//    val windSpeed: String,
//    val sunrise: String,
//    val sunset: String
//)