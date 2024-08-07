package com.example.newsflow.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.newsflow.R
import com.example.newsflow.data.database.users.UserDatabase
import com.example.newsflow.data.models.FirestoreUser
import com.example.newsflow.data.models.User
import com.example.newsflow.data.repositories.UserRepository
import com.example.newsflow.databinding.FragmentSettingsBinding
import com.example.newsflow.ui.auth.AuthViewModel
import com.example.newsflow.util.ImageUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var currUser: FirebaseUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val firestoreDb: FirebaseFirestore = FirebaseFirestore.getInstance()
        val firestoreAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val userRepository = UserRepository(firestoreDb, firestoreAuth, UserDatabase.getDatabase(requireContext()).userDao())
        currUser = firestoreAuth.currentUser!!

        viewModel = ViewModelProvider(
            this,
            AuthViewModel.AuthModelFactory(userRepository)
        )[AuthViewModel::class.java]

        binding.signout.setOnClickListener {
            viewModel.logOut()
            Navigation.createNavigateOnClickListener(R.id.action_settingsFragment_to_logInFragment);
        }

        binding.editUser.setOnClickListener {
            Navigation.createNavigateOnClickListener(R.id.action_settingsFragment_to_editProfileFragment);
        }

        val imageView: ImageView = binding.imageView
        val progressBar: ProgressBar = binding.progressBar
        ImageUtil.showImgInViewFromUrl(currUser.photoUrl.toString(), imageView, progressBar)
        binding.emailtext.text = currUser.email
        binding.userName.text = currUser.displayName

        return binding.root
    }
}