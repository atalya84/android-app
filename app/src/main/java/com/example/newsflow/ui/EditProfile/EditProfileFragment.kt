package com.example.newsflow.ui.EditProfile

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.newsflow.R
import com.example.newsflow.data.database.users.UserDatabase
import com.example.newsflow.data.repositories.UserRepository
import com.example.newsflow.databinding.FragmentEditProfileBinding
import com.example.newsflow.databinding.FragmentSettingsBinding
import com.example.newsflow.ui.NewsActivity
import com.example.newsflow.ui.auth.AuthViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        val firestoreDb: FirebaseFirestore = FirebaseFirestore.getInstance()
        val firestoreAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val userRepository = UserRepository(firestoreDb, firestoreAuth, UserDatabase.getDatabase(requireContext()).userDao())

        viewModel = ViewModelProvider(
            this,
            AuthViewModel.AuthModelFactory(userRepository)
        )[AuthViewModel::class.java]

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

        binding.changePic.setOnClickListener{
            (activity as NewsActivity).requestPermission.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
        (activity as NewsActivity).uriResult.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                try {
                    val contentResolver = requireContext().contentResolver
                    val imageView: ImageView = binding.imageView
                    viewModel.ShowImgInView(contentResolver, imageView, uri)
                } catch (e: Exception) {
                    Log.e("Picturerequest", "Error reading exif", e)
                    Toast.makeText(requireContext(), getString(R.string.image_error), Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    fun validation(): Boolean {
        val name = binding.etName.text
        val email = binding.etEmail.text

        if (viewModel.imageToShow.value == null) {
            Toast.makeText(requireContext(), getString(R.string.enter_img), Toast.LENGTH_SHORT).show()
            return false
        }

        if (name.isNullOrEmpty()){
            Toast.makeText(requireContext(), getString(R.string.enter_name), Toast.LENGTH_SHORT).show()
            return false
        }

        if (email.isNullOrEmpty()){
            Toast.makeText(requireContext(), getString(R.string.enter_email), Toast.LENGTH_SHORT).show()
            return false
        } else {
            val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
            if (!emailRegex.toRegex().matches(email.toString())){
                Toast.makeText(requireContext(), getString(R.string.invalid_email), Toast.LENGTH_SHORT).show()
                return false
            }
        }

        return true
    }
}