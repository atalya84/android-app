package com.example.newsflow.ui.feed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsflow.adapters.FeedAdapter
import com.example.newsflow.data.database.posts.PostDatabase
import com.example.newsflow.data.models.Post
import com.example.newsflow.data.repositories.PostRepository
import com.example.newsflow.databinding.FragmentFeedBinding
import com.google.firebase.firestore.FirebaseFirestore

class FeedFragment : Fragment() {
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