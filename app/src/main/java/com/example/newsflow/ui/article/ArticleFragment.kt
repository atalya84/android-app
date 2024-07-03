package com.example.newsflow.ui.article

import android.os.Bundle
import android.util.Log
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.newsflow.R
import com.example.newsflow.databinding.FragmentArticleBinding
import com.example.newsflow.util.ImageUtil
import android.text.style.ClickableSpan
import android.content.Intent
import android.net.Uri
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import androidx.core.content.ContextCompat
import com.example.newsflow.ui.NewsActivity

class ArticleFragment : Fragment() {

    private lateinit var viewModel: ArticleViewModel
    private lateinit var binding: FragmentArticleBinding
    private val newsActivity: NewsActivity
        get() = activity as NewsActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentArticleBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(
            requireActivity(),
            ArticleViewModel.ArticleModelFactory()
        )[ArticleViewModel::class.java]

        newsActivity.hideNavBar()

        viewModel.articleLiveData.observe(viewLifecycleOwner) { post ->
            binding.articleImage.setImageURI(post.imageUrl.toUri())
            ImageUtil.loadImage(post.imageUrl.toUri(), binding.articleImage)
            binding.articleCountryTag.text = post.country
            binding.articleTitle.text = post.title
            binding.articleDesc.text = post.desc
            binding.publisher.text = post.username
            binding.date.text = post.createdString.split(" ")[0]

            clickable_source_link(post.articleUrl)

            binding.articleReturnBtn.setOnClickListener {
                val destination: Int = when (viewModel.origin.value) {
                    ArticleViewModel.Origin.FEED -> R.id.feedFragment
                    ArticleViewModel.Origin.MY_NEWS -> R.id.userNewsFragment
                    else -> R.id.feedFragment
                }
                newsActivity.displayNavBar()
                view?.let { view ->
                    Navigation.findNavController(requireView()).popBackStack(destination,false)
                }
            }
        }
        return (binding.root)
    }

    fun clickable_source_link(url: String) {
        val textView: TextView = binding.articleSource
        val linkText = getString(R.string.source_placeholder)
        val spannableString = SpannableString(linkText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireContext(), R.color.yellow)
            }
        }
        spannableString.setSpan(clickableSpan, 0, linkText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // Adjust indexes based on "here"
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}