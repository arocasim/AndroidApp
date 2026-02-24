package com.example.myapplication

import Article
import ArticleAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = List(10) { Article("Lorem Ipsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.") }

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = GridLayoutManager(
                this@MainActivity,
                if (resources.configuration.smallestScreenWidthDp >= 600) 2 else 1
            )
            adapter = ArticleAdapter(data) { article ->
                startActivity(Intent(this@MainActivity, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_TITLE, article.title)
                    putExtra(DetailActivity.EXTRA_BODY, article.subtitle)
                })
            }
        }

        findViewById<ImageButton>(R.id.filterButton).setOnClickListener {
            startActivity(Intent(this, FilterActivity::class.java))
        }
    }
}