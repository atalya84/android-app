package com.example.newsflow.ui.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsflow.data.models.Post
import com.example.newsflow.data.repositories.PostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class PostViewModel(private val repository: PostRepository): ViewModel() {

    private val _postsLiveData = MutableLiveData<List<Post>>()
    val postsLiveData: LiveData<List<Post>> get() = _postsLiveData

    fun getAllPosts() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allPosts = repository.getAll()
                _postsLiveData.postValue(allPosts)
            } catch(e: Exception) {
                Log.w("PostViewModel", "Error getting all posts")
            }
        }
    }

    fun createPost(newPost: Post) = viewModelScope.launch {
        repository.insert(newPost)
    }

    fun updatePost(post: Post) = viewModelScope.launch {
        repository.update(post)
    }

//    fun deletePost(postId: Int) = viewModelScope.launch {
//        repository.delete(postId)
//    }

    class PostModelFactory(private val repository: PostRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PostViewModel::class.java))
                return PostViewModel(repository) as T
            throw IllegalArgumentException("Unknown Class for View Model")
        }
    }
}