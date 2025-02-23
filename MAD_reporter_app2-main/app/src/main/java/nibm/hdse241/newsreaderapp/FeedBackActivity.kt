package nibm.hdse241.newsreaderapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.*

class FeedBackActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var headlineText: TextView
    private lateinit var feedbackInput: EditText
    private lateinit var submitButton: Button
    private var newsId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_feedback)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance("https://newsreaderapp4-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        // Retrieve news ID from intent
        newsId = intent.getStringExtra("news_id") ?: ""

        // Initialize UI components
        headlineText = findViewById(R.id.headlineText)
        feedbackInput = findViewById(R.id.feedbackInput)
        submitButton = findViewById(R.id.submitButton)

        // Fetch and display the news headline
        if (newsId.isNotEmpty()) {
            fetchNewsHeading(newsId)
        } else {
            Toast.makeText(this, "Invalid news ID", Toast.LENGTH_SHORT).show()
        }

        // Handle submit button click
        submitButton.setOnClickListener {
            submitFeedback()
        }
    }

    private fun fetchNewsHeading(newsId: String) {
        val newsRef = database.child("posts").child(newsId).child("heading")

        newsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val title = snapshot.getValue(String::class.java)
                if (title != null) {
                    headlineText.text = title
                } else {
                    headlineText.text = "Title not found"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FeedBackActivity, "Error fetching title", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun submitFeedback() {
        val feedbackText = feedbackInput.text.toString().trim()

        if (feedbackText.isEmpty()) {
            Toast.makeText(this, "Please enter feedback", Toast.LENGTH_SHORT).show()
            return
        }

        // Store feedback under "feedback/{newsId}"
        val feedbackRef = database.child("feedback").child(newsId)

        val feedbackData = HashMap<String, Any>()
        feedbackData["feedbackText"] = feedbackText
        feedbackData["timestamp"] = System.currentTimeMillis()

        feedbackRef.setValue(feedbackData)
            .addOnSuccessListener {
                Toast.makeText(this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, NewsViewingActivity::class.java)
                finish() // Close activity after submission
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to submit feedback", Toast.LENGTH_SHORT).show()
            }
    }
}
