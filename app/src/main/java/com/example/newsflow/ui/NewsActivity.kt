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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NewsActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var uriResult: MutableLiveData<Uri?> = MutableLiveData<Uri?>()
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

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(
            bottomNavigationView,navController
        )

        val cancelButton: FloatingActionButton = findViewById(R.id.cancelBotton)
        val addButton: FloatingActionButton = findViewById(R.id.addBotton)

        addButton.setOnClickListener {
            navController.navigate(R.id.signUpFragment)
//            navController.navigate(R.id.addArticleFragment)
//            cancelButton.isEnabled = true
//            addButton.isEnabled = false
//
//            val size = bottomNavigationView.menu.size()
//            for (i in 0 until size) {
//                bottomNavigationView.menu.getItem(i).isChecked = false
//                bottomNavigationView.menu.getItem(i).isEnabled = false
//            }
//
//            val menuItemDashboard = bottomNavigationView.menu.findItem(R.id.fab)
//            menuItemDashboard.isChecked = true
        }

        cancelButton.setOnClickListener {
            navController.navigate(R.id.headlinesFragment)
            cancelButton.isEnabled = false
            addButton.isEnabled = true

            val size = bottomNavigationView.menu.size()
            for (i in 0 until size) {size
                bottomNavigationView.menu.getItem(i).isEnabled = true
            }
        }
    }
    public override fun onStop() {
        super.onStop()
        scope.cancel()
    }
}