package com.example.newsflow.viewHolders

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.newsflow.R
import com.example.newsflow.adapters.FeedAdapter
import com.example.newsflow.data.models.Post
import com.example.newsflow.databinding.FragmentHeadlineBinding
import com.example.newsflow.ui.article.ArticleViewModel
import com.example.newsflow.util.ImageUtil

class PostViewHolder (
    private val binding: FragmentHeadlineBinding
): RecyclerView.ViewHolder(binding.root){
    fun bindPost(post:Post, context: Context, viewModel: ArticleViewModel, owner: LifecycleOwner, postClickListener: FeedAdapter.PostClickListener?) {

        binding.loadingSpinner.visibility = View.VISIBLE
        binding.headlineTitle.text = post.title
        binding.headlineImage.setImageURI(post.imageUrl.toUri())
        binding.headlineCountryTag.text = post.country

        binding.root.setOnClickListener{
            postClickListener?.onPostClick(post)
        }

        viewModel.loading.observe(owner) { isLoading ->
            binding.loadingSpinner.isVisible = isLoading
        }
        ImageUtil.loadImageInFeed(post.imageUrl.toUri(), binding.headlineImage)  {
            binding.loadingSpinner.visibility = View.GONE
        }

    }
}