package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.myapplication.R
import com.example.myapplication.adapter.ArticleAdapter
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.model.Article
import com.example.myapplication.network.ApiClient
import com.google.android.material.chip.Chip

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter

    private var allArticles: List<Article> = emptyList()

    private var currentCategories = arrayListOf<String>()
    private var currentStyle: String? = null
    private var currentSortBy: String? = null
    private var currentOnlyNewest = false
    private var currentSearchText = ""

    private val filterLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK) return@registerForActivityResult

            val data = result.data ?: return@registerForActivityResult
            currentCategories =
                data.getStringArrayListExtra(FilterActivity.EXTRA_CATEGORIES) ?: arrayListOf()
            currentStyle = data.getStringExtra(FilterActivity.EXTRA_STYLE)
            currentSortBy = data.getStringExtra(FilterActivity.EXTRA_SORT)
            currentOnlyNewest =
                data.getBooleanExtra(FilterActivity.EXTRA_ONLY_NEWEST, false)

            applyAllFilters()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "article_database"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

        recyclerView = findViewById(R.id.recyclerView)

        setupRecyclerView()
        restoreLastFilter()
        setupMainChips()
        setupSearch()
        setupButtons()
        seedDatabase()
        loadFilters()
    }

    override fun onResume() {
        super.onResume()
        loadArticles()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(
            this,
            if (resources.configuration.smallestScreenWidthDp >= 600) 2 else 1
        )

        articleAdapter = ArticleAdapter(
            items = emptyList(),
            onClick = { openDetails(it.id) },
            onEdit = { openEditor(it.id) },
            onDelete = {
                db.articleDao().delete(it)
                loadArticles()
            }
        )
        recyclerView.adapter = articleAdapter
    }

    private fun setupButtons() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<ImageButton>(R.id.filterButton).setOnClickListener {
            filterLauncher.launch(Intent(this, FilterActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btnAdd).setOnClickListener {
            openEditor()
        }
    }

    private fun openDetails(articleId: Int) {
        startActivity(Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_ID, articleId)
        })
    }

    private fun openEditor(articleId: Int? = null) {
        startActivity(Intent(this, EditArticleActivity::class.java).apply {
            articleId?.let { putExtra(EditArticleActivity.EXTRA_ID, it) }
        })
    }

    private fun loadArticles() {
        allArticles = db.articleDao().getAll()
        applyAllFilters()
    }

    private fun updateAdapter(items: List<Article>) {
        articleAdapter = ArticleAdapter(
            items = items,
            onClick = { openDetails(it.id) },
            onEdit = { openEditor(it.id) },
            onDelete = {
                db.articleDao().delete(it)
                loadArticles()
            }
        )
        recyclerView.adapter = articleAdapter
    }

    private fun setupMainChips() {
        val chipTech = findViewById<Chip>(R.id.chipMain1)
        val chipScience = findViewById<Chip>(R.id.chipMain2)
        val chipEnv = findViewById<Chip>(R.id.chipMain3)

        val updateCategories = {
            currentCategories = arrayListOf<String>().apply {
                if (chipTech.isChecked) add("Technology")
                if (chipScience.isChecked) add("Science")
                if (chipEnv.isChecked) add("Environment")
            }
            applyAllFilters()
        }

        chipTech.setOnCheckedChangeListener { _, _ -> updateCategories() }
        chipScience.setOnCheckedChangeListener { _, _ -> updateCategories() }
        chipEnv.setOnCheckedChangeListener { _, _ -> updateCategories() }
    }

    private fun setupSearch() {
        findViewById<EditText>(R.id.searchInput).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                currentSearchText = s?.toString()?.trim().orEmpty()
                applyAllFilters()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun applyAllFilters() {
        var filtered = allArticles

        if (currentCategories.isNotEmpty()) {
            filtered = filtered.filter { it.category in currentCategories }
        }

        filtered = when (currentStyle) {
            "Short" -> filtered.filter { it.content.length < 140 }
            "Medium" -> filtered.filter { it.content.length in 140..220 }
            "Long" -> filtered.filter { it.content.length > 220 }
            else -> filtered
        }

        if (currentSearchText.isNotEmpty()) {
            filtered = filtered.filter {
                it.title.contains(currentSearchText, true) ||
                        it.subtitle.contains(currentSearchText, true) ||
                        it.author.contains(currentSearchText, true)
            }
        }

        filtered = when (currentSortBy) {
            "Title" -> filtered.sortedBy { it.title }
            "Author" -> filtered.sortedBy { it.author }
            "Newest" -> filtered.sortedByDescending { it.date }
            else -> filtered
        }

        if (currentOnlyNewest) {
            filtered = filtered.sortedByDescending { it.date }.take(3)
        }

        updateAdapter(filtered)
    }

    private fun seedDatabase() {
        val client = ApiClient()

        client.getArticles { data ->
            runOnUiThread {
                if (data != null) {
                    db.articleDao().deleteAll()

                    data.articles.forEach { article ->
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

                    Log.d("API", "Data loaded from server: ${data.articles.size} articles")
                    loadArticles()
                } else {
                    Log.d("API", "Failed to load data from server")
                }
            }
        }
    }

    private fun loadFilters() {
        val client = ApiClient()

        client.getFilters { data ->
            runOnUiThread {
                if (data != null) {
                    Log.d("API_FILTERS", "Categories: ${data.filters.categories}")
                    Log.d("API_FILTERS", "Styles: ${data.filters.styles}")
                    Log.d("API_FILTERS", "Sort options: ${data.filters.sortOptions}")
                    Log.d("API_FILTERS", "Only newest: ${data.filters.onlyNewest}")
                } else {
                    Log.d("API_FILTERS", "Failed to load filters")
                }
            }
        }
    }

    private fun restoreLastFilter() {
        val lastFilter = db.searchFilterDao().getLastFilter() ?: return

        currentCategories = ArrayList(
            db.filterCategoryDao().getCategoriesForFilter(lastFilter.id)
        )
        currentStyle = lastFilter.style
        currentSortBy = lastFilter.sortBy
        currentOnlyNewest = lastFilter.onlyNewest
    }
}