package com.example.newsflow.ui.fragments

import android.os.Bundle
import com.example.newsflow.R
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.os.AsyncTask
import android.widget.Toast
import com.androdocs.httprequest.HttpRequest
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherFragment : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_weather)
        userCity = findViewById(R.id.Your_city)
        search = findViewById(R.id.search)

        // CALL ALL ANSWERS :
        city = findViewById(R.id.city)
        country = findViewById(R.id.country)
        time = findViewById(R.id.time)
        temp = findViewById(R.id.temp)
        forecast = findViewById(R.id.forecast)
        humidity = findViewById(R.id.humidity)
        min_temp = findViewById(R.id.min_temp)
        max_temp = findViewById(R.id.max_temp)
        sunrises = findViewById(R.id.sunrises)
        sunsets = findViewById(R.id.sunsets)
        pressure = findViewById(R.id.pressure)
        windSpeed = findViewById(R.id.wind_speed)

        // CLICK ON SEARCH BUTTON :
        search.setOnClickListener {
            CITY = userCity.text.toString()
            WeatherTask().execute()
        }
    }

    inner class WeatherTask : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }
        override fun doInBackground(vararg args: String?): String {
            val response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API")
            return response
        }
        override fun onPostExecute(result: String?) {
            try {
                val jsonObj = JSONObject(result)
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
                Toast.makeText(this@WeatherFragment, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}