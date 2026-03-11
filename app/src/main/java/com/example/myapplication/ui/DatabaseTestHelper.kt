package com.example.myapplication.test

import android.util.Log
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.model.Article

class DatabaseTestHelper(private val db: AppDatabase) {

    fun runAllTests() {
        testInsert()
        testRead()
        testUpdate()
        testDelete()
        testRead()
    }

    fun testInsert() {
        Log.d("DB_TEST", "Insert test")

        val article = Article(
            id = 999,
            title = "Test Article",
            subtitle = "Subtitle",
            content = "Test content for database",
            author = "Tester",
            category = "Technology",
            date = "2026-03-11"
        )

        db.articleDao().insert(article)
    }

    fun testRead() {
        Log.d("DB_TEST", "Read test")

        val articles = db.articleDao().getAll()

        articles.forEach {
            Log.d("DB_TEST", "Article: ${it.title} by ${it.author}")
        }
    }

    fun testUpdate() {
        Log.d("DB_TEST", "Update test")

        val articles = db.articleDao().getAll()
        if (articles.isNotEmpty()) {

            val article = articles.first().copy(
                title = "Updated title"
            )

            db.articleDao().update(article)
        }
    }

    fun testDelete() {
        Log.d("DB_TEST", "Delete test")

        val articles = db.articleDao().getAll()
        if (articles.isNotEmpty()) {
            db.articleDao().delete(articles.last())
        }
    }
}