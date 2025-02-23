package nibm.hdse241.newsreaderapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Get a reference to the Firebase Realtime Database
        database = FirebaseDatabase.getInstance("https://newsreaderapp4-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val loginButton = findViewById<MaterialButton>(R.id.loginButton)
        val signupTextView: TextView = findViewById(R.id.signupTextView)

        // Navigate to RegisterActivity when user clicks "Register"
        signupTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        // Sign in user with Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // User is logged in successfully
                    val user = auth.currentUser

                    // Retrieve user details from Firebase Realtime Database
                    user?.uid?.let {
                        database.child("users").child(it).get()
                            .addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    // User data found in the Realtime Database
                                    val name = snapshot.child("name").value.toString()
                                    val userType = snapshot.child("userType").value.toString()

                                    // Show a message with the user's name and type
                                    Toast.makeText(this, "Welcome $name ($userType)", Toast.LENGTH_SHORT).show()

                                    // Navigate to the correct activity based on user type
                                    val intent = when (userType) {
                                        "admin" -> Intent(this, NewsViewingActivity::class.java)
                                        "reporter" -> Intent(this, ReporterActivity::class.java)
                                        else -> {
                                            Toast.makeText(this, "Unknown user type", Toast.LENGTH_SHORT).show()
                                            return@addOnSuccessListener
                                        }
                                    }
                                    startActivity(intent)
                                    finish() // Close the login screen
                                } else {
                                    Toast.makeText(this, "User data not found in the database", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener { exception ->
                                // Handle failure to retrieve data
                                Toast.makeText(this, "Failed to retrieve user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // If login fails
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
