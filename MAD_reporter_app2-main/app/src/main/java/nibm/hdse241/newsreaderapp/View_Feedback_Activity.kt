package nibm.hdse241.newsreaderapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import android.widget.Toast
import com.google.firebase.database.DatabaseError
import android.util.Log
class View_Feedback_Activity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var tvNewsTitle: TextView
    private lateinit var tvFeedbackContent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_feedback)

        tvNewsTitle = findViewById(R.id.tvNewsTitle)
        tvFeedbackContent = findViewById(R.id.tvFeedbackContent)

        // Retrieve the news ID and title from the Intent
        val newsId = intent.getStringExtra("NEWS_ID")
        val newsTitle = intent.getStringExtra("NEWS_TITLE")
        tvNewsTitle.text = newsTitle

        if (newsId.isNullOrEmpty()) {
            Toast.makeText(this, "News ID missing", Toast.LENGTH_SHORT).show()
            return
        }

        // Initialize Firebase Database reference to the "feedback" node
        database = FirebaseDatabase
            .getInstance("https://newsreaderapp4-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("feedback")

        // Query the feedback for the specified news ID.
        database.child(newsId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Assume feedback is stored with a field "text"
                    val feedbackText = snapshot.child("text").value?.toString() ?: "No feedback text"
                    tvFeedbackContent.text = feedbackText
                } else {
                    tvFeedbackContent.text = "No feedback available"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ViewFeedbackActivity", "Error fetching feedback: ${error.message}")
                Toast.makeText(this@View_Feedback_Activity, "Error fetching feedback", Toast.LENGTH_SHORT).show()
            }
        })
    }
}