package com.example.myapplication.ui.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.ArticleAdapter
import com.example.myapplication.data.model.Article
import com.example.myapplication.di.DiHelper
import com.example.myapplication.ui.MainContract
import com.google.android.material.chip.Chip

class MainActivity : AppCompatActivity(), MainContract.View {

    private lateinit var presenter: MainContract.Presenter
    private lateinit var recyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter

    private var currentCategories = mutableListOf<String>()
    private var currentStyle: String? = null
    private var currentSortBy: String? = null
    private var currentOnlyNewest = false
    private var currentSearchText = ""

    private val filterLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK) return@registerForActivityResult

            val data = result.data ?: return@registerForActivityResult

            currentCategories =
                data.getStringArrayListExtra(FilterActivity.EXTRA_CATEGORIES)?.toMutableList()
                    ?: mutableListOf()

            currentStyle = data.getStringExtra(FilterActivity.EXTRA_STYLE)
            currentSortBy = data.getStringExtra(FilterActivity.EXTRA_SORT)
            currentOnlyNewest =
                data.getBooleanExtra(FilterActivity.EXTRA_ONLY_NEWEST, false)

            applyCurrentFilters()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = DiHelper.getPresenter(this, this)
        recyclerView = findViewById(R.id.recyclerView)

        setupRecyclerView()
        setupButtons()
        setupMainChips()
        setupSearch()

        presenter.restoreLastFilter()
        presenter.syncArticlesFromServer()
        presenter.loadFiltersFromServer()
    }

    override fun onResume() {
        super.onResume()
        presenter.loadArticlesFromDb()
        applyCurrentFilters()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(
            this,
            if (resources.configuration.smallestScreenWidthDp >= 600) 2 else 1
        )

        articleAdapter = ArticleAdapter(
            onClick = { article -> openDetails(article.id) },
            onEdit = { article -> openEditor(article.id) },
            onDelete = { article -> presenter.deleteArticle(article) }
        )

        recyclerView.adapter = articleAdapter
    }

    private fun setupButtons() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<ImageButton>(R.id.filterButton).setOnClickListener {
            filterLauncher.launch(Intent(this, FilterActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btnAdd).setOnClickListener {
            openEditor()
        }
    }

    private fun setupMainChips() {
        val chipTech = findViewById<Chip>(R.id.chipMain1)
        val chipScience = findViewById<Chip>(R.id.chipMain2)
        val chipEnv = findViewById<Chip>(R.id.chipMain3)

        val updateCategories = {
            currentCategories.clear()

            if (chipTech.isChecked) currentCategories.add("Technology")
            if (chipScience.isChecked) currentCategories.add("Science")
            if (chipEnv.isChecked) currentCategories.add("Environment")

            applyCurrentFilters()
        }

        chipTech.setOnCheckedChangeListener { _, _ -> updateCategories() }
        chipScience.setOnCheckedChangeListener { _, _ -> updateCategories() }
        chipEnv.setOnCheckedChangeListener { _, _ -> updateCategories() }
    }

    private fun setupSearch() {
        findViewById<EditText>(R.id.searchInput).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                currentSearchText = s?.toString()?.trim().orEmpty()
                applyCurrentFilters()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun applyCurrentFilters() {
        presenter.applyFilters(
            searchText = currentSearchText,
            categories = currentCategories,
            style = currentStyle,
            sortBy = currentSortBy,
            onlyNewest = currentOnlyNewest
        )
    }

    private fun openDetails(articleId: Int) {
        startActivity(
            Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_ID, articleId)
            }
        )
    }

    private fun openEditor(articleId: Int? = null) {
        startActivity(
            Intent(this, EditArticleActivity::class.java).apply {
                articleId?.let { putExtra(EditArticleActivity.EXTRA_ID, it) }
            }
        )
    }

    override fun showArticles(items: List<Article>) {
        articleAdapter.updateItems(items)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun updateFilterState(
        categories: List<String>,
        style: String?,
        sortBy: String?,
        onlyNewest: Boolean
    ) {
        currentCategories = categories.toMutableList()
        currentStyle = style
        currentSortBy = sortBy
        currentOnlyNewest = onlyNewest
    }
}