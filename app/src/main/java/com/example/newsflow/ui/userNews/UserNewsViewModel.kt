package com.example.newsflow.ui.userNews

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.newsflow.data.models.Post
import com.example.newsflow.data.repositories.PostRepository

class UserNewsViewModel(private val repository: PostRepository) : ViewModel() {

    var userPosts: LiveData<List<Post>> = repository.getUserPosts("aaaaa").asLiveData()

    class UserNewsModelFactory(private val repository: PostRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserNewsViewModel::class.java))
                return UserNewsViewModel(repository) as T
            throw IllegalArgumentException("Unknown Class for View Model")
        }
    }
}