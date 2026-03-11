package com.example.myapplication.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.model.SearchFilter

@Dao
interface SearchFilterDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insert(filter: SearchFilter): Long

    @Query("SELECT * FROM search_filters ORDER BY id DESC")
    fun getAll(): List<SearchFilter>

    @Query("SELECT * FROM search_filters ORDER BY id DESC LIMIT 1")
    fun getLastFilter(): SearchFilter?

    @Update
    fun update(filter: SearchFilter)

    @Delete
    fun delete(filter: SearchFilter)

    @Query("DELETE FROM search_filters")
    fun deleteAll()
}