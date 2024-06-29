package com.example.newsflow.viewHolders

import android.content.Context
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.newsflow.data.models.Post
import com.example.newsflow.databinding.FragmentHeadlineBinding
import com.example.newsflow.util.ImageUtil

class PostViewHolder (
    private val binding: FragmentHeadlineBinding
): RecyclerView.ViewHolder(binding.root){
    fun bindPost(post:Post, context: Context) {
        binding.articleTitle.text = post.title
        binding.articleImage.setImageURI(post.imageUrl.toUri())
        binding.articleDescription.text = post.desc
        binding.articleSource.text = post.articleUrl
        binding.articleDateTime.text = post.createdString

        ImageUtil.loadImage(post.imageUrl.toUri(), context, binding.articleImage)
    }
}