package nibm.hdse241.newsreaderapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import nibm.hdse241.newsreaderapp.AcceptingActivity
import nibm.hdse241.newsreaderapp.FullNewsActivity
import nibm.hdse241.newsreaderapp.NewsItem
import nibm.hdse241.newsreaderapp.R

class NewsAdapter(private var newsList: List<NewsItem>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHeading: TextView = itemView.findViewById(R.id.tvHeading)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvArticle: TextView = itemView.findViewById(R.id.tvArticle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.tvHeading.text = news.heading
        holder.tvCategory.text = "Category: ${news.category}"

        // Log the newsId for debugging purposes
        Log.d("NewsAdapter", "News ID: ${news.newsId}")

        // Set up the click listener for the news item
        holder.itemView.setOnClickListener {
            // Log the newsId when the item is clicked
            Log.d("NewsAdapter", "Clicked News ID: ${news.newsId}")

            // Get the current user's ID (assuming Firebase Authentication is used)
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId != null) {
                // Use the specified Firebase Database URL
                val database: DatabaseReference = FirebaseDatabase
                    .getInstance("https://newsreaderapp4-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .reference
                val userRef = database.child("users").child(userId)

                userRef.get().addOnSuccessListener { dataSnapshot ->
                    val userType = dataSnapshot.child("userType").value.toString() // "reporter" or "admin"

                    Log.d("NewsAdapter", "User Type: $userType")  // Log the user type

                    // Check user type and navigate accordingly
                    if (userType == "reporter") {
                        // Pass the data of the selected news item to FullNewsActivity for reporters
                        val intent = Intent(holder.itemView.context, FullNewsActivity::class.java)
                        intent.putExtra("news_title", news.heading)
                        intent.putExtra("news_category", news.category)
                        intent.putExtra("news_article", news.article)
                        holder.itemView.context.startActivity(intent)
                    } else if (userType == "admin") {
                        // Pass the data to AcceptingActivity for admins
                        val intent = Intent(holder.itemView.context, AcceptingActivity::class.java)
                        intent.putExtra("news_title", news.heading)
                        intent.putExtra("news_category", news.category)
                        intent.putExtra("news_article", news.article)
                        intent.putExtra("news_id", news.newsId)  // Pass the newsId
                        holder.itemView.context.startActivity(intent)
                    } else {
                        Log.e("NewsAdapter", "User type is invalid or missing")
                    }
                }.addOnFailureListener { exception ->
                    Log.e("NewsAdapter", "Error fetching user data: ${exception.message}")
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
