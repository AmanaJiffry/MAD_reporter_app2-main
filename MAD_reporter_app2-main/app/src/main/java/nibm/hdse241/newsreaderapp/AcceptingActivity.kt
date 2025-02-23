package nibm.hdse241.newsreaderapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AcceptingActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_accepting)

        // Set padding for system bars (edge to edge support)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve the passed data from the intent
        val newsTitle = intent.getStringExtra("news_title") ?: "No Title"
        val newsCategory = intent.getStringExtra("news_category") ?: "No Category"
        val newsArticle = intent.getStringExtra("news_article") ?: "No Article"
        val newsId = intent.getStringExtra("news_id") ?: "" // Assuming a unique ID for each news

        // Display the values in corresponding TextViews
        val tvNewsTitle = findViewById<TextView>(R.id.tvNewsTitle)
        val tvNewsCategory = findViewById<TextView>(R.id.tvNewsCategory)
        val tvNewsArticle = findViewById<TextView>(R.id.tvNewsArticle)

        tvNewsTitle.text = newsTitle
        tvNewsCategory.text = "Category: $newsCategory"
        tvNewsArticle.text = newsArticle

        // Firebase database reference
        val database: DatabaseReference = FirebaseDatabase.getInstance("https://newsreaderapp4-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        // Buttons for accept and reject
        val acceptButton = findViewById<Button>(R.id.acceptButton)
        val rejectButton = findViewById<Button>(R.id.rejectButton)

        acceptButton.setOnClickListener {
            if (newsId.isNotEmpty()) {
                // Update the status to "accepted"
                val newsRef = database.child("posts").child(newsId)
                newsRef.child("status").setValue("accepted")
                    .addOnCompleteListener {
                        // Show a Toast message confirming the acceptance
                        Toast.makeText(this, "News item accepted", Toast.LENGTH_SHORT).show()

                        // Navigate to NewsViewActivity
                        val intent = Intent(this, NewsViewingActivity::class.java)
                        intent.putExtra("news_id", newsId)
                        startActivity(intent)
                        finish() // Close the current activity
                    }
            }
        }

        rejectButton.setOnClickListener {
            if (newsId.isNotEmpty()) {
                // Update the status to "rejected"
                val newsRef = database.child("posts").child(newsId)
                newsRef.child("status").setValue("rejected")
                    .addOnCompleteListener {
                        // Show a Toast message confirming the rejection
                        Toast.makeText(this, "News item rejected", Toast.LENGTH_SHORT).show()

                        // Navigate to NewsViewActivity
                        val intent = Intent(this, FeedBackActivity::class.java)
                        intent.putExtra("news_id", newsId)
                        startActivity(intent)
                        finish() // Close the current activity
                    }
            }
        }
    }
}
