package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.room.Room
import com.example.myapplication.R
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.model.FilterCategoryCrossRef
import com.example.myapplication.model.SearchFilter
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.FiltersResponse
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class FilterActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var chipCats: ChipGroup
    private lateinit var chipStyle: ChipGroup
    private lateinit var chipSort: ChipGroup
    private lateinit var swNewest: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "article_database"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

        chipCats = findViewById(R.id.chipGroupCategories)
        chipStyle = findViewById(R.id.chipGroupStyle)
        chipSort = findViewById(R.id.chipGroupSort)
        swNewest = findViewById(R.id.switchOption1)

        loadFiltersFromServer()

        findViewById<ImageButton>(R.id.btnClose).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnReset).setOnClickListener { resetFilters() }
        findViewById<Button>(R.id.btnApply).setOnClickListener { applyFilters() }
    }

    private fun loadFiltersFromServer() {
        val client = ApiClient()

        client.getFilters { data ->
            runOnUiThread {
                if (data != null) {
                    showFilters(data)
                    restoreLastFilter()
                } else {
                    Log.d("API_FILTERS", "Failed to load filters")
                }
            }
        }
    }

    private fun showFilters(data: FiltersResponse) {
        chipCats.removeAllViews()
        chipStyle.removeAllViews()
        chipSort.removeAllViews()

        data.filters.categories.forEach {
            chipCats.addView(createChip(it))
        }

        data.filters.styles.forEach {
            chipStyle.addView(createChip(it))
        }

        data.filters.sortOptions.forEach {
            chipSort.addView(createChip(it))
        }
    }

    private fun createChip(text: String): Chip {
        return Chip(this).apply {
            this.text = text
            isCheckable = true
            setTextColor(getColor(R.color.text_primary))
            chipBackgroundColor = getColorStateList(R.color.primary_light)
            chipStrokeColor = getColorStateList(R.color.primary)
            chipStrokeWidth = 1f
        }
    }

    private fun restoreLastFilter() {
        val lastFilter = db.searchFilterDao().getLastFilter() ?: return

        val categories = db.filterCategoryDao().getCategoriesForFilter(lastFilter.id)
        setCheckedChipsByTexts(chipCats, categories)
        setCheckedChipByText(chipStyle, lastFilter.style)
        setCheckedChipByText(chipSort, lastFilter.sortBy)
        swNewest.isChecked = lastFilter.onlyNewest
    }

    private fun resetFilters() {
        clearChips(chipCats)
        clearChips(chipStyle)
        clearChips(chipSort)
        swNewest.isChecked = false

        val filterId = db.searchFilterDao().insert(
            SearchFilter(
                style = null,
                sortBy = null,
                onlyNewest = false
            )
        ).toInt()

        db.filterCategoryDao().deleteByFilterId(filterId)

        setResult(
            RESULT_OK,
            Intent().apply {
                putStringArrayListExtra(EXTRA_CATEGORIES, arrayListOf())
                putExtra(EXTRA_STYLE, null as String?)
                putExtra(EXTRA_SORT, null as String?)
                putExtra(EXTRA_ONLY_NEWEST, false)
            }
        )
        finish()
    }

    private fun applyFilters() {
        val categories = getCheckedChipTexts(chipCats)
        val style = getSelectedChipText(chipStyle)
        val sortBy = getSelectedChipText(chipSort)
        val onlyNewest = swNewest.isChecked

        val filterId = db.searchFilterDao().insert(
            SearchFilter(
                style = style,
                sortBy = sortBy,
                onlyNewest = onlyNewest
            )
        ).toInt()

        val crossRefs = categories.map {
            FilterCategoryCrossRef(filterId = filterId, categoryName = it)
        }
        db.filterCategoryDao().insertAll(crossRefs)

        setResult(
            RESULT_OK,
            Intent().apply {
                putStringArrayListExtra(EXTRA_CATEGORIES, categories)
                putExtra(EXTRA_STYLE, style)
                putExtra(EXTRA_SORT, sortBy)
                putExtra(EXTRA_ONLY_NEWEST, onlyNewest)
            }
        )
        finish()
    }

    private fun clearChips(group: ChipGroup) {
        for (i in 0 until group.childCount) {
            (group.getChildAt(i) as? Chip)?.isChecked = false
        }
    }

    private fun getSelectedChipText(group: ChipGroup): String? {
        val id = group.checkedChipId
        if (id == -1) return null
        return group.findViewById<Chip>(id)?.text?.toString()
    }

    private fun getCheckedChipTexts(group: ChipGroup): ArrayList<String> {
        val result = arrayListOf<String>()
        for (i in 0 until group.childCount) {
            val chip = group.getChildAt(i) as? Chip ?: continue
            if (chip.isChecked) result.add(chip.text.toString())
        }
        return result
    }

    private fun setCheckedChipByText(group: ChipGroup, text: String?) {
        if (text == null) return
        for (i in 0 until group.childCount) {
            val chip = group.getChildAt(i) as? Chip ?: continue
            chip.isChecked = chip.text.toString() == text
        }
    }

    private fun setCheckedChipsByTexts(group: ChipGroup, values: List<String>) {
        for (i in 0 until group.childCount) {
            val chip = group.getChildAt(i) as? Chip ?: continue
            chip.isChecked = chip.text.toString() in values
        }
    }

    companion object {
        const val EXTRA_CATEGORIES = "extra_categories"
        const val EXTRA_STYLE = "extra_style"
        const val EXTRA_SORT = "extra_sort"
        const val EXTRA_ONLY_NEWEST = "extra_only_newest"
    }
}