package com.example.newsflow.ui.feed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsflow.adapters.FeedAdapter
import com.example.newsflow.data.database.posts.PostDatabase
import com.example.newsflow.data.models.FirestorePost
import com.example.newsflow.data.models.Post
import com.example.newsflow.data.repositories.PostRepository
import com.example.newsflow.databinding.FragmentFeedBinding
import com.google.firebase.firestore.FirebaseFirestore

class FeedFragment : Fragment() {
    private val TAG = "FeedFragment"
    private lateinit var binding: FragmentFeedBinding
    private lateinit var postViewModel: PostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val posts = MutableLiveData<List<Post>>()
        val adapter = FeedAdapter(mutableListOf())
        val firestoreDb: FirebaseFirestore = FirebaseFirestore.getInstance()
        val postRepository = PostRepository(firestoreDb, PostDatabase.getDatabase(requireContext()).postDao())

        binding = FragmentFeedBinding.inflate(inflater, container, false)
        postViewModel = ViewModelProvider(
            this,
            PostViewModel.PostModelFactory(postRepository)
        )[PostViewModel::class.java]

        setRecyclerView(posts, postRepository, adapter)

//        binding.btnAddPost.setOnClickListener{
//            firestoreDb.collection("posts").add(FirestorePost(
//                "Test",
//                "test description",
//                "https://i.kym-cdn.com/entries/icons/original/000/043/344/cover5.jpg",
//                "https://knowyourmeme.com/memes/he-just-like-me-fr",
//                userId = "aaXzimaHB8xGPTf0mF8H"
//            ))
//        }
        return (binding.root)
    }

    private fun setRecyclerView(posts: MutableLiveData<List<Post>>, postRepository: PostRepository, feedAdapter: FeedAdapter) {
        postRepository.postsLiveData.observe(viewLifecycleOwner) {
            posts.value = ArrayList(it)
//            Log.d("FeedFragment", posts.value.toString())
            feedAdapter.submitList(posts.value!!)
            binding.recyclerHeadlines.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = feedAdapter
            }
        }
    }
}