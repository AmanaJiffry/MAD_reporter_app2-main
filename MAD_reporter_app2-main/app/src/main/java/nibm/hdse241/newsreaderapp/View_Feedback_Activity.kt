package nibm.hdse241.newsreaderapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class View_Feedback_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_feedback) // Use your feedback page layout file

        // Retrieve the passed data from the Intent
        val newsTitle = intent.getStringExtra("NEWS_TITLE")
        val feedbackContent = intent.getStringExtra("FEEDBACK_CONTENT")

        // Find the TextViews in the layout
        val tvNewsTitle = findViewById<TextView>(R.id.tvNewsTitle)
        val tvFeedbackContent = findViewById<TextView>(R.id.tvFeedbackContent)

        // Set the retrieved data to the TextViews
        tvNewsTitle.text = newsTitle
        tvFeedbackContent.text = feedbackContent
    }
}