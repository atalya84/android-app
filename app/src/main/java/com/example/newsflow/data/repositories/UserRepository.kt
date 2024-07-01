package com.example.newsflow.data.repositories

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.ImageView
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newsflow.data.database.users.UserDao
import com.example.newsflow.data.models.FirestoreUser
import com.example.newsflow.data.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso
import java.io.IOException
import java.io.InputStream


class UserRepository (private val firestoreDb: FirebaseFirestore, private val firestoreAuth: FirebaseAuth, private val userDao: UserDao) {

    private val TAG = "SignUpViewModel"
    private val COLLECTION = "users"
//    Log.d(TAG, "profileImageRef: $profileImageRef")

    private val _imageBitmap = MutableLiveData<Bitmap>()
    val imageBitmap: LiveData<Bitmap> = _imageBitmap

    private val _signUpSuccessfull = MutableLiveData<Boolean>()
    val signUpSuccessfull: LiveData<Boolean> = _signUpSuccessfull

    private val _signUpFailed = MutableLiveData<Boolean>()
    val signUpFailed: LiveData<Boolean> = _signUpFailed

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _loginSuccessfull = MutableLiveData<Boolean>()
    val loginSuccessfull: LiveData<Boolean> = _loginSuccessfull

    private val _loginFailed = MutableLiveData<Boolean>()
    val loginFailed: LiveData<Boolean> = _loginFailed


    private val storageRef = Firebase.storage.reference;

    @WorkerThread
    fun get (id: String): User = userDao.get(id)

    fun createUser(newUser: FirestoreUser) {
        _loading.value = true
        firestoreAuth.createUserWithEmailAndPassword(newUser.email, newUser.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firestoreAuth.currentUser
                    user?.let {
                        val profileUpdates = userProfileChangeRequest {
                            displayName = newUser.name
//                            photoUri = ImageToShow.value
                        }
                        it.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileUpdateTask ->
                                if (profileUpdateTask.isSuccessful) {
                                    Log.d(TAG, "User profile updated.")

                                    val updatedUser = firestoreAuth.currentUser
                                    updatedUser?.let { updatedUser ->
                                        storeUserData(updatedUser.uid, updatedUser.email, updatedUser.displayName, updatedUser.photoUrl)
                                    }
                                } else {
                                    Log.d(TAG, "There was an error updating the user profile")
                                }
                            }
                    }

                    _signUpSuccessfull.value = true
                } else {
                    try {
                        throw task.exception ?: java.lang.Exception("Invalid authentication")
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        Log.d(TAG, "Authentication failed, Password should be at least 6 characters")
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        Log.d(TAG, "Authentication failed, Invalid email entered")
                    } catch (e: FirebaseAuthUserCollisionException) {
                        Log.d(TAG, "Authentication failed, Email already registered.")
                    } catch (e: Exception) {
                        e.message?.let { Log.d(TAG, it) }
                    }
                }
                _loading.value = false
            }
    }

    fun login(email: String, password: String) {
        _loading.value = true
        firestoreAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firestoreAuth.currentUser
                    if (user != null) {
                        _loginSuccessfull.value = true
                        Log.i("Login", "signInWithEmailAndPassword:success")
                    }
                } else {
                    _loginFailed.value = true
                    Log.i("Login", "Error")
                }
            }
        _loading.value = false
    }

    // Function to store user data in Firestore
    private fun storeUserData(userId: String?, email: String?, name: String?, photo: Uri?) {
        val userData = hashMapOf(
            "userId" to userId,
            "name" to name,
            "imageUrl" to photo
        )
        firestoreDb.collection(COLLECTION).document(email ?: "").set(userData)
    }

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
    }

    fun UplaodImage(imageUri: Uri, context: Context, storageDir: String, profileImageRef: StorageReference) {
        val imgName = getFileName(context, imageUri)
        Log.d(TAG, "imgName: ${imgName}")
        Log.d(TAG, "firestoreAuth.currentUser: ${firestoreAuth.currentUser}")
        Log.d(TAG, "firestoreAuth.currentUser.uid: ${firestoreAuth.currentUser?.uid}")
//        val imageRef = profileImageRef.child(firestoreAuth.currentUser?.uid ?: "")
//        val uploadTask = storageRef.child("$storageDir/$imgName").putFile(imageUri)
//
//        uploadTask.addOnSuccessListener {
//            storageRef.child("upload/$imgName").downloadUrl.addOnSuccessListener { uri ->
//                ImageToShow.value = uri
//                Log.e("Firebase", "download passed")
//            }.addOnFailureListener { exception ->
//                Log.e("Firebase", "Failed in downloading", exception)
//            }
//        }.addOnFailureListener { exception ->
//            Log.e("Firebase", "Image Upload fail", exception)
//        }
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

    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            return cursor.getString(nameIndex)
                        }
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }
}