package com.example.newsflow.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import com.example.newsflow.R
import androidx.fragment.app.Fragment
import com.example.newsflow.ui.fragments.AddArticleFragment
import com.example.newsflow.ui.fragments.HeadlinesFragment
import com.example.newsflow.ui.fragments.SettingsFragment
import com.example.newsflow.ui.fragments.UserNewsFragment
import com.example.newsflow.ui.fragments.WeatherFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NewsActivity : AppCompatActivity() {

    lateinit var bottomNav : BottomNavigationView

    val scope = CoroutineScope(Dispatchers.IO + Job())
    var uriResult: MutableLiveData<Uri?> = MutableLiveData<Uri?>()
    val requestPermission =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                uriResult.value = uri
                Log.d("Picturerequest", "$uri")
            } else {
                Log.d("Picturerequest", "No media selected")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        bottomNav = findViewById(R.id.bottomNavigationView) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.weatherFragment -> {
                    loadFragment(WeatherFragment())
                    true
                }
                R.id.settingsFragment -> {
                    loadFragment(SettingsFragment())
                    true
                }
                R.id.headLinesFragment -> {
                    loadFragment(HeadlinesFragment())
                    true
                }
                R.id.userNewsFragment -> {
                    loadFragment(UserNewsFragment())
                    true
                }
                else -> false
            }
        }
        val cancelButton: FloatingActionButton = findViewById(R.id.cancelBotton)
        val addButton: FloatingActionButton = findViewById(R.id.addBotton)

        addButton.setOnClickListener {
            loadFragment(AddArticleFragment())
            cancelButton.isEnabled = true
            addButton.isEnabled = false

            val size = bottomNav.menu.size()
            for (i in 0 until size) {
                bottomNav.menu.getItem(i).isChecked = false
                bottomNav.menu.getItem(i).isEnabled = false
            }

            val menuItemDashboard = bottomNav.menu.findItem(R.id.fab)
            menuItemDashboard.isChecked = true
        }

        cancelButton.setOnClickListener {
            loadFragment(HeadlinesFragment())
            cancelButton.isEnabled = false
            addButton.isEnabled = true

            val size = bottomNav.menu.size()
            for (i in 0 until size) {
                bottomNav.menu.getItem(i).isChecked = false
                bottomNav.menu.getItem(i).isEnabled = true
            }

            val menuItemDashboard = bottomNav.menu.findItem(R.id.headLinesFragment)
            menuItemDashboard.isChecked = true
        }
    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout,fragment)
        transaction.commit()
    }
    public override fun onStop() {
        super.onStop()
        scope.cancel()
    }
}