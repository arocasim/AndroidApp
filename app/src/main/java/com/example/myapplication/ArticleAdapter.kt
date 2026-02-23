import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class ArticleAdapter(
    private val items: List<Article>,
    private val onClick: (Article) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ArticleVH>() {

    class ArticleVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.articleImage)
        val title: TextView = itemView.findViewById(R.id.articleTitle)
        val subtitle: TextView = itemView.findViewById(R.id.articleSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleVH(v)
    }

    override fun onBindViewHolder(holder: ArticleVH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size
}