package com.example.newsflow.ui.feed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsflow.R
import com.example.newsflow.adapters.FeedAdapter
import com.example.newsflow.data.database.posts.PostDatabase
import com.example.newsflow.data.models.Post
import com.example.newsflow.data.repositories.PostRepository
import com.example.newsflow.databinding.FragmentFeedBinding
import com.example.newsflow.ui.NewsActivity
import com.example.newsflow.ui.article.ArticleViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FeedFragment : Fragment() {
    private lateinit var binding: FragmentFeedBinding
  private val newsActivity: NewsActivity
        get() = activity as NewsActivity
    private lateinit var articleViewModel: ArticleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        newsActivity.displayNavBar()

        val posts = MutableLiveData<List<Post>>()
        val adapter = FeedAdapter(mutableListOf(), object : FeedAdapter.PostClickListener {
            override fun onPostClick(post: Post) {
                posts.value?.find { p -> p.id == post.id }
                    ?.let {
                        articleViewModel.selectPost(it, ArticleViewModel.Origin.FEED)
                        view?.let { view ->
                            Navigation.findNavController(view).navigate(R.id.action_feedFragment_to_articleFragment)
                        }
                    }
            }
        })
        val firestoreDb: FirebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val postRepository = PostRepository(firestoreDb, firebaseAuth, PostDatabase.getDatabase(requireContext()).postDao())

        binding = FragmentFeedBinding.inflate(inflater, container, false)
        articleViewModel = ViewModelProvider(
            requireActivity(),
            ArticleViewModel.ArticleModelFactory()
        )[ArticleViewModel::class.java]
        setRecyclerView(posts, postRepository, adapter)
        return (binding.root)
    }

    private fun setRecyclerView(posts: MutableLiveData<List<Post>>, postRepository: PostRepository, feedAdapter: FeedAdapter) {
        postRepository.postsLiveData.observe(viewLifecycleOwner) {
            posts.value = ArrayList(it)
            feedAdapter.submitList(posts.value!!)
            binding.recyclerHeadlines.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = feedAdapter
            }
        }
    }
}