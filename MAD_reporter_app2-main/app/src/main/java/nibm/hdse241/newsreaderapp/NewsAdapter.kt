package nibm.hdse241.newsreaderapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.util.Log

class NewsAdapter(private var newsList: List<NewsItem>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHeading: TextView = itemView.findViewById(R.id.tvHeading)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvArticle: TextView = itemView.findViewById(R.id.tvArticle)
        // Removed: feedback button is not part of the news item layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.tvHeading.text = news.heading
        holder.tvCategory.text = "Category: ${news.category}"
        holder.tvArticle.text = news.article

        Log.d("NewsAdapter", "News ID: ${news.newsId}")

        // Click listener for the news item – handles navigation based on user type.
        holder.itemView.setOnClickListener {
            Log.d("NewsAdapter", "Clicked News ID: ${news.newsId}")
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val database = FirebaseDatabase
                    .getInstance("https://newsreaderapp4-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .reference
                database.child("users").child(userId).get().addOnSuccessListener { dataSnapshot ->
                    val userType = dataSnapshot.child("userType").value.toString()
                    Log.d("NewsAdapter", "User Type: $userType")
                    if (userType == "reporter") {
                        val intent = Intent(holder.itemView.context, FullNewsActivity::class.java)
                        intent.putExtra("news_title", news.heading)
                        intent.putExtra("news_category", news.category)
                        intent.putExtra("news_article", news.article)
                        holder.itemView.context.startActivity(intent)
                    } else if (userType == "admin") {
                        val intent = Intent(holder.itemView.context, AcceptingActivity::class.java)
                        intent.putExtra("news_title", news.heading)
                        intent.putExtra("news_category", news.category)
                        intent.putExtra("news_article", news.article)
                        intent.putExtra("news_id", news.newsId)
                        holder.itemView.context.startActivity(intent)
                    } else {
                        Log.e("NewsAdapter", "User type is invalid or missing")
                    }
                }
            } else {
                Log.e("NewsAdapter", "User ID is null")
            }
        }
    }

    override fun getItemCount() = newsList.size

    fun updateList(newList: List<NewsItem>) {
        newsList = newList
        notifyDataSetChanged()
        }
}