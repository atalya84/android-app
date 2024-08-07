package com.example.newsflow.ui.userNews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsflow.R
import com.example.newsflow.adapters.UserNewsAdapter
import com.example.newsflow.data.database.posts.PostDatabase
import com.example.newsflow.data.models.Post
import com.example.newsflow.data.repositories.PostRepository
import com.example.newsflow.databinding.FragmentUserNewsBinding
import com.example.newsflow.ui.article.ArticleViewModel
import com.google.firebase.firestore.FirebaseFirestore

class UserNewsFragment : Fragment() {

    private lateinit var binding: FragmentUserNewsBinding
    private lateinit var userNewsViewModel: UserNewsViewModel
    private lateinit var articleViewModel: ArticleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val userPosts  = MutableLiveData<List<Post>>()
        val userNewsAdapter = UserNewsAdapter(mutableListOf(), object : UserNewsAdapter.PostClickListener {
            override fun onPostClick(post: Post) {
                userPosts.value?.find { p -> p.id == post.id }
                    ?.let {
                        articleViewModel.selectPost(it, ArticleViewModel.Origin.MY_NEWS)
                        Navigation.findNavController(view).navigate(R.id.action_userNewsFragment_to_articleFragment);
                    }
            }
        })

        val firestoreDb: FirebaseFirestore = FirebaseFirestore.getInstance()
        val postRepository = PostRepository(firestoreDb, PostDatabase.getDatabase(requireContext()).postDao())

        binding = FragmentUserNewsBinding.inflate(inflater, container, false)
        articleViewModel = ViewModelProvider(requireActivity(), ArticleViewModel.ArticleModelFactory())[ArticleViewModel::class.java]
        userNewsViewModel = ViewModelProvider(
            this,
            UserNewsViewModel.UserNewsModelFactory(postRepository)
        )[UserNewsViewModel::class.java]

        setRecyclerView(userPosts, postRepository, userNewsAdapter)

        return (binding.root)
    }

    private fun setRecyclerView(posts: MutableLiveData<List<Post>>, postRepository: PostRepository, userNewsAdapter: UserNewsAdapter) {
        userNewsViewModel.userPosts.observe(viewLifecycleOwner) {
            posts.value = ArrayList(it)
            userNewsAdapter.submitList(posts.value!!)
            binding.recyclerUserHeadlines.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = userNewsAdapter
            }
        }
    }
}