package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class FilterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        val btnClose = findViewById<ImageButton>(R.id.btnClose)
        val btnReset = findViewById<Button>(R.id.btnReset)
        val btnApply = findViewById<Button>(R.id.btnApply)

        val chipCats = findViewById<ChipGroup>(R.id.chipGroupCategories)
        val chipStyle = findViewById<ChipGroup>(R.id.chipGroupStyle)
        val chipSort = findViewById<ChipGroup>(R.id.chipGroupSort)

        val sw = findViewById<SwitchCompat>(R.id.switchOption)

        fun clearChips(group: ChipGroup) {
            for (i in 0 until group.childCount) {
                (group.getChildAt(i) as? Chip)?.isChecked = false
            }
        }

        fun getSelectedChipText(group: ChipGroup): String? {
            val id = group.checkedChipId
            if (id == -1) return null
            return group.findViewById<Chip>(id)?.text?.toString()
        }

        fun getCheckedChipTexts(group: ChipGroup): ArrayList<String> {
            val res = arrayListOf<String>()
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i) as? Chip ?: continue
                if (chip.isChecked) res.add(chip.text.toString())
            }
            return res
        }

        btnClose.setOnClickListener { finish() }

        btnReset.setOnClickListener {
            clearChips(chipCats)
            clearChips(chipStyle)
            clearChips(chipSort)
            sw.isChecked = false
        }

        btnApply.setOnClickListener {
            val categories = getCheckedChipTexts(chipCats)
            val style = getSelectedChipText(chipStyle)
            val sortBy = getSelectedChipText(chipSort)
            val option = sw.isChecked

            val result = Intent().apply {
                putStringArrayListExtra(EXTRA_CATEGORIES, categories)
                putExtra(EXTRA_STYLE, style)
                putExtra(EXTRA_SORT, sortBy)
                putExtra(EXTRA_SWITCH, option)
            }
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }

    companion object {
        const val EXTRA_CATEGORIES = "extra_categories"
        const val EXTRA_STYLE = "extra_style"
        const val EXTRA_SORT = "extra_sort"
        const val EXTRA_SWITCH = "extra_switch"
    }
}