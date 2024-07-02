package com.example.newsflow.ui.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsflow.data.models.Post

class ArticleViewModel : ViewModel() {
    private val _articleLiveData = MutableLiveData<Post>()
    private val _origin = MutableLiveData<Origin>();
    val articleLiveData: LiveData<Post> get() = _articleLiveData
    val origin: LiveData<Origin> get() = _origin

    fun selectPost(post: Post, origin: Origin) {
        _articleLiveData.postValue(post)
        _origin.postValue(origin)
    }

    class ArticleModelFactory: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ArticleViewModel::class.java))
                return ArticleViewModel() as T
            throw IllegalArgumentException("Unknown Class for View Model")
        }
    }

    enum class Origin {
        FEED, MY_NEWS
    }
}