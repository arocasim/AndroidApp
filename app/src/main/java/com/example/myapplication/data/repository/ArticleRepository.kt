    package com.example.myapplication.data.repository

    import com.example.myapplication.data.api.ApiResponse
    import com.example.myapplication.data.api.FiltersResponse
    import com.example.myapplication.data.api.IDataSource
    import com.example.myapplication.data.local.database.AppDatabase
    import com.example.myapplication.data.model.Article
    import com.example.myapplication.data.model.SearchFilter

    class ArticleRepository(
        private val service: IDataSource,
        private val db: AppDatabase
    ) {

        fun getArticlesFromDb(): List<Article> {
            return db.articleDao().getAll()
        }

        fun syncArticles(callback: SyncArticlesCallback) {
            service.getArticles(object : IDataSource.ArticlesCallback {
                override fun onSuccess(response: ApiResponse) {
                    db.articleDao().deleteAll()

                    response.articles.forEach { article ->
                        db.articleDao().insert(
                            Article(
                                title = article.title,
                                subtitle = article.subtitle,
                                content = article.content,
                                author = article.author,
                                category = article.category,
                                date = article.date
                            )
                        )
                    }

                    callback.onSuccess(db.articleDao().getAll())
                }

                override fun onFailure(message: String) {
                    callback.onFailure(message, db.articleDao().getAll())
                }
            })
        }

        fun getFiltersFromServer(callback: FiltersCallback) {
            service.getFilters(object : IDataSource.FiltersCallback {
                override fun onSuccess(response: FiltersResponse) {
                    callback.onSuccess(response)
                }

                override fun onFailure(message: String) {
                    callback.onFailure(message)
                }
            })
        }

        fun deleteArticle(article: Article) {
            db.articleDao().delete(article)
        }

        fun getLastFilter(): SearchFilter? {
            return db.searchFilterDao().getLastFilter()
        }

        fun getCategoriesForFilter(filterId: Int): List<String> {
            return db.filterCategoryDao().getCategoriesForFilter(filterId)
        }

        interface SyncArticlesCallback {
            fun onSuccess(articles: List<Article>)
            fun onFailure(message: String, cachedArticles: List<Article>)
        }

        interface FiltersCallback {
            fun onSuccess(response: FiltersResponse)
            fun onFailure(message: String)
        }
    }