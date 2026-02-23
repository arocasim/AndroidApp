package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Lorem Ipsum"
        val body = intent.getStringExtra(EXTRA_BODY) ?: "Lorem ipsum dolor sit amet..."

        findViewById<TextView>(R.id.tvTitle).text = title
        findViewById<TextView>(R.id.tvBody).text = body
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
    }
}