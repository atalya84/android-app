package com.example.newsflow.ui.weather
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import retrofit2.Response
import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale
import retrofit2.Callback
import retrofit2.Call
import com.example.newsflow.models.WeatherResponse
import com.example.newsflow.networking.ApiConfig
import com.google.android.gms.location.FusedLocationProviderClient

class WeatherViewModel(private val context: Context) : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> get() = _weatherData
    var errorMessage: String = ""
        private set

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun getCurrentLocationWeather() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            onError("Location permission not granted")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val geocoder = Geocoder(context, Locale.getDefault())
                try {
                    val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1) ?: emptyList()
                    if (addresses.isNotEmpty()) {
                        val cityName: String = addresses[0].locality
                        getWeatherData(cityName)
                    } else {
                        onError("Unable to get city name")
                    }
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
}