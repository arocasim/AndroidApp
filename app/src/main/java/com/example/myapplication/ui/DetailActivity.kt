package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.myapplication.ui.EditArticleActivity
import com.example.myapplication.R
import com.example.myapplication.database.AppDatabase
import com.google.android.material.button.MaterialButton

class DetailActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var articleId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "article_database"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

        articleId = intent.getIntExtra(EXTRA_ID, -1)

        loadArticleData()

        findViewById<ImageButton>(R.id.btnClose).setOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.btnEditDetail).setOnClickListener {
            if (articleId != -1) {
                startActivity(
                    Intent(this, EditArticleActivity::class.java).apply {
                        putExtra(EditArticleActivity.Companion.EXTRA_ID, articleId)
                    }
                )
            }
        }

        findViewById<MaterialButton>(R.id.btnDeleteDetail).setOnClickListener {
            if (articleId != -1) {
                val article = db.articleDao().getById(articleId)
                if (article != null) {
                    db.articleDao().delete(article)
                }
            }
            finish()
        }
    }

    private fun loadArticleData() {
        val article = db.articleDao().getById(articleId)

        findViewById<TextView>(R.id.tvTitle).text = article?.title ?: "Article"
        findViewById<TextView>(R.id.tvSubtitle).text = article?.subtitle ?: ""
        findViewById<TextView>(R.id.tvAuthor).text = "Author: ${article?.author ?: "-"}"
        findViewById<TextView>(R.id.tvCategory).text = "Category: ${article?.category ?: "-"}"
        findViewById<TextView>(R.id.tvDate).text = "Date: ${article?.date ?: "-"}"
        findViewById<TextView>(R.id.tvBody).text = article?.content ?: "No content available."
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}