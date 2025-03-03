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
    class ReporterActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_reporter) // Replace with your current activity layout file

            // Find the feedback icon (ImageView)
            val feedbackButton = findViewById<ImageView>(R.id.feedbackbutton)

            // Set a click listener for the feedback icon
            feedbackButton.setOnClickListener {
                // Create an Intent to navigate to the feedback activity
                val intent = Intent(this, View_Feedback_Activity::class.java)

                // Pass data to the feedback activity (optional)
                intent.putExtra("NEWS_TITLE", "Sample News Title") // Replace with actual data
                intent.putExtra("FEEDBACK_CONTENT", "The content needs more verification.") // Replace with actual data

                // Start the feedback activity
                startActivity(intent)
            }
        }
    }

    private lateinit var database: DatabaseReference
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private val newsList = mutableListOf<NewsItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporter)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance("https://newsreaderapp4-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("posts")

        // Initialize RecyclerView
        newsRecyclerView = findViewById(R.id.contentRecyclerView)
        newsRecyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(newsList)
        newsRecyclerView.adapter = newsAdapter

        // Load all news initially
        loadNews()

        // Set up filter buttons
        findViewById<Button>(R.id.acceptedButton).setOnClickListener { filterNews("accepted") }
        findViewById<Button>(R.id.rejectedButton).setOnClickListener { filterNews("rejected") }
        findViewById<Button>(R.id.pendingButton).setOnClickListener { filterNews("pending") }

        // Edit Button to upload news
        findViewById<ImageButton>(R.id.editButton).setOnClickListener {
            val intent = Intent(this, NewsUploadingActivity::class.java)
            startActivity(intent)
        }

        // Edit Button to upload news
        findViewById<ImageButton>(R.id.logoutButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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
                Toast.makeText(this@ReporterActivity, "Failed to load news: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }





    private fun filterNews(status: String) {
        val filteredList = newsList.filter { it.status == status }
        newsAdapter.updateList(filteredList)
    }
}

