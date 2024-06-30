package com.example.newsflow.viewHolders

import android.content.Context
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.newsflow.adapters.FeedAdapter
import com.example.newsflow.data.models.Post
import com.example.newsflow.databinding.FragmentHeadlineBinding
import com.example.newsflow.util.ImageUtil

class PostViewHolder (
    private val binding: FragmentHeadlineBinding
): RecyclerView.ViewHolder(binding.root){
    fun bindPost(post:Post, context: Context, postClickListener: FeedAdapter.PostClickListener?) {

        ImageUtil.loadImage(post.imageUrl.toUri(), context, binding.headlineImage)
        binding.headlineTitle.text = post.title
        binding.headlineImage.setImageURI(post.imageUrl.toUri())
        if (post.country.isNotEmpty()) {
            binding.headlineCountryTag.text = post.country
        } else
            binding.headlineCountryTag.isVisible = false

        binding.root.setOnClickListener{
            postClickListener?.onPostClick(post)
        }
    }
}