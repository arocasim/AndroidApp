package com.example.myapplication.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.model.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insert(article: Article): Long

    @Query("SELECT * FROM articles ORDER BY id DESC")
    fun getAll(): List<Article>

    @Query("SELECT * FROM articles WHERE id = :id LIMIT 1")
    fun getById(id: Int): Article?

    @Update
    fun update(article: Article)

    @Delete
    fun delete(article: Article)

    @Query("DELETE FROM articles")
    fun deleteAll()
}