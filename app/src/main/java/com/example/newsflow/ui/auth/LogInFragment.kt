package com.example.newsflow.ui.auth

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.newsflow.R
import com.example.newsflow.databinding.FragmentLogInBinding

class LogInFragment : Fragment() {

    companion object {
        fun newInstance() = LogInFragment()
    }

    private lateinit var binding: FragmentLogInBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogInBinding.inflate(inflater, container, false)

        binding.moveToSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
        }

        val bottomAppBar = requireActivity().findViewById<View>(R.id.bottomAppBar)
        bottomAppBar.isVisible = false
        val addBotton = requireActivity().findViewById<View>(R.id.addBotton)
        addBotton.isVisible = false


        return binding.root
    }
}