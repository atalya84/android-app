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
import com.google.android.gms.location.FusedLocationProviderClient


class WeatherViewModel(private val context: Context) : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> get() = _weatherData

    var errorMessage: String = ""
        private set

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun getWeatherData(city: String) {

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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}