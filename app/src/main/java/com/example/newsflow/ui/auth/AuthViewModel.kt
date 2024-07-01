package com.example.newsflow.ui.auth

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.newsflow.data.models.FirestoreUser
import com.example.newsflow.data.models.Post
import com.example.newsflow.data.models.User
import com.example.newsflow.data.repositories.UserRepository
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.io.InputStream

class AuthViewModel(private val repository: UserRepository): ViewModel() {

    val loading: LiveData<Boolean> = repository.loading
    val signUpSuccessfull: LiveData<Boolean> = repository.signUpSuccessfull
    val signUpFailed: LiveData<Boolean> = repository.signUpFailed
    val loginSuccessfull: LiveData<Boolean> = repository.loginSuccessfull
    val loginFailed: LiveData<Boolean> = repository.loginFailed
    val imageBitmap: LiveData<Bitmap> = repository.imageBitmap

    fun createUser(newUser: FirestoreUser) = viewModelScope.launch {
        repository.createUser(newUser)
    }

    fun logOut() = viewModelScope.launch {
        repository.logOut()
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        repository.login(email, password)
    }

//    fun UplaodImage(imageUri: Uri, context: Context, storageDir: String, profileImageRef: StorageReference) = viewModelScope.launch {
//        repository.UplaodImage(imageUri, context, storageDir, profileImageRef)
//    }

    fun ShowImgInView(contentResolver: ContentResolver, imageView: ImageView, imageUri: Uri) {
        repository.ShowImgInView(contentResolver, imageView, imageUri)
    }

    class AuthModelFactory(private val repository: UserRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java))
                return AuthViewModel(repository) as T
            throw IllegalArgumentException("Unknown Class for View Model")
        }
    }
}