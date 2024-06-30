package com.example.newsflow.ui.auth

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.newsflow.R
import com.example.newsflow.databinding.FragmentSignUpBinding
import androidx.navigation.fragment.findNavController
import com.example.newsflow.data.database.users.UserDatabase
import com.example.newsflow.data.models.FirestoreUser
import com.example.newsflow.data.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: AuthViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val firestoreDb: FirebaseFirestore = FirebaseFirestore.getInstance()
        val firestoreAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val userRepository = UserRepository(firestoreDb, firestoreAuth, UserDatabase.getDatabase(requireContext()).userDao())

        val bottomAppBar = requireActivity().findViewById<View>(R.id.bottomAppBar)
        val addBotton = requireActivity().findViewById<View>(R.id.addBotton)

        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(
            this,
            AuthViewModel.AuthModelFactory(userRepository)
        )[AuthViewModel::class.java]

        binding.moveToLogIn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_logInFragment)
        }

        bottomAppBar.isVisible = false
        addBotton.isVisible = false

        binding.btnSignUp.setOnClickListener {
            if(validation()) {
                viewModel.createUser(
                    FirestoreUser(
                        email = binding.etEmail.text.toString(),
                        password = binding.etPassword.text.toString(),
                        name = binding.etName.text.toString()
                    )
                )
            }
        }

        viewModel.signUpSuccessfull.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                bottomAppBar.isVisible = true
                addBotton.isVisible = true
                findNavController().navigate(R.id.action_signUpFragment_to_headlinesFragment)
            } else {
                // Handle unsuccessful login
            }
        })

        // Observe the loading LiveData
        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.registerProgress.isVisible = true
                binding.btnSignUp.text = ""
            } else {
                binding.registerProgress.isVisible = false
                binding.btnSignUp.text = "@string/sign_me_up"
            }
        })

        return binding.root
    }

    fun validation(): Boolean {
        var isValid = true
        val name = binding.etName.text
        val email = binding.etEmail.text
        val password = binding.etPassword.text

        if (name.isNullOrEmpty()){
            isValid = false
            Toast.makeText(requireContext(), getString(R.string.enter_name), Toast.LENGTH_SHORT).show()
        }

        if (email.isNullOrEmpty()){
            isValid = false
            Toast.makeText(requireContext(), getString(R.string.enter_email), Toast.LENGTH_SHORT).show()
        } else {
            val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
            if (!emailRegex.toRegex().matches(email.toString())){
                isValid = false
                Toast.makeText(requireContext(), getString(R.string.invalid_email), Toast.LENGTH_SHORT).show()
            }
        }
        if (password.isNullOrEmpty()){
            isValid = false
            Toast.makeText(requireContext(), getString(R.string.enter_password), Toast.LENGTH_SHORT).show()
        } else {
            if (password.toString().length < 8){
                isValid = false
                Toast.makeText(requireContext(), getString(R.string.invalid_password), Toast.LENGTH_SHORT).show()
            }
        }
        return isValid
    }
}