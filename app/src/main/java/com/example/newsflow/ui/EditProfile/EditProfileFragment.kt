package com.example.newsflow.ui.EditProfile

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.newsflow.R
import com.example.newsflow.databinding.FragmentEditProfileBinding
import com.example.newsflow.databinding.FragmentSettingsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val size = bottomNavigationView.menu.size()
        for (i in 0 until size) {
            bottomNavigationView.menu.getItem(i).isEnabled = false
        }

        binding.saveChanges.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment_to_settingsFragment)
        }
        binding.cancle.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment_to_settingsFragment)
        }

        return binding.root
    }
}