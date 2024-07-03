package com.example.newsflow.ui.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.newsflow.R
import com.example.newsflow.databinding.FragmentArticleBinding
import com.example.newsflow.util.ImageUtil

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
            binding.articleSource.text = post.articleUrl
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
}