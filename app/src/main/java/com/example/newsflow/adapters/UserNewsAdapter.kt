package com.example.newsflow.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newsflow.data.models.Post
import com.example.newsflow.databinding.FragmentUserHeadlineBinding
import com.example.newsflow.viewHolders.UserNewsViewHolder

class UserNewsAdapter (
    private val posts: MutableList<Post>,
    private val postClickListener: PostClickListener? = null
): RecyclerView.Adapter<UserNewsViewHolder>() {

    interface PostClickListener {
        fun onPostClick(post: Post)
    }

    var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserNewsViewHolder {
        context = parent.context
        return UserNewsViewHolder(
            FragmentUserHeadlineBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: UserNewsViewHolder, position: Int) {
        context?.let {
            holder.bindPost(posts[position], it, postClickListener)
        }
    }

    fun submitList(postList: List<Post>) {
        this.posts.clear()
        this.posts.addAll(postList)
        notifyDataSetChanged()
    }
}