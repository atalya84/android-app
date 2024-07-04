package com.example.newsflow.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.newsflow.data.models.Post
import com.example.newsflow.databinding.FragmentHeadlineBinding
import com.example.newsflow.ui.article.ArticleViewModel
import com.example.newsflow.viewHolders.PostViewHolder

class FeedAdapter (
    private val posts: MutableList<Post>,
    private val viewModel: ArticleViewModel,
    private val owner: LifecycleOwner,
    private val postClickListener: PostClickListener? = null
): RecyclerView.Adapter<PostViewHolder>() {

    interface PostClickListener {
        fun onPostClick(post: Post)
    }

    var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        context = parent.context
        return PostViewHolder(
            FragmentHeadlineBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        context?.let {
            holder.bindPost(posts[position], it, viewModel, owner ,postClickListener)
        }
    }

    fun submitList(postList: List<Post>) {
        this.posts.clear()
        this.posts.addAll(postList)
        notifyDataSetChanged()
    }
}