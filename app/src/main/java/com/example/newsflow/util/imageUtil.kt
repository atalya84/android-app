package com.example.newsflow.util

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide

class ImageUtil private constructor() {
    companion object {
        fun loadImage(imageUri: Uri?, context: Context, imageView: ImageView) {
            Glide.with(context).load(imageUri).into(imageView)
        }
    }
}