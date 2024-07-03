package com.example.newsflow.ui.auth

import android.content.pm.ActivityInfo
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
import com.example.newsflow.ui.NewsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LogInFragment : Fragment() {

    companion object {
        fun newInstance() = LogInFragment()
    }

    private lateinit var binding: FragmentLogInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val firestoreDb: FirebaseFirestore = FirebaseFirestore.getInstance()
        val firestoreAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val userRepository = UserRepository(firestoreDb, firestoreAuth, UserDatabase.getDatabase(requireContext()).userDao())

        binding = FragmentLogInBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(
            this,
            AuthViewModel.AuthModelFactory(userRepository)
        )[AuthViewModel::class.java]

        binding.moveToSignUp.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.action_logInFragment_to_signUpFragment)
        }


        newsActivity.hideNavBar()

        binding.btnLogIn.setOnClickListener {
            if(validation()) {
                viewModel.login(
                    email = binding.etEmail.text.toString(),
                    password = binding.etPassword.text.toString()
                ) { errorMessage ->
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.loginSuccessfull.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                newsActivity.displayNavBar()
                Navigation.findNavController(requireView()).popBackStack(R.id.feedFragment,false)
            } else {
                Toast.makeText(requireContext(), "Couldn't log you in", Toast.LENGTH_SHORT).show()
            }
        })

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
    private lateinit var viewModel: AuthViewModel
    private val newsActivity: NewsActivity
        get() = activity as NewsActivity

    fun validation(): Boolean {
        val email = binding.etEmail.text
        val password = binding.etPassword.text

        if (email.isNullOrEmpty()){
            Toast.makeText(requireContext(), getString(R.string.enter_email), Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isNullOrEmpty()){
            Toast.makeText(requireContext(), getString(R.string.enter_password), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onResume() {
        super.onResume()
        getActivity()?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    override fun onPause() {
        super.onPause()
        getActivity()?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }
}