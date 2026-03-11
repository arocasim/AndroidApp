package com.example.myapplication.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.model.Article
import com.example.myapplication.dao.ArticleDao
import com.example.myapplication.model.FilterCategoryCrossRef
import com.example.myapplication.dao.FilterCategoryDao
import com.example.myapplication.model.SearchFilter
import com.example.myapplication.dao.SearchFilterDao

@Database(
    entities = [
        Article::class,
        SearchFilter::class,
        FilterCategoryCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun searchFilterDao(): SearchFilterDao
    abstract fun filterCategoryDao(): FilterCategoryDao
}