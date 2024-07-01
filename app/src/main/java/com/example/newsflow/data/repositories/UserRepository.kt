package com.example.newsflow.data.repositories

import android.content.ContentResolver
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newsflow.data.database.users.UserDao
import com.example.newsflow.data.models.FirestoreUser
import com.example.newsflow.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository (private val firestoreDb: FirebaseFirestore, private val firestoreAuth: FirebaseAuth, private val userDao: UserDao) {

    private val TAG = "SignUpViewModel"
    private val COLLECTION = "users"

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

    private val _ImageToShow = MutableLiveData<Uri>()
    val imageToShow: LiveData<Uri> = _ImageToShow
    
    @WorkerThread
    fun get (id: String): User = userDao.get(id)

    fun createUser(newUser: FirestoreUser, profileImageRef: StorageReference ) {
        _loading.value = true
        firestoreAuth.createUserWithEmailAndPassword(newUser.email, newUser.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // get user created from firebase auth
                    val user = firestoreAuth.currentUser
                    user?.let {
                        // if the user has uploaded an image
                        _ImageToShow.value?.let {uri ->
                            // asynchronous operation to upload image and creating user
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    // upload image to firebase storage
                                    val uri = UploadImage(uri, profileImageRef)
                                    // if download url is not empty the upload was successful
                                    if (uri != null) {
                                        // update the new user with the name and image url
                                        val profileUpdates = userProfileChangeRequest {
                                            displayName = newUser.name
                                            photoUri = uri
                                        }
                                        // when the update is done
                                        it.updateProfile(profileUpdates)
                                            .addOnCompleteListener { profileUpdateTask ->
                                                if (profileUpdateTask.isSuccessful) {
                                                    Log.d(TAG, "User profile updated.")

                                                    // save all the data in firestore db
                                                    val updatedUser = firestoreAuth.currentUser
                                                    updatedUser?.let { user ->
                                                        storeUserData(user.uid, user.email, user.displayName, user.photoUrl)
                                                    }
                                                } else {
                                                    Log.d(TAG, "There was an error updating the user profile")
                                                }
                                            }
                                    }
                                } catch (e: Exception) {
                                    // Handle exceptions
                                }
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

    suspend fun UploadImage(imageUri: Uri, profileImageRef: StorageReference): Uri? {
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

            _ImageToShow.value = imageUri
        } else {
            Log.d("Picturerequest", "Input stream is null")
        }
    }
}