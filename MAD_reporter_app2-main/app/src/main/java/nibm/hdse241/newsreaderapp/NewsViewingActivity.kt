package nibm.hdse241.newsreaderapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class NewsViewingActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private val newsList = mutableListOf<NewsItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_viewing)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance("https://newsreaderapp4-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("posts")

        // Initialize RecyclerView
        newsRecyclerView = findViewById(R.id.newsRecyclerView)
        newsRecyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(newsList)
        newsRecyclerView.adapter = newsAdapter

        // Load all news initially
        loadNews()

        // Set up button listeners
        findViewById<Button>(R.id.btnAllNews).setOnClickListener {
            loadNews()
        }

        findViewById<Button>(R.id.btnAcceptedNews).setOnClickListener {
            loadAcceptedNews()
        }

        // Logout Button
        findViewById<ImageButton>(R.id.btnLogout).setOnClickListener {
            // Handle logout if needed
        }
    }

    private fun loadNews() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                newsList.clear()
                // Log the snapshot size to ensure we're getting data
                Log.d("NewsAdapter", "Snapshot size: ${snapshot.childrenCount}")

                for (newsSnapshot in snapshot.children) {
                    val newsId = newsSnapshot.key ?: "No key"
                    Log.d("NewsAdapter", "Snapshot key: $newsId")  // More specific log here

                    val newsItem = newsSnapshot.getValue(NewsItem::class.java)
                    if (newsItem != null) {
                        newsItem.newsId = newsId
                        Log.d("NewsAdapter", "Mapped News ID: ${newsItem.newsId}")
                        newsList.add(newsItem)
                    }
                }

                newsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@NewsViewingActivity, "Failed to load news: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadAcceptedNews() {
        database.orderByChild("status").equalTo("accepted").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                newsList.clear()
                for (newsSnapshot in snapshot.children) {
                    val newsItem = newsSnapshot.getValue(NewsItem::class.java)
                    if (newsItem != null) {
                        newsList.add(newsItem)
                    }
                }
                newsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@NewsViewingActivity, "Failed to load accepted news: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
