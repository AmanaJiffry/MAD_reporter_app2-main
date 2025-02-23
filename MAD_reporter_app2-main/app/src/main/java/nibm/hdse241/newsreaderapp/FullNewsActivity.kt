package nibm.hdse241.newsreaderapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FullNewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_news)

        // Get the news item data passed from the previous activity
        val newsTitle = intent.getStringExtra("news_title")
        val newsCategory = intent.getStringExtra("news_category")
        val newsArticle = intent.getStringExtra("news_article")

        // Set the title, category, and article content in the TextViews
        val titleTextView: TextView = findViewById(R.id.newsTitle)
        val categoryTextView: TextView = findViewById(R.id.newsCategory)
        val articleTextView: TextView = findViewById(R.id.newsArticle)

        titleTextView.text = newsTitle
        categoryTextView.text = newsCategory
        articleTextView.text = newsArticle
    }
}
