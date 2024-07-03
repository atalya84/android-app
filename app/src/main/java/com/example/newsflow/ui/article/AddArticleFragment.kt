package com.example.newsflow.ui.article

import android.content.pm.ActivityInfo
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
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
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
    private val args: AddArticleFragmentArgs by navArgs()

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
            if(validation()) {
                saveAction()
            }
        }

        articleViewModel.postSuccessful.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                articleViewModel.resetForm()
                newsActivity.enableNavBar()
                newsActivity.apply { uriResult.value = null }
                Navigation.findNavController(requireView()).popBackStack(R.id.feedFragment,false)
            }
        })

        articleViewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.uploadProgress.isVisible = true
                binding.createPostBtn.text = ""
            } else {
                binding.uploadProgress.isVisible = false
                binding.createPostBtn.text = getString(R.string.create_post_btn_text)
            }
        })
            if (args.id.isNotEmpty()) {
                binding.createPostBtn.text = "Save Changes"

                val editable = Editable.Factory.getInstance()
                binding.countryInput.text = editable.newEditable(args.country)
                binding.headlineInput.text = editable.newEditable(args.title)
                binding.sourceInput.text = editable.newEditable(args.source)
                binding.descInput.text = editable.newEditable(args.desc)

               showImage()
                binding.choosePhotoImgBtn.setImageURI(args.imageUrl.toUri())
                articleViewModel.setImageUri(args.imageUrl.toUri())
                ImageUtil.showImgInViewFromUrl(
                    args.imageUrl,
                    binding.choosePhotoImgBtn,
                    binding.imgSpinner
                )

            } else {
                hideImage()
                binding.createPostBtn.text = "Post News"
            }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getActivity()?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    override fun onPause() {
        super.onPause()
        getActivity()?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
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
        FirebaseAuth.getInstance().currentUser?.email?.let { userId ->
            hideImage()
            val postId: String = args.id.ifEmpty { articleViewModel.generateRandomUid() }
            articleViewModel.insertPost(Post(
                id = postId,
                title = binding.headlineInput.text.toString(),
                desc = binding.descInput.text.toString(),
                articleUrl = binding.sourceInput.text.toString(),
                country = binding.countryInput.text.toString(),
                imageUrl = args.imageUrl.ifEmpty { "" },
                createdString = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH).format(Date()),
                userId = userId,
                username = FirebaseAuth.getInstance().currentUser?.displayName ?: ""
            ))
        }
    }

    fun validation(): Boolean {
        val headline = binding.headlineInput.text
        val articleUrl = binding.sourceInput.text
        val country = binding.countryInput.text

        if (newsActivity.uriResult.value == null && args.imageUrl.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.select_img), Toast.LENGTH_SHORT).show()
            return false
        }

        if (country.isNullOrEmpty()){
            Toast.makeText(requireContext(), getString(R.string.enter_country), Toast.LENGTH_SHORT).show()
            return false
        }

        if (headline.isNullOrEmpty()){
            Toast.makeText(requireContext(), getString(R.string.enter_headline), Toast.LENGTH_SHORT).show()
            return false
        }

        if (articleUrl.isNullOrEmpty()){
            Toast.makeText(requireContext(), getString(R.string.enter_url), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}