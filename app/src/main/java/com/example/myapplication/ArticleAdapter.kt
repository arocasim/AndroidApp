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

    class ArticleVH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.articleTitle)
        val subtitle: TextView = view.findViewById(R.id.articleSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ArticleVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_article, parent, false)
        )

    override fun onBindViewHolder(holder: ArticleVH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}