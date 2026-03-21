package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.model.Article

class ArticleAdapter(
    private val onClick: (Article) -> Unit,
    private val onEdit: (Article) -> Unit,
    private val onDelete: (Article) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ArticleVH>() {

    private val items = mutableListOf<Article>()

    class ArticleVH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.articleTitle)
        val subtitle: TextView = view.findViewById(R.id.articleSubtitle)
        val author: TextView = view.findViewById(R.id.articleAuthor)
        val category: TextView = view.findViewById(R.id.articleCategory)
        val date: TextView = view.findViewById(R.id.articleDate)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEditArticle)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteArticle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleVH(view)
    }

    override fun onBindViewHolder(holder: ArticleVH, position: Int) {
        val item = items[position]

        holder.title.text = item.title
        holder.subtitle.text = item.subtitle
        holder.author.text = "Author: ${item.author}"
        holder.category.text = item.category
        holder.date.text = item.date

        holder.itemView.setOnClickListener { onClick(item) }
        holder.btnEdit.setOnClickListener { onEdit(item) }
        holder.btnDelete.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Article>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}