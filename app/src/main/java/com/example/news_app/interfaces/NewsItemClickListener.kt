package com.example.news_app.interfaces

import com.example.news_app.models.Article

interface NewsItemClickListener {
    fun onClick(article:Article)
}