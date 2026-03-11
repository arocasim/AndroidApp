package com.example.myapplication.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.model.FilterCategoryCrossRef

@Dao
interface FilterCategoryDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insert(crossRef: FilterCategoryCrossRef)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insertAll(crossRefs: List<FilterCategoryCrossRef>)

    @Query("SELECT categoryName FROM filter_category_cross_ref WHERE filterId = :filterId")
    fun getCategoriesForFilter(filterId: Int): List<String>

    @Query("DELETE FROM filter_category_cross_ref WHERE filterId = :filterId")
    fun deleteByFilterId(filterId: Int)

    @Query("DELETE FROM filter_category_cross_ref")
    fun deleteAll()
}