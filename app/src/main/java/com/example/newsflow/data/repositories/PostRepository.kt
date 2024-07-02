package com.example.newsflow.data.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newsflow.data.database.posts.PostDao
import com.example.newsflow.data.models.FirestorePost
import com.example.newsflow.data.models.Post
import com.example.newsflow.data.models.toRoomPost
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PostRepository (private val firestoreDb: FirebaseFirestore, private val postDao: PostDao) {

    private val COLLECTION = "posts"
    private var postsListenerRegistration: ListenerRegistration? = null

    private val _postsLiveData = MutableLiveData<List<Post>>()
    val postsLiveData: LiveData<List<Post>> get() = _postsLiveData

    @WorkerThread
    fun getAll(): List<Post> = postDao.getAll()

    @WorkerThread
    fun get (id: Int): Post = postDao.get(id)

    @WorkerThread
    fun getUserPosts(userId: String): Flow<List<Post>> = postDao.getUserPosts(userId)

    @WorkerThread
    suspend fun insert(post: Post) { postDao.insert(post) }

    @WorkerThread
    suspend fun update(post: Post) { postDao.update(post) }

    @WorkerThread
    suspend fun delete (post: Post) = postDao.delete(post)

    init {
        listenForPostUpdates()
    }

    private fun listenForPostUpdates() {
        postsListenerRegistration = firestoreDb.collection(COLLECTION).addSnapshotListener { snapshot, error ->
            if (error != null) {
                error.printStackTrace()
                return@addSnapshotListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val posts = mutableListOf<Post>()

                snapshot?.documents?.forEach {document ->
                    val firestorePost = document.toObject(FirestorePost::class.java)
                    firestorePost?.let {fsPost ->
                        val post = fsPost.toRoomPost(document.id)
                        insert(post)
                        posts.add(post)
                    }
                }
                _postsLiveData.postValue(posts)
            }
        }
    }
}