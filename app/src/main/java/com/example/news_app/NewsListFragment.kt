package com.example.news_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news_app.adapters.NewsListAdapter
import com.example.news_app.data.RetrofitInstance
import com.example.news_app.databinding.BottomSheetSourceFilterBinding
import com.example.news_app.databinding.FragmentNewsListBinding
import com.example.news_app.interfaces.NewsItemClickListener
import com.example.news_app.models.Article
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.ceil


class NewsListFragment : Fragment(), NewsItemClickListener {
    private lateinit var binding: FragmentNewsListBinding
    private val TAG: String = "NewsList Fragment"
    private lateinit var newsListAdapter: NewsListAdapter
    private var selectedCountryCode = "in"
    private var rbSelectedCountyId = R.id.rbIndia
    private val sourceList:MutableList<String> =ArrayList()
    private var page = 1
    private var totalPage =1
    var loadingNext = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val articleList = ArrayList<Article>()
        binding.rvNewsList.apply {
            newsListAdapter = NewsListAdapter(articleList, context, this@NewsListFragment)
            adapter = newsListAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        getNews()
        context?.let {
            ArrayAdapter.createFromResource(
                it, R.array.sort_by_array, android.R.layout.simple_spinner_dropdown_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spSortBy.adapter = adapter
            }
        }
        binding.btnSearch.setOnClickListener {
            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentView, SearchFragment())
                .addToBackStack("search")
                .commit()
        }
        binding.tvCountry.setOnClickListener {
            showCountryBottomSheet()
        }
        binding.fabSourceFilter.setOnClickListener {
            showSourceFilterBottomSheet()
        }
        binding.rvNewsList.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(!recyclerView.canScrollVertically(1)){
                    if(page<totalPage&& !loadingNext){

                        page++;
                        getNextNews();
                    }
                }
            }

        })
    }

    private fun getNews() {
        lifecycleScope.launchWhenStarted {
            val response = try {
                showLoader()
                page = 1
                if(sourceList.isEmpty()) RetrofitInstance.api.getTopHeadLines(country = selectedCountryCode,page= page) else RetrofitInstance.api.getFilteredTopHeadLines(sources = sourceList.joinToString(),page = 1)

            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                return@launchWhenStarted
            }
            if (response.isSuccessful && response.body() != null) {
                totalPage = ceil(response.body()!!.totalResults/20.0).toInt()
                newsListAdapter.articleList.clear()
                newsListAdapter.articleList.addAll(response.body()!!.articles.toMutableList())
                binding.progressBar.visibility = View.GONE
                newsListAdapter.loadNext = page<totalPage
                newsListAdapter.notifyDataSetChanged()
                hideLoader()
            }
        }
    }
    private fun getNextNews() {
        lifecycleScope.launchWhenStarted {
            val response = try {
                loadingNext = true
                if(sourceList.isEmpty()) RetrofitInstance.api.getTopHeadLines(country = selectedCountryCode,page= page) else RetrofitInstance.api.getFilteredTopHeadLines(sources = sourceList.joinToString(),page = page)
            } catch (e: Exception) {
                return@launchWhenStarted
            }
            if (response.isSuccessful && response.body() != null) {
                totalPage = ceil(response.body()!!.totalResults/20.0).toInt()
                newsListAdapter.articleList.addAll(response.body()!!.articles)
                newsListAdapter.loadNext = page<totalPage
                newsListAdapter.notifyDataSetChanged()
                loadingNext =false
            }
        }
    }

    private fun showCountryBottomSheet() {
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(R.layout.bottom_sheet_choose_location)
        bottomSheet.show()
        val btnApply = bottomSheet.findViewById<Button>(R.id.btnApply)
        val rgCountry = bottomSheet.findViewById<RadioGroup>(R.id.rgCountry)
        val rbSelectedCountry = bottomSheet.findViewById<RadioButton>(rbSelectedCountyId)
        rbSelectedCountry?.isChecked = true
        btnApply?.setOnClickListener {
            val rgSelectedCountry = rgCountry?.checkedRadioButtonId
            val radioButton = bottomSheet.findViewById<RadioButton>(rgSelectedCountry!!)
            bottomSheet.hide()
            rbSelectedCountyId = rgSelectedCountry
            binding.tvCountry.text = radioButton!!.text
            selectedCountryCode = radioButton.tag!!.toString()
            sourceList.clear()
            getNews()

        }
    }

    override fun onClick(article: Article) {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .add(R.id.fragmentView, NewsDetailsFragment(article))
            .addToBackStack("newsDetails")
            .commit()
    }

    private fun showSourceFilterBottomSheet() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val sourceBinding = BottomSheetSourceFilterBinding.inflate(layoutInflater,null,false )
        bottomSheet.setContentView(sourceBinding.root)
        bottomSheet.show()
        sourceBinding.cbAbc.isChecked = sourceList.contains(sourceBinding.cbAbc.tag.toString())
        sourceBinding.cbBild.isChecked = sourceList.contains(sourceBinding.cbBild.tag.toString())
        sourceBinding.cbTOI.isChecked = sourceList.contains(sourceBinding.cbTOI.tag.toString())
        sourceBinding.cbTalkSport.isChecked = sourceList.contains(sourceBinding.cbTalkSport.tag.toString())
        sourceBinding.btnApply.setOnClickListener{
            sourceList.clear()
            if(sourceBinding.cbAbc.isChecked) sourceList.add(sourceBinding.cbAbc.tag.toString())
            if(sourceBinding.cbBild.isChecked) sourceList.add(sourceBinding.cbBild.tag.toString())
            if(sourceBinding.cbTOI.isChecked) sourceList.add(sourceBinding.cbTOI.tag.toString())
            if(sourceBinding.cbTalkSport.isChecked) sourceList.add(sourceBinding.cbTalkSport.tag.toString())
            bottomSheet.hide()
            getNews()
        }
    }
    private fun showLoader(){
        binding.rvNewsList.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }
    private fun hideLoader(){
        binding.rvNewsList.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

}