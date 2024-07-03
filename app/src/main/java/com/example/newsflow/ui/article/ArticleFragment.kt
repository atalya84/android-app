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

class ArticleFragment : Fragment() {

    private lateinit var viewModel: ArticleViewModel
    private lateinit var binding: FragmentArticleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentArticleBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(
            requireActivity(),
            ArticleViewModel.ArticleModelFactory()
        )[ArticleViewModel::class.java]

        viewModel.articleLiveData.observe(viewLifecycleOwner) { post ->
            ImageUtil.loadImage(post.imageUrl.toUri(), requireContext(), binding.articleImage)
            binding.articleImage.setImageURI(post.imageUrl.toUri())
            binding.articleCountryTag.text = post.country
            binding.articleTitle.text = post.title
            binding.articleDesc.text = post.desc

            clickable_source_link(post.articleUrl)

            binding.articleReturnBtn.setOnClickListener {
                val destination: Int = when (viewModel.origin.value) {
                    ArticleViewModel.Origin.FEED -> R.id.action_articleFragment_to_feedFragment
                    ArticleViewModel.Origin.MY_NEWS -> R.id.action_articleFragment_to_userNewsFragment
                    else -> R.id.action_articleFragment_to_feedFragment
                }
                view?.let { view ->
                    Navigation.findNavController(view).navigate(destination)
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