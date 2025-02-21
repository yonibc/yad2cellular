package com.example.yad2cellular

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yad2cellular.LoginActivity
import com.example.yad2cellular.R
import com.google.firebase.auth.FirebaseAuth

class MyProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        auth = FirebaseAuth.getInstance()

        val updateDetailsButton: Button = findViewById(R.id.update_details_button)
        val myPostsButton: Button = findViewById(R.id.my_posts_button)
        val logoutButton: Button = findViewById(R.id.logout_button)

        // Navigate to Update Details Activity
        updateDetailsButton.setOnClickListener {
            startActivity(Intent(this, UpdateDetailsActivity::class.java))
        }

        // Navigate to My Posts Activity
        myPostsButton.setOnClickListener {
            //startActivity(Intent(this, MyPostsActivity::class.java))
        }

        // Logout user and return to Login screen
        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
