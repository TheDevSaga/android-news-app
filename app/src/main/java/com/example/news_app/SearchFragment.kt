package com.example.news_app

import android.content.Context
import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news_app.adapters.NewsListAdapter
import com.example.news_app.data.RetrofitInstance
import com.example.news_app.databinding.FragmentSearchBinding
import com.example.news_app.interfaces.NewsItemClickListener
import com.example.news_app.models.Article
import java.lang.Exception


class SearchFragment : Fragment(),NewsItemClickListener{

    private lateinit var binding: FragmentSearchBinding
    private lateinit var newsListAdapter:NewsListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentSearchBinding.inflate(inflater,container,false)
        binding.materialToolbar3.apply {
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                hideKeyboard()
                requireActivity().onBackPressed()

            }
            title = "Search"
            setTitleTextColor(resources.getColor(R.color.white))
            isTitleCentered = true
        }
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvNewsList.apply {
            newsListAdapter = NewsListAdapter(ArrayList(),context,this@SearchFragment)
            adapter = newsListAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        binding.etSearch.requestFocus()
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.etSearch, InputMethodManager.SHOW_FORCED)
        binding.etSearch.addTextChangedListener(onTextChanged = {text, start, before, count ->
            searchNews(text.toString())
        })
        binding.root.setOnClickListener{
            hideKeyboard()
        }
    }

    private fun searchNews(searchText:String){
        lifecycleScope.launchWhenStarted {
            val response = try {
                RetrofitInstance.api.getSearchNes(searchText)
            }catch(e: Exception){
                return@launchWhenStarted
            }
            if(response.isSuccessful && response.body()!=null){
                newsListAdapter.articleList = response.body()!!.articles.toMutableList()
                newsListAdapter.notifyDataSetChanged()
                if(newsListAdapter.articleList.isEmpty()){
                    binding.rvNewsList.visibility = View.GONE
                    binding.imageView.visibility =View.VISIBLE
                    binding.textView3.visibility =View.VISIBLE
                    binding.textView3.text = "No Result Found"
                }else{
                    binding.rvNewsList.visibility =View.VISIBLE
                    binding.imageView.visibility =View.GONE
                    binding.textView3.visibility = View.GONE

                }
            }
        }

    }

    override fun onClick(article: Article) {
        hideKeyboard()
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .add(R.id.fragmentView,NewsDetailsFragment(article)).addToBackStack("searchPage")
            .commit()
    }
    private fun hideKeyboard(){
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputMethodManager.isAcceptingText){
            inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken,0)

        }
    }
}