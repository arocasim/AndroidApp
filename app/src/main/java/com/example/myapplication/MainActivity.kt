package com.example.myapplication

import Article
import ArticleAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // щоб контент не залазив під статус-бар/навігацію
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- RecyclerView ---
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val data = List(10) {
            Article(
                title = "Lorem Ipsum",
                subtitle = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
            )
        }

        // клік по елементу -> DetailActivity
        val adapter = ArticleAdapter(data) { article ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_TITLE, article.title)
                putExtra(DetailActivity.EXTRA_BODY, article.subtitle)
            }
            startActivity(intent)
        }

        // адаптивність: телефон = 1 колонка, планшет = 2 колонки
        val spanCount = if (resources.configuration.smallestScreenWidthDp >= 600) 2 else 1
        recyclerView.layoutManager = GridLayoutManager(this, spanCount)

        recyclerView.adapter = adapter

        // --- Кнопка Filters ---
        val filterBtn = findViewById<ImageButton>(R.id.filterButton)
        filterBtn.setOnClickListener {
            startActivity(Intent(this, FilterActivity::class.java))
        }
    }
}