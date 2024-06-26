package com.example.newsflow.ui.fragments
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.androdocs.httprequest.HttpRequest
import com.example.newsflow.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherFragment : Fragment() {

    var CITY: String = ""
    val API = "6c635fd7996cc1b5d9466f1ea159adf5"
    lateinit var search: ImageView
    lateinit var userCity: EditText
    lateinit var city: TextView
    lateinit var country: TextView
    lateinit var time: TextView
    lateinit var temp: TextView
    lateinit var forecast: TextView
    lateinit var humidity: TextView
    lateinit var min_temp: TextView
    lateinit var max_temp: TextView
    lateinit var sunrises: TextView
    lateinit var sunsets: TextView
    lateinit var pressure: TextView
    lateinit var windSpeed: TextView

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userCity = view.findViewById(R.id.Your_city)
        search = view.findViewById(R.id.search)

        // CALL ALL ANSWERS :
        city = view.findViewById(R.id.city)
        country = view.findViewById(R.id.country)
        time = view.findViewById(R.id.time)
        temp = view.findViewById(R.id.temp)
        forecast = view.findViewById(R.id.forecast)
        humidity = view.findViewById(R.id.humidity)
        min_temp = view.findViewById(R.id.min_temp)
        max_temp = view.findViewById(R.id.max_temp)
        sunrises = view.findViewById(R.id.sunrises)
        sunsets = view.findViewById(R.id.sunsets)
        pressure = view.findViewById(R.id.pressure)
        windSpeed = view.findViewById(R.id.wind_speed)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fetchLocationAndWeather()

        // CLICK ON SEARCH BUTTON :
        search.setOnClickListener {
            CITY = userCity.text.toString()
            // Start coroutine for fetching weather data
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response =
                        HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API")
                    withContext(Dispatchers.Main) {
                        handleResponse(response)
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Network error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun fetchLocationAndWeather() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                fetchWeatherByLocation(lat, lon)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Unable to get current location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun fetchWeatherByLocation(lat: Double, lon: Double) {
        // Start coroutine for fetching weather data
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=metric&appid=$API")
                withContext(Dispatchers.Main) {
                    handleResponse(response)
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchWeatherByCity(city: String) {
        // Start coroutine for fetching weather data
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$API")
                withContext(Dispatchers.Main) {
                    handleResponse(response)
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleResponse(response: String) {
        // Parse JSON response and update UI
        try {
            val jsonObj = JSONObject(response)
            val main = jsonObj.getJSONObject("main")
            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
            val wind = jsonObj.getJSONObject("wind")
            val sys = jsonObj.getJSONObject("sys")

            // CALL VALUE IN API :
            val cityName = jsonObj.getString("name")
            val countryName = sys.getString("country")
            val updatedAt = jsonObj.getLong("dt")
            val updatedAtText = "Last Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt * 1000))
            val temperature = main.getString("temp")
            val cast = weather.getString("description")
            val humidity = main.getString("humidity")
            val tempMin = main.getString("temp_min")
            val tempMax = main.getString("temp_max")
            val pressure = main.getString("pressure")
            val windSpeed = wind.getString("speed")
            val rise = sys.getLong("sunrise")
            val sunrise = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(rise * 1000))
            val set = sys.getLong("sunset")
            val sunset = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(set * 1000))

            // SET ALL VALUES IN TEXTBOX :
            city.text = cityName
            country.text = countryName
            time.text = updatedAtText
            temp.text = "$temperature°C"
            forecast.text = cast
            this@WeatherFragment.humidity.text = humidity
            min_temp.text = tempMin
            max_temp.text = tempMax
            sunrises.text = sunrise
            sunsets.text = sunset
            this@WeatherFragment.pressure.text = pressure
            this@WeatherFragment.windSpeed.text = windSpeed

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
