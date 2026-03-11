package com.example.myapplication.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.myapplication.R
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.model.Article
import com.google.android.material.button.MaterialButton

class EditArticleActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var articleId: Int = -1

    private lateinit var etTitle: EditText
    private lateinit var etSubtitle: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etCategory: EditText
    private lateinit var etDate: EditText
    private lateinit var etContent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_article)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "article_database"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

        etTitle = findViewById(R.id.etTitle)
        etSubtitle = findViewById(R.id.etSubtitle)
        etAuthor = findViewById(R.id.etAuthor)
        etCategory = findViewById(R.id.etCategory)
        etDate = findViewById(R.id.etDate)
        etContent = findViewById(R.id.etContent)

        articleId = intent.getIntExtra(EXTRA_ID, -1)

        if (articleId != -1) {
            loadArticleForEdit(articleId)
        }

        findViewById<ImageButton>(R.id.btnCloseEdit).setOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.btnSaveArticle).setOnClickListener {
            saveArticle()
        }
    }

    private fun loadArticleForEdit(id: Int) {
        val article = db.articleDao().getById(id) ?: return

        etTitle.setText(article.title)
        etSubtitle.setText(article.subtitle)
        etAuthor.setText(article.author)
        etCategory.setText(article.category)
        etDate.setText(article.date)
        etContent.setText(article.content)
    }

    private fun saveArticle() {
        val title = etTitle.text.toString().trim()
        val subtitle = etSubtitle.text.toString().trim()
        val author = etAuthor.text.toString().trim()
        val category = etCategory.text.toString().trim()
        val date = etDate.text.toString().trim()
        val content = etContent.text.toString().trim()

        if (title.isEmpty() || subtitle.isEmpty() || author.isEmpty() ||
            category.isEmpty() || date.isEmpty() || content.isEmpty()
        ) {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (articleId == -1) {
            val newArticle = Article(
                title = title,
                subtitle = subtitle,
                content = content,
                author = author,
                category = category,
                date = date
            )
            db.articleDao().insert(newArticle)
            Toast.makeText(this, "Article added", Toast.LENGTH_SHORT).show()
        } else {
            val updatedArticle = Article(
                id = articleId,
                title = title,
                subtitle = subtitle,
                content = content,
                author = author,
                category = category,
                date = date
            )
            db.articleDao().update(updatedArticle)
            Toast.makeText(this, "Article updated", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}