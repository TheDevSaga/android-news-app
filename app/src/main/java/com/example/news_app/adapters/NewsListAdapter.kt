package com.example.news_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.news_app.R
import com.example.news_app.databinding.ItemNewsListBinding
import com.example.news_app.interfaces.NewsItemClickListener
import com.example.news_app.models.Article

class NewsListAdapter(var articleList:List<Article>, private val context:Context,private val newsItemClickListener: NewsItemClickListener):
    RecyclerView.Adapter<NewsListAdapter.ViewHolder>() {



    inner class ViewHolder(val binding: ItemNewsListBinding):RecyclerView.ViewHolder(binding.root){
        init {
            binding.newsListItem.setOnClickListener{
               newsItemClickListener.onClick(articleList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:ItemNewsListBinding = ItemNewsListBinding.inflate(LayoutInflater.from(parent.context),parent,false)


        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articleList[position]
        holder.binding.apply {
            tvNewsHeadline.text = article.title
            tvNewsSource.text = article.source.name
            tvDateTime.text = article.publishedAt
            Glide
                .with(context)
                .load(article.urlToImage)
                .centerCrop()
                .into(icNewsCover)
        }

    }

    override fun getItemCount(): Int {
        return articleList.size
    }

}