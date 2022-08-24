package com.example.news_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.news_app.databinding.ItemLoadingBinding
import com.example.news_app.databinding.ItemNewsListBinding
import com.example.news_app.interfaces.NewsItemClickListener
import com.example.news_app.models.Article

class NewsListAdapter(
    var articleList: MutableList<Article>,
    private val context: Context,
    private val newsItemClickListener: NewsItemClickListener,
    public var loadNext: Boolean = false
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_NEWS_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    inner class ViewHolder(val binding: ItemNewsListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.newsListItem.setOnClickListener {
                newsItemClickListener.onClick(articleList[adapterPosition])
            }
        }
    }

    inner class LoadingViewHolder(val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoadingViewHolder(binding)
            }
            else -> {
                val binding: ItemNewsListBinding =
                    ItemNewsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolder(binding)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
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

    }

    override fun getItemCount(): Int {
        return if (loadNext) articleList.size + 1 else articleList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (loadNext && position == articleList.size) VIEW_TYPE_LOADING else VIEW_TYPE_NEWS_ITEM
    }
}