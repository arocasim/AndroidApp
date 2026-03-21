package com.example.myapplication.ui

import com.example.myapplication.data.model.Article

interface MainContract {

    interface View {
        fun showArticles(items: List<Article>)
        fun showError(message: String)
        fun updateFilterState(
            categories: List<String>,
            style: String?,
            sortBy: String?,
            onlyNewest: Boolean
        )
    }

    interface Presenter {
        fun restoreLastFilter()
        fun loadArticlesFromDb()
        fun syncArticlesFromServer()
        fun loadFiltersFromServer()
        fun deleteArticle(article: Article)
        fun applyFilters(
            searchText: String,
            categories: List<String>,
            style: String?,
            sortBy: String?,
            onlyNewest: Boolean
        )
    }
}