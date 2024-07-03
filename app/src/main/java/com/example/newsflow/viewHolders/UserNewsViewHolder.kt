package com.example.newsflow.viewHolders

import android.content.Context
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.newsflow.adapters.UserNewsAdapter
import com.example.newsflow.data.models.Post
import com.example.newsflow.databinding.FragmentUserHeadlineBinding
import com.example.newsflow.util.ImageUtil

class UserNewsViewHolder (
    private val binding: FragmentUserHeadlineBinding
): RecyclerView.ViewHolder(binding.root){
    fun bindPost(post:Post, context: Context, postClickListener: UserNewsAdapter.PostClickListener?) {

        binding.myNewsTitle.text = post.title
        binding.myNewsImage.setImageURI(post.imageUrl.toUri())
        binding.myNewsCountry.text = post.country
        binding.myNewsDate.text = post.createdString.split(" ")[0]

        binding.root.setOnClickListener{
            postClickListener?.onPostClick(post)
        }

        ImageUtil.loadImage(post.imageUrl.toUri(), context, binding.myNewsImage)
    }
}