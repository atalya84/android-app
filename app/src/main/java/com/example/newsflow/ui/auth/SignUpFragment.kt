package com.example.newsflow.ui.auth

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.newsflow.R
import com.example.newsflow.data.database.users.UserDatabase
import com.example.newsflow.data.models.FirestoreUser
import com.example.newsflow.data.repositories.UserRepository
import com.example.newsflow.databinding.FragmentSignUpBinding
import com.example.newsflow.ui.NewsActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import android.util.Log
import android.widget.ImageView
import androidx.navigation.Navigation

class SignUpFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: AuthViewModel
    private val newsActivity: NewsActivity
        get() = activity as NewsActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val firestoreDb: FirebaseFirestore = FirebaseFirestore.getInstance()
        val firestoreAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val profileImageRef: StorageReference = Firebase.storage.reference.child("profileImages")

        val userRepository = UserRepository(firestoreDb, firestoreAuth, UserDatabase.getDatabase(requireContext()).userDao())

        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(
            this,
            AuthViewModel.AuthModelFactory(userRepository)
        )[AuthViewModel::class.java]

        binding.moveToLogIn.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.action_signUpFragment_to_logInFragment)
        }

        newsActivity.hideNavBar()

        binding.btnSignUp.setOnClickListener {
            if(validation()) {
                viewModel.createUser(
                    FirestoreUser(
                        email = binding.etEmail.text.toString(),
                        password = binding.etPassword.text.toString(),
                        name = binding.etName.text.toString()
                    ), profileImageRef
                ) { errorMessage ->
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.imageView.setOnClickListener {
            newsActivity.requestPermission.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
        newsActivity.uriResult.observe(viewLifecycleOwner) { uri ->
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

        viewModel.signUpSuccessfull.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                newsActivity.displayNavBar()
                newsActivity.apply { uriResult.value = null }
                Navigation.findNavController(requireView()).popBackStack(R.id.feedFragment,false)
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
                binding.btnSignUp.text = getString(R.string.sign_me_up)
            }
        })

        return binding.root
    }

    fun validation(): Boolean {
        val name = binding.etName.text
        val email = binding.etEmail.text
        val password = binding.etPassword.text

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
        }

        if (password.isNullOrEmpty()){
            Toast.makeText(requireContext(), getString(R.string.enter_password), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}