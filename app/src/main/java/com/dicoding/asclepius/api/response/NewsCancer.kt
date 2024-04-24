package com.dicoding.asclepius.api.response

data class NewsCancer(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsCancerArticles>
)

data class NewsCancerArticles (
    val source: NewsCancerArticlesSource,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?
)

data class NewsCancerArticlesSource (
    val id: String?,
    val name: String
)
