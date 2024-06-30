package com.example.newsflow.data.repositories

import android.net.Uri
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.newsflow.data.database.users.UserDao
import com.example.newsflow.data.models.FirestoreUser
import com.example.newsflow.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.auth.userProfileChangeRequest

class UserRepository (private val firestoreDb: FirebaseFirestore, private val firestoreAuth: FirebaseAuth, private val userDao: UserDao) {

    private val TAG = "SignUpViewModel"
    private val COLLECTION = "users"

    private var usersListenerRegistration: ListenerRegistration? = null

    val ImageToShow: MutableLiveData<Uri?> = MutableLiveData<Uri?>()
    private val _usersLiveData = MutableLiveData<List<User>>()

    private val _loginSuccessfull = MutableLiveData<Boolean>()
    val loginSuccessfull: LiveData<Boolean> = _loginSuccessfull

    val postsLiveData: LiveData<List<User>> get() = _usersLiveData

    @WorkerThread
    fun get (id: String): User = userDao.get(id)

    fun createUser(newUser: FirestoreUser) {
        firestoreAuth.createUserWithEmailAndPassword(newUser.email, newUser.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firestoreAuth.currentUser
                    user?.let {
                        val profileUpdates = userProfileChangeRequest {
                            displayName = newUser.name
                            photoUri = ImageToShow.value
                        }
                        it.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileUpdateTask ->
                                if (profileUpdateTask.isSuccessful) {
                                    Log.d(TAG, "User profile updated.")

                                    val updatedUser = firestoreAuth.currentUser
                                    updatedUser?.let { updatedUser ->
                                        storeUserData(updatedUser.email, updatedUser.displayName)
                                    }
                                } else {
                                    Log.d(TAG, "There was an error updating the user profile")
                                }
                            }
                    }

                    _loginSuccessfull.value = true
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
            }
    }

    // Function to store user data in Firestore
    private fun storeUserData(email: String?, name: String?) {
        val userData = hashMapOf(
            "name" to name
        )
        firestoreDb.collection(COLLECTION).document(email ?: "").set(userData)
    }
}