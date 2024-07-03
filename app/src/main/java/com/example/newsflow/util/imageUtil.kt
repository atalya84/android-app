package com.example.newsflow.util

import android.content.ContentResolver
import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.Rotate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import android.widget.ProgressBar
import android.graphics.BitmapFactory
import com.bumptech.glide.request.RequestOptions
import com.example.newsflow.R
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.InputStream

class ImageUtil private constructor() {
    companion object {
        fun loadImage(imageUri: Uri?, context: Context, imageView: ImageView) {
            Glide
                .with(context)
                .load(imageUri)
                .placeholder(R.mipmap.logo)
                .into(imageView)
        }

        fun loadImageInFeed(imageUri: Uri?, context: Context, imageView: ImageView) {
            Glide
                .with(context)
                .load(imageUri)
                .apply(RequestOptions()
                    .fitCenter()
                    .centerCrop())
                .placeholder(R.mipmap.logo)
                .into(imageView)
        }

        fun ShowImgInViewFromGallery(contentResolver: ContentResolver, imageView: ImageView, imageUri: Uri) {
            val inputStream = contentResolver.openInputStream(imageUri)
            if (inputStream != null) {
                val exif = ExifInterface(inputStream)
                val rotation =
                    exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

                val degrees = when (rotation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90F
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180F
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270F
                    else -> 0F
                }

                inputStream.close()

                Picasso.get()
                    .load(imageUri.toString())
                    .rotate(degrees)
                    .fit()
                    .centerCrop()
                    .into(imageView)
            } else {
                Log.d("Picturerequest", "Input stream is null")
            }
        }

        fun showImgInViewFromUrl(imageUri: String, imageView: ImageView, progressBar: ProgressBar) {
            progressBar.visibility = ProgressBar.VISIBLE

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val degrees = withContext(Dispatchers.IO) {
                        val client = OkHttpClient()
                        val request = Request.Builder().url(imageUri).build()
                        client.newCall(request).execute().use { response ->
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
                            response.body?.byteStream()?.use { inputStream ->
                                val exif = ExifInterface(inputStream)
                                val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                                when (rotation) {
                                    ExifInterface.ORIENTATION_ROTATE_90 -> 90F
                                    ExifInterface.ORIENTATION_ROTATE_180 -> 180F
                                    ExifInterface.ORIENTATION_ROTATE_270 -> 270F
                                    else -> 0F
                                }
                            } ?: 0F
                        }
                    }

                    Picasso.get()
                        .load(imageUri)
                        .rotate(degrees)
                        .fit()
                        .centerCrop()
                        .into(imageView)

                    progressBar.visibility = ProgressBar.GONE
                } catch (e: Exception) {
                    progressBar.visibility = ProgressBar.GONE
                }
            }
        }

//        fun showImageInViewFromStorage(imageUri: String, imageView: ImageView, progressBar: ProgressBar, storageRef: StorageReference) {
//            progressBar.visibility = ProgressBar.VISIBLE
//            storageRef.downloadUrl
//        }


        suspend fun UploadImage(imageId: String, imageUri: Uri, storageRef: StorageReference): Uri? {
            val imageRef = storageRef.child(imageId)

            return try {
                imageRef.putFile(imageUri).await()

                val downloadUrl = withContext(Dispatchers.IO) {
                    imageRef.downloadUrl.await()
                }

                downloadUrl
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}