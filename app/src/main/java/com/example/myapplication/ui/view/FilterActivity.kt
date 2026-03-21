package com.example.myapplication.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.myapplication.R
import com.example.myapplication.data.api.FiltersResponse
import com.example.myapplication.data.local.database.AppDatabase
import com.example.myapplication.data.model.FilterCategoryCrossRef
import com.example.myapplication.data.model.SearchFilter
import com.example.myapplication.data.repository.ArticleRepository
import com.example.myapplication.di.DiHelper
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class FilterActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var repository: ArticleRepository

    private lateinit var chipCats: ChipGroup
    private lateinit var chipStyle: ChipGroup
    private lateinit var chipSort: ChipGroup
    private lateinit var swNewest: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        db = DiHelper.getDatabase(this)
        repository = DiHelper.getRepository(this)

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
        repository.getFiltersFromServer(object : ArticleRepository.FiltersCallback {
            override fun onSuccess(response: FiltersResponse) {
                showFilters(response)
                restoreLastFilter()
            }

            override fun onFailure(message: String) {
                Log.d("API_FILTERS", message)
            }
        })
    }

    private fun showFilters(data: FiltersResponse) {
        chipCats.removeAllViews()
        chipStyle.removeAllViews()
        chipSort.removeAllViews()

        data.filters.categories.forEach { category ->
            chipCats.addView(createChip(category))
        }

        data.filters.styles.forEach { style ->
            chipStyle.addView(createChip(style))
        }

        data.filters.sortOptions.forEach { sortOption ->
            chipSort.addView(createChip(sortOption))
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

        val crossRefs = categories.map { category ->
            FilterCategoryCrossRef(
                filterId = filterId,
                categoryName = category
            )
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
            if (chip.isChecked) {
                result.add(chip.text.toString())
            }
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