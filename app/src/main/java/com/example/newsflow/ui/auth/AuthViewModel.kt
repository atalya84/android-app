package com.example.newsflow.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.newsflow.data.models.FirestoreUser
import com.example.newsflow.data.models.Post
import com.example.newsflow.data.models.User
import com.example.newsflow.data.repositories.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserRepository): ViewModel() {

    val loading: LiveData<Boolean> = repository.loading
    val signUpSuccessfull: LiveData<Boolean> = repository.signUpSuccessfull
    val signUpFailed: LiveData<Boolean> = repository.signUpFailed
    val loginSuccessfull: LiveData<Boolean> = repository.loginSuccessfull
    val loginFailed: LiveData<Boolean> = repository.loginFailed

    fun createUser(newUser: FirestoreUser) = viewModelScope.launch {
        repository.createUser(newUser)
    }

    fun logOut() = viewModelScope.launch {
        repository.logOut()
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        repository.login(email, password)
    }

    class AuthModelFactory(private val repository: UserRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java))
                return AuthViewModel(repository) as T
            throw IllegalArgumentException("Unknown Class for View Model")
        }
    }
}