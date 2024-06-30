package com.example.newsflow.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.newsflow.R
import com.example.newsflow.databinding.FragmentSignUpBinding
import androidx.navigation.fragment.findNavController
import com.example.newsflow.data.database.users.UserDatabase
import com.example.newsflow.data.models.FirestoreUser
import com.example.newsflow.data.models.User
import com.example.newsflow.data.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val firestoreDb: FirebaseFirestore = FirebaseFirestore.getInstance()
        val firestoreAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val userRepository = UserRepository(firestoreDb, firestoreAuth, UserDatabase.getDatabase(requireContext()).userDao())

        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(
            this,
            AuthViewModel.AuthModelFactory(userRepository)
        )[AuthViewModel::class.java]

        binding.moveToLogIn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_logInFragment)
        }

        val bottomAppBar = requireActivity().findViewById<View>(R.id.bottomAppBar)
        bottomAppBar.isVisible = false
        val addBotton = requireActivity().findViewById<View>(R.id.addBotton)
        addBotton.isVisible = false

        binding.btnSignUp.setOnClickListener {
            viewModel.createUser(
                FirestoreUser(
                    email = binding.etEmail.text.toString(),
                    password = binding.etPassword.text.toString(),
                    name = binding.etName.text.toString()
                )
            )
        }


        return binding.root
    }
}