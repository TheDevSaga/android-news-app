package com.example.news_app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news_app.adapters.NewsListAdapter
import com.example.news_app.data.RetrofitInstance
import com.example.news_app.databinding.FragmentNewsListBinding
import com.example.news_app.interfaces.NewsItemClickListener
import com.example.news_app.models.Article
import com.example.news_app.models.Source
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.lang.Exception


class NewsListFragment : Fragment(),NewsItemClickListener {
    private lateinit var binding:FragmentNewsListBinding
    private val TAG:String = "NewsList Fragment"
    private lateinit var newsListAdapter: NewsListAdapter
    private var selectedCountryCode= "in"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val articleList = ArrayList<Article>()
        binding.rvNewsList.apply {
            newsListAdapter = NewsListAdapter(articleList,context,this@NewsListFragment)
            adapter = newsListAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        getNews()
        context?.let {
            ArrayAdapter.createFromResource(
                it,R.array.sort_by_array, android.R.layout.simple_spinner_dropdown_item
            ).also {
                adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spSortBy.adapter = adapter
            }
        }
        binding.btnSearch.setOnClickListener {
            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentView,SearchFragment())
                .addToBackStack("search")
                .commit()
        }
        binding.tvCountry.setOnClickListener {
            showCountryBottomSheet()
        }
    }

    private fun getNews(){
        lifecycleScope.launchWhenStarted {
            val response = try {
                RetrofitInstance.api.getTopHeadLines(country = selectedCountryCode)
            }catch(e:Exception){
                return@launchWhenStarted
            }
            if(response.isSuccessful && response.body()!=null){
                newsListAdapter.articleList = response.body()!!.articles
                binding.progressBar.visibility = View.GONE
                newsListAdapter.notifyDataSetChanged()
            }
        }
    }
    private fun showCountryBottomSheet(){
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(R.layout.bottom_sheet_choose_location)
        bottomSheet.show()
        val btnApply = bottomSheet.findViewById<Button>(R.id.btnApply)
        val rgCountry = bottomSheet.findViewById<RadioGroup>(R.id.rgCountry)
        btnApply?.setOnClickListener{
            val rgSelectedCountry =  rgCountry?.checkedRadioButtonId
            val radioButton = bottomSheet.findViewById<RadioButton>(rgSelectedCountry!!)
            bottomSheet.hide()
            binding.tvCountry.text = radioButton!!.text
            selectedCountryCode = radioButton.tag!!.toString()
            getNews()
        }
    }

    override fun onClick(article: Article) {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .add(R.id.fragmentView,NewsDetailsFragment(article))
            .addToBackStack("newsDetails")
            .commit()
    }
}