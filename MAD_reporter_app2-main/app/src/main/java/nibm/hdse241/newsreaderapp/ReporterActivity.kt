package nibm.hdse241.newsreaderapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*


class ReporterActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private val newsList = mutableListOf<NewsItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporter)

        // Initialize Firebase Database reference to "posts"
        database = FirebaseDatabase
            .getInstance("https://newsreaderapp4-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("posts")

        // Initialize RecyclerView
        newsRecyclerView = findViewById(R.id.contentRecyclerView)
        newsRecyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(newsList)
        newsRecyclerView.adapter = newsAdapter

        // Load news from Firebase
        loadNews()

        // Set up filter buttons
        findViewById<Button>(R.id.acceptedButton).setOnClickListener { filterNews("accepted") }
        findViewById<Button>(R.id.rejectedButton).setOnClickListener { filterNews("rejected") }
        findViewById<Button>(R.id.pendingButton).setOnClickListener { filterNews("pending") }

        // Edit Button to upload news
        findViewById<ImageButton>(R.id.editButton).setOnClickListener {
            startActivity(Intent(this, NewsUploadingActivity::class.java))
        }

        // Logout Button
        findViewById<ImageButton>(R.id.logoutButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Global Feedback Button defined in ReporterActivity layout
        val globalFeedbackButton = findViewById<ImageView>(R.id.feedbackbutton)
        globalFeedbackButton.setOnClickListener {
            if (newsList.isNotEmpty()) {
                // For demonstration, we use the first news item.
                // Change this logic if you need a different behavior.
                val news = newsList[0]
                val intent = Intent(this, View_Feedback_Activity::class.java)
                intent.putExtra("NEWS_ID", news.newsId)
                intent.putExtra("NEWS_TITLE", news.heading)
                startActivity(intent)
            } else {
                Toast.makeText(this, "No news available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadNews() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                newsList.clear()
                Log.d("ReporterActivity", "Snapshot size: ${snapshot.childrenCount}")
                for (newsSnapshot in snapshot.children) {
                    val newsId = newsSnapshot.key ?: "No key"
                    Log.d("ReporterActivity", "Snapshot key: $newsId")
                    val newsItem = newsSnapshot.getValue(NewsItem::class.java)
                    if (newsItem != null) {
                        newsItem.newsId = newsId
                        Log.d("ReporterActivity", "Mapped News ID: ${newsItem.newsId}")
                        newsList.add(newsItem)
                    }
                }
                newsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ReporterActivity, "Failed to load news: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterNews(status: String) {
        val filteredList = newsList.filter { it.status == status }
        newsAdapter.updateList(filteredList)
    }
}