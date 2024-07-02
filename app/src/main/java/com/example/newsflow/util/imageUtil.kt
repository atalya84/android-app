package com.example.newsflow.util

import android.content.ContentResolver
import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ImageUtil private constructor() {
    companion object {
        fun loadImage(imageUri: Uri?, context: Context, imageView: ImageView) {
            Glide.with(context).load(imageUri).into(imageView)
        }

        fun ShowImgInView(contentResolver: ContentResolver, imageView: ImageView, imageUri: Uri) {
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
                    .load(imageUri)
                    .rotate(degrees)
                    .fit()
                    .centerCrop()
                    .into(imageView)
            } else {
                Log.d("Picturerequest", "Input stream is null")
            }
        }

        suspend fun UploadImage(firestoreAuth: FirebaseAuth, imageUri: Uri, profileImageRef: StorageReference): Uri? {
            val userId = firestoreAuth.currentUser?.uid ?: ""
            val imageRef = profileImageRef.child(userId)

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