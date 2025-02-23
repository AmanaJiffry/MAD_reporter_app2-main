package nibm.hdse241.newsreaderapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class NewsUploadingActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_uploading)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Get a reference to the Firebase Realtime Database
        database = FirebaseDatabase.getInstance("https://newsreaderapp4-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        val headingEditText = findViewById<TextInputEditText>(R.id.headingEditText)
        val articleEditText = findViewById<TextInputEditText>(R.id.articleEditText)
        val categorySelector = findViewById<AutoCompleteTextView>(R.id.categorySelector)
        val submitButton = findViewById<MaterialButton>(R.id.submitButton)
        val clearButton = findViewById<MaterialButton>(R.id.clearButton)

        // List of categories to display in the AutoCompleteTextView
        val categories = listOf("Politics", "Sports", "Entertainment", "Technology", "Health", "Business")

        // Set the adapter to the category selector
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        categorySelector.setAdapter(adapter)

        // Handle the submit button click
        submitButton.setOnClickListener {
            val heading = headingEditText.text.toString().trim()
            val article = articleEditText.text.toString().trim()
            val category = categorySelector.text.toString().trim()

            if (heading.isNotEmpty() && article.isNotEmpty() && category.isNotEmpty()) {
                submitNewsPost(heading, article, category)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle the clear button click
        clearButton.setOnClickListener {
            headingEditText.text?.clear()
            articleEditText.text?.clear()
            categorySelector.text?.clear()
        }
    }

    private fun submitNewsPost(heading: String, article: String, category: String) {
        // Get the current user
        val user = auth.currentUser

        if (user != null) {
            val newsPost = hashMapOf(
                "heading" to heading,
                "article" to article,
                "category" to category,
                "userId" to user.uid,
                "status" to "pending" // Default value for status
            )

            // Push the news post to the Firebase Realtime Database under 'posts'
            database.child("posts").push().setValue(newsPost)
                .addOnSuccessListener {
                    Toast.makeText(this, "News posted successfully", Toast.LENGTH_SHORT).show()
                    // Clear input fields after submission
                    findViewById<TextInputEditText>(R.id.headingEditText).text?.clear()
                    findViewById<TextInputEditText>(R.id.articleEditText).text?.clear()
                    findViewById<AutoCompleteTextView>(R.id.categorySelector).text?.clear()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to post news: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
        }
    }
}
