package com.example.newsflow.ui.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.newsflow.R
import com.example.newsflow.databinding.FragmentWeatherBinding
import com.example.newsflow.models.WeatherResponse
import com.example.newsflow.ui.weather.WeatherFactory
import com.example.newsflow.ui.weather.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherFragment : Fragment() {

    private val weatherViewModel: WeatherViewModel by viewModels {
        WeatherFactory()
    }

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentWeatherBinding

    private lateinit var userCity: EditText
    private lateinit var search: ImageView
    private lateinit var city: TextView
    private lateinit var country: TextView
    private lateinit var time: TextView
    private lateinit var temp: TextView
    private lateinit var forecast: TextView
    private lateinit var humidity: TextView
    private lateinit var minTemp: TextView
    private lateinit var maxTemp: TextView
    private lateinit var sunrise: TextView
    private lateinit var sunset: TextView
    private lateinit var windSpeed: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        subscribe()

        userCity = binding.YourCity
        search = binding.search

        // Initialize UI elements
        city = binding.city
        country = binding.country
        time = binding.time
        temp = binding.temp
        forecast = binding.forecast
        humidity = binding.humidity
        minTemp = binding.minTemp
        maxTemp = binding.maxTemp
        sunrise = binding.sunrises
        sunset = binding.sunsets
        windSpeed = binding.windSpeed

        // Initialize the requestPermissionLauncher
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            weatherViewModel.getCurrentLocationWeather(requireContext(), requireActivity())
        }

        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        search.setOnClickListener {
            val cityName = userCity.text.toString()
            if (cityName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a city name", Toast.LENGTH_SHORT).show()
            } else {
                weatherViewModel.getWeatherData(cityName)
                userCity.setText("")
            }
        }
        
        return binding.root
    }

    private fun subscribe() {
        weatherViewModel.weatherData.observe(viewLifecycleOwner) { weatherData ->
            setResultText(weatherData)
        }
    }

    private fun setResultText(weatherData: WeatherResponse) {
        weatherData.main?.let { main ->
            val responseCityName = weatherData.name ?: "Unknown"
            val responseCountryName = weatherData.sys?.country ?: "Unknown"

            val updatedAt = weatherData.dt?.let { it.toLong() } ?: 0L
            val responseUpdatedAtText =
                "Last Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt * 1000)
                )

            val responseTemperature = main.temp.toString() ?: "N/A"
            val responseCast = weatherData.weather?.firstOrNull()?.description ?: "N/A"
            val responseHumidity = main.humidity.toString() ?: "N/A"
            val responseTempMin = main.tempMin.toString() ?: "N/A"
            val responseTempMax = main.tempMax.toString() ?: "N/A"
            val responseWindSpeed = weatherData.wind?.speed.toString() ?: "N/A"

            val rise = weatherData.sys?.sunrise?.let { it.toLong() } ?: 0L
            val responseSunrise = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(rise * 1000))

            val set = weatherData.sys?.sunset?.let { it.toLong() } ?: 0L
            val responseSunset = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(set * 1000))

            // SET ALL VALUES IN TEXTBOX :
            city.text = responseCityName
            country.text = responseCountryName
            time.text = responseUpdatedAtText
            temp.text = "$responseTemperature°C"
            forecast.text = responseCast
            this@WeatherFragment.humidity.text = responseHumidity
            minTemp.text = responseTempMin
            maxTemp.text = responseTempMax
            sunrise.text = responseSunrise
            sunset.text = responseSunset
            this@WeatherFragment.windSpeed.text = responseWindSpeed
        }
    }
}