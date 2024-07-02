package com.example.newsflow.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.newsflow.R
import com.example.newsflow.data.database.users.UserDatabase
import com.example.newsflow.data.repositories.UserRepository
import com.example.newsflow.databinding.FragmentLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LogInFragment : Fragment() {

    companion object {
        fun newInstance() = LogInFragment()
    }

    private lateinit var binding: FragmentLogInBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val firestoreDb: FirebaseFirestore = FirebaseFirestore.getInstance()
        val firestoreAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val userRepository = UserRepository(firestoreDb, firestoreAuth, UserDatabase.getDatabase(requireContext()).userDao())

        val bottomAppBar = requireActivity().findViewById<View>(R.id.bottomAppBar)
        val addBotton = requireActivity().findViewById<View>(R.id.addBotton)
        binding = FragmentLogInBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(
            this,
            AuthViewModel.AuthModelFactory(userRepository)
        )[AuthViewModel::class.java]

        binding.moveToSignUp.setOnClickListener {
            Navigation.createNavigateOnClickListener(R.id.action_logInFragment_to_signUpFragment)
        }

        bottomAppBar.isVisible = false
        addBotton.isVisible = false

        binding.btnLogIn.setOnClickListener {
            viewModel.login(
                email = binding.etEmail.text.toString(),
                password = binding.etPassword.text.toString()
            )
        }

        viewModel.loginSuccessfull.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                bottomAppBar.isVisible = true
                addBotton.isVisible = true
                Navigation.findNavController(requireView()).popBackStack(R.id.feedFragment,false)
            } else {
                Toast.makeText(requireContext(), "Couldn't log you in", Toast.LENGTH_SHORT).show()
            }
        })

        // Observe the loading LiveData
        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.registerProgress.isVisible = true
                binding.btnLogIn.text = ""
            } else {
                binding.registerProgress.isVisible = false
                binding.btnLogIn.text = "Log In"
            }
        })

        // Observe the loading LiveData
        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.registerProgress.isVisible = true
                binding.btnLogIn.text = ""
            } else {
                binding.registerProgress.isVisible = false
                binding.btnLogIn.text = getString(R.string.log_in)
            }
        })

        return binding.root
    }
}