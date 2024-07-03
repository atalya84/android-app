package com.example.newsflow.ui.article

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.newsflow.R
import com.example.newsflow.data.models.Post
import com.example.newsflow.databinding.ActivityNewsBinding
import com.example.newsflow.databinding.FragmentAddArticleBinding
import com.example.newsflow.ui.NewsActivity
import com.example.newsflow.util.ImageUtil
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddArticleFragment : Fragment() {
    private lateinit var binding: FragmentAddArticleBinding
    private lateinit var articleViewModel: ArticleViewModel
    private lateinit var mainActivityBinding: ActivityNewsBinding
    private var currentPost: Post? = null

    private val newsActivity: NewsActivity
        get() = activity as NewsActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAddArticleBinding.inflate(inflater, container, false)
        mainActivityBinding = ActivityNewsBinding.inflate(inflater, container, false)
        articleViewModel = ViewModelProvider(
            requireActivity(),
            ArticleViewModel.ArticleModelFactory()
        )[ArticleViewModel::class.java]

        val uploadImageClickListener = OnClickListener {
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
                    val imageBtn: ImageButton = binding.choosePhotoImgBtn
                    articleViewModel.ShowImgInView(contentResolver, imageBtn, uri)
                    showImage()
                } catch (e: Exception) {
                    Log.e("Picturerequest", "Error reading exif", e)
                    Toast.makeText(requireContext(), getString(R.string.image_error), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.choosePhotoBtn.setOnClickListener(uploadImageClickListener)
        binding.choosePhotoImgBtn.setOnClickListener(uploadImageClickListener)

       hideImage()

        binding.createPostBtn.setOnClickListener{
            saveAction()
            Navigation.findNavController(requireView()).popBackStack(R.id.feedFragment,false)
        }

        articleViewModel.editLiveData.observe(viewLifecycleOwner) { post ->

            currentPost = post
            if (post != null) {
                binding.createPostBtn.text = "Save Changes"

                val editable = Editable.Factory.getInstance()
                binding.countryInput.text = editable.newEditable(post.country)
                binding.headlineInput.text = editable.newEditable(post.title)
                binding.sourceInput.text = editable.newEditable(post.articleUrl)
                binding.descInput.text = editable.newEditable(post.desc)

               showImage()
                binding.choosePhotoImgBtn.setImageURI(post.imageUrl.toUri())
                ImageUtil.showImgInViewFromUrl(
                    post.imageUrl,
                    binding.choosePhotoImgBtn,
                    binding.imgSpinner
                )
            } else {
                hideImage()
                binding.createPostBtn.text = "Post News"
            }
        }

        return binding.root
    }

    private fun showImage() {
        binding.choosePhotoBtn.visibility = View.GONE
        binding.choosePhotoImgBtn.visibility = View.VISIBLE
    }

    private fun hideImage() {
        binding.choosePhotoBtn.visibility = View.VISIBLE
        binding.choosePhotoImgBtn.visibility = View.GONE
    }

    private fun saveAction() {
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            hideImage()
            val postId: String = if (currentPost != null) currentPost!!.id else articleViewModel.generateRandomUid()
            articleViewModel.insertPost(Post(
                id = postId,
                title = binding.headlineInput.text.toString(),
                desc = binding.descInput.text.toString(),
                articleUrl = binding.sourceInput.text.toString(),
                country = binding.countryInput.text.toString(),
                imageUrl = "",
                createdString = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(Date()),
                userId = userId,
                username = FirebaseAuth.getInstance().currentUser?.displayName ?: ""
            ))
        }
    }
}