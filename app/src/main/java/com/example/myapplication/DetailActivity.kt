package com.example.myapplication

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        findViewById<TextView>(R.id.tvTitle).text =
            intent.getStringExtra(EXTRA_TITLE) ?: "Lorem Ipsum"

        findViewById<TextView>(R.id.tvBody).text =
            intent.getStringExtra(EXTRA_BODY) ?: "Lorem ipsum dolor sit amet..."

        findViewById<ImageButton>(R.id.btnClose).setOnClickListener { finish() }
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
    }
}