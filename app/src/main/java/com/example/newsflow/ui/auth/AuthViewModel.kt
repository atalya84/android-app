package com.example.newsflow.ui.auth

import android.content.ContentResolver
import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.newsflow.data.models.FirestoreUser
import com.example.newsflow.data.repositories.UserRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserRepository): ViewModel() {

    val loading: LiveData<Boolean> = repository.loading
    val signUpSuccessfull: LiveData<Boolean> = repository.signUpSuccessfull
    val signUpFailed: LiveData<Boolean> = repository.signUpFailed
    val loginSuccessfull: LiveData<Boolean> = repository.loginSuccessfull
    val loginFailed: LiveData<Boolean> = repository.loginFailed
    val imageToShow: LiveData<Uri> = repository.imageToShow
    val currUser: LiveData<FirebaseUser> = repository.currUser
    val updateSuccessfull: LiveData<Boolean> = repository.updateSuccessfull

    fun createUser(newUser: FirestoreUser, profileImageRef: StorageReference, errorCallback: (String) -> Unit ) = viewModelScope.launch {
        repository.createUser(newUser, profileImageRef, errorCallback)
    }

    fun logOut() = viewModelScope.launch {
        repository.logOut()
    }

    fun login(email: String, password: String, errorCallback: (String) -> Unit ) = viewModelScope.launch {
        repository.login(email, password, errorCallback)
    }

    fun ShowImgInView(contentResolver: ContentResolver, imageView: ImageView, imageUri: Uri) {
        repository.ShowImgInView(contentResolver, imageView, imageUri)
    }

    fun updateCurrUser(user: FirebaseUser) {
        repository.updateCurrUser(user)
    }

    fun updateProfile(name: String, profileImageRef: StorageReference, imgUrl: Uri, uploadPic: Boolean) {
        repository.updateProfile(name, profileImageRef, imgUrl, uploadPic)
    }

    class AuthModelFactory(private val repository: UserRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java))
                return AuthViewModel(repository) as T
            throw IllegalArgumentException("Unknown Class for View Model")
        }
    }
}