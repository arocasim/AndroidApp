package com.example.myapplication.ui.presenter

import android.util.Log
import com.example.myapplication.data.model.Article
import com.example.myapplication.data.repository.ArticleRepository
import com.example.myapplication.ui.MainContract

class MainPresenter(
    private val view: MainContract.View,
    private val repository: ArticleRepository
) : MainContract.Presenter {

    private var allArticles: List<Article> = emptyList()

    override fun restoreLastFilter() {
        val lastFilter = repository.getLastFilter() ?: return
        val categories = repository.getCategoriesForFilter(lastFilter.id)

        view.updateFilterState(
            categories = categories,
            style = lastFilter.style,
            sortBy = lastFilter.sortBy,
            onlyNewest = lastFilter.onlyNewest
        )
    }

    override fun loadArticlesFromDb() {
        allArticles = repository.getArticlesFromDb()
        view.showArticles(allArticles)
    }

    override fun syncArticlesFromServer() {
        repository.syncArticles(object : ArticleRepository.SyncArticlesCallback {
            override fun onSuccess(articles: List<Article>) {
                allArticles = articles
                Log.d("API_ARTICLES", "Data loaded from server: ${articles.size} articles")
                view.showArticles(allArticles)
            }

            override fun onFailure(message: String, cachedArticles: List<Article>) {
                Log.d("API_ARTICLES", message)
                allArticles = cachedArticles
                view.showArticles(allArticles)
                view.showError(message)
            }
        })
    }

    override fun loadFiltersFromServer() {
        repository.getFiltersFromServer(object : ArticleRepository.FiltersCallback {
            override fun onSuccess(response: com.example.myapplication.data.api.FiltersResponse) {
                Log.d("API_FILTERS", "Categories: ${response.filters.categories}")
                Log.d("API_FILTERS", "Styles: ${response.filters.styles}")
                Log.d("API_FILTERS", "Sort options: ${response.filters.sortOptions}")
                Log.d("API_FILTERS", "Only newest: ${response.filters.onlyNewest}")
            }

            override fun onFailure(message: String) {
                Log.d("API_FILTERS", message)
            }
        })
    }

    override fun deleteArticle(article: Article) {
        repository.deleteArticle(article)
        loadArticlesFromDb()
    }

    override fun applyFilters(
        searchText: String,
        categories: List<String>,
        style: String?,
        sortBy: String?,
        onlyNewest: Boolean
    ) {
        var filtered = allArticles

        if (categories.isNotEmpty()) {
            filtered = filtered.filter { it.category in categories }
        }

        filtered = filterByStyle(filtered, style)

        if (searchText.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(searchText, ignoreCase = true) ||
                        it.subtitle.contains(searchText, ignoreCase = true) ||
                        it.author.contains(searchText, ignoreCase = true)
            }
        }

        filtered = sortArticles(filtered, sortBy)

        if (onlyNewest) {
            filtered = filtered.take(3)
        }

        view.showArticles(filtered)
    }

    private fun filterByStyle(items: List<Article>, style: String?): List<Article> {
        return when (style) {
            "Short" -> items.filter { it.content.length < 140 }
            "Medium" -> items.filter { it.content.length in 140..220 }
            "Long" -> items.filter { it.content.length > 220 }
            else -> items
        }
    }

    private fun sortArticles(items: List<Article>, sortBy: String?): List<Article> {
        return when (sortBy) {
            "Title" -> items.sortedBy { it.title }
            "Author" -> items.sortedBy { it.author }
            "Newest" -> items.sortedByDescending { it.date }
            else -> items
        }
    }
}