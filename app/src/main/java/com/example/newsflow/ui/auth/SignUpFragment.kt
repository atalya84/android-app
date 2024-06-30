package com.example.newsflow.ui.auth

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.newsflow.R
import com.example.newsflow.databinding.FragmentSignUpBinding
import androidx.navigation.fragment.findNavController

class SignUpFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private lateinit var binding: FragmentSignUpBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.moveToLogIn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_logInFragment)
        }

        val bottomAppBar = requireActivity().findViewById<View>(R.id.bottomAppBar)
        bottomAppBar.isVisible = false
        val addBotton = requireActivity().findViewById<View>(R.id.addBotton)
        addBotton.isVisible = false


        return binding.root
    }
}