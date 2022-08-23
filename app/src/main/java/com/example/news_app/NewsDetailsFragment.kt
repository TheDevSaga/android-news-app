package com.example.news_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.news_app.databinding.FragmentNewsDetailsBinding
import com.example.news_app.models.Article

class NewsDetailsFragment(private val articleModel:Article) : Fragment() {
    private lateinit var  binding: FragmentNewsDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentNewsDetailsBinding.inflate(inflater,container,false)
        binding.materialToolbar2.setNavigationIcon(R.drawable.ic_back)
        binding.materialToolbar2.setNavigationOnClickListener{
            requireActivity().onBackPressed()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(view).load(articleModel.urlToImage).centerCrop().into(binding.ivCover)
        binding.tvHeadline.text = articleModel.title
        binding.tvDicription.text  = articleModel.description
        binding.tvTime.text = articleModel.publishedAt
        binding.tvSource.text = articleModel.source.name
        binding.btnSeeFull.setOnClickListener{
            Intent().apply {
                this.action = Intent.ACTION_VIEW
                this.data = Uri.parse(articleModel.url)
                startActivity(this)
            }
        }
    }
}