package com.example.myapplication.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.data.model.Article
import com.example.myapplication.data.local.dao.ArticleDao
import com.example.myapplication.data.model.FilterCategoryCrossRef
import com.example.myapplication.data.local.dao.FilterCategoryDao
import com.example.myapplication.data.model.SearchFilter
import com.example.myapplication.data.local.dao.SearchFilterDao

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