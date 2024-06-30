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

    private val _postsLiveData = MutableLiveData<List<User>>()
    val usersLiveData: LiveData<List<User>> get() = _postsLiveData

    fun createUser(newUser: FirestoreUser) = viewModelScope.launch {
        repository.createUser(newUser)
    }

    class AuthModelFactory(private val repository: UserRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java))
                return AuthViewModel(repository) as T
            throw IllegalArgumentException("Unknown Class for View Model")
        }
    }
}