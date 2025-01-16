package com.example.yad2cellular

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)

        // Get the TextView for "Already have an account?"
        val alreadyHaveAccountTextView = findViewById<TextView>(R.id.already_have_an_account_text_view_registration_activity)

        // Create the SpannableString
        val text = "Already have an account? Login"
        val spannableString = SpannableString(text)

        // Make "Login" clickable
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                // Navigate to LoginActivity when "Login" is clicked
                val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        // Apply the clickable span to "Login"
        spannableString.setSpan(clickableSpan, text.indexOf("Login"), text.length, 0)

        // Keep the color as #0000FF (blue) for "Login" before and after clicking
        spannableString.setSpan(ForegroundColorSpan(0xFF0000FF.toInt()), text.indexOf("Login"), text.length, 0)

        // Remove the clickable span's default background (no highlight or border around the word)
        alreadyHaveAccountTextView.setHighlightColor(android.graphics.Color.TRANSPARENT)

        // Set the SpannableString to the TextView
        alreadyHaveAccountTextView.text = spannableString
        alreadyHaveAccountTextView.movementMethod = android.text.method.LinkMovementMethod.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
