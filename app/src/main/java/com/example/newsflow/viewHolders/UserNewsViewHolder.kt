package com.example.newsflow.viewHolders

import android.content.Context
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.newsflow.R
import com.example.newsflow.adapters.UserNewsAdapter
import com.example.newsflow.data.models.Post
import com.example.newsflow.databinding.FragmentUserHeadlineBinding
import com.example.newsflow.util.ImageUtil

class UserNewsViewHolder (
    private val binding: FragmentUserHeadlineBinding
): RecyclerView.ViewHolder(binding.root){
    fun bindPost(
        post:Post,
        context: Context,
        postClickListener: UserNewsAdapter.PostClickListener?,
        menuItemClickListener: UserNewsAdapter.MenuClickListener?
    ) {
        binding.myNewsTitle.text = post.title
        binding.myNewsImage.setImageURI(post.imageUrl.toUri())
        binding.myNewsCountry.text = post.country
        binding.myNewsDate.text = post.createdString.split(" ")[0]

        binding.root.setOnClickListener{
            postClickListener?.onPostClick(post)
        }

        binding.articleMenuBtn.setOnClickListener {
            showPopup(context, binding.articleMenuBtn, post, menuItemClickListener)
        }

        ImageUtil.loadImage(post.imageUrl.toUri(), binding.myNewsImage)
    }

    private fun showPopup(context: Context, view: View, post: Post, menuItemClickListener: UserNewsAdapter.MenuClickListener?) {
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.article_menu)

        popup.setOnMenuItemClickListener {
            menuItemClickListener?.onMenuItemClick(post,it.itemId)
            true
        }
        popup.show()
    }
}