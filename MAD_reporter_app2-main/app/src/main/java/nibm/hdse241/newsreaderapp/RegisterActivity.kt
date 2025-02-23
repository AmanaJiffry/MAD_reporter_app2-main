package nibm.hdse241.newsreaderapp

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Get a reference to the Firebase Realtime Database
        database = FirebaseDatabase.getInstance("https://newsreaderapp4-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        val nameEditText = findViewById<TextInputEditText>(R.id.nameEditText)
        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val signupButton = findViewById<MaterialButton>(R.id.signupButton)

        signupButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validate input fields
            if (validateFields(name, email, password)) {
                registerUser(name, email, password, nameEditText, emailEditText, passwordEditText)
            }
        }
    }

    private fun validateFields(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(email) -> {
                Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun registerUser(name: String, email: String, password: String,
                             nameEditText: TextInputEditText, emailEditText: TextInputEditText,
                             passwordEditText: TextInputEditText) {

        // Create a new user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // User is registered successfully
                    val user = auth.currentUser

                    // Store additional user details in Firebase Realtime Database
                    val userDetails = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "userType" to "admin"  // Default user type is 'reporter'
                    )

                    user?.uid?.let {
                        // Push user data to the Firebase Realtime Database under 'users/{uid}'
                        database.child("users").child(it).setValue(userDetails)
                            .addOnSuccessListener {
                                // Successfully stored user details
                                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                                // Clear all fields after successful registration
                                nameEditText.text?.clear()
                                emailEditText.text?.clear()
                                passwordEditText.text?.clear()
                            }
                            .addOnFailureListener { exception ->
                                // Handle failure in storing data
                                Toast.makeText(this, "Failed to store user details: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // If registration fails
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
