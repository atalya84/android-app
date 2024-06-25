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

class NewsActivity : AppCompatActivity() {

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
    }

    public override fun onStop() {
        super.onStop()
        scope.cancel()
    }
}