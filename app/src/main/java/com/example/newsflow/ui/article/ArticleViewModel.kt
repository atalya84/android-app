package com.example.newsflow.ui.article

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsflow.data.models.Post
import com.example.newsflow.data.models.toFirestorePost
import com.example.newsflow.util.ImageUtil
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.util.UUID

class ArticleViewModel : ViewModel() {
    private val COLLECTION = "posts"

    private val _articleLiveData = MutableLiveData<Post>()
    private val _origin = MutableLiveData<Origin>()
    private val _postImage = MutableLiveData<Uri>()
    private val _loading = MutableLiveData<Boolean>()
    private val _postSuccessful = MutableLiveData<Boolean>()
    private val firebaseDb: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storageRef: StorageReference = Firebase.storage.reference.child("posts")

    val articleLiveData: LiveData<Post> get() = _articleLiveData
    val origin: LiveData<Origin> get() = _origin
    val loading: LiveData<Boolean> = _loading
    val postSuccessful: LiveData<Boolean> = _postSuccessful

    fun selectPost(post: Post, origin: Origin) {
        _articleLiveData.postValue(post)
        _origin.postValue(origin)
    }

    fun resetForm() { _postSuccessful.postValue(false) }

    fun ShowImgInView(contentResolver: ContentResolver, imageView: ImageView, imageUri: Uri) {
        ImageUtil.ShowImgInViewFromGallery(contentResolver, imageView, imageUri)
        setImageUri(imageUri)
    }

    fun setImageUri(uri: Uri) {
        _postImage.postValue(uri)
    }

    fun insertPost(post: Post) {
        _loading.postValue(true)
        try {
            _postImage.value?.let { uri ->
                CoroutineScope(Dispatchers.IO).launch {
                    if (post.imageUrl.isEmpty()) {
                        post.imageUrl = ImageUtil.UploadImage(post.id, uri, storageRef).toString()
                    }
                    val fsPost = post.toFirestorePost()
                    firebaseDb.collection(COLLECTION).document(post.id).set(fsPost)
                }
            }
        } finally {
            _postSuccessful.postValue(true)
            _loading.postValue(false)
        }
    }

    fun deletePost(id: String) {
        ImageUtil.deleteStorageImage(id, storageRef)
            .addOnSuccessListener {
                firebaseDb.collection(COLLECTION).document(id).delete()
                    .addOnFailureListener {
                        throw(Exception("Could not delete post"))
                    }
            }
            .addOnFailureListener {
                throw(Exception("Could not delete image from storage"))
            }
    }

    fun generateRandomUid(): String {
        val random = SecureRandom()
        val uidBytes = ByteArray(16)
        random.nextBytes(uidBytes)
        val uuid = UUID.nameUUIDFromBytes(uidBytes)
        return uuid.toString().replace("-", "")
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