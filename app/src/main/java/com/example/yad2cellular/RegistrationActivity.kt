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

        val alreadyHaveAccountTextView = findViewById<TextView>(R.id.already_have_an_account_text_view_registration_activity)

        val text = "Already have an account? Login"
        val spannableString = SpannableString(text)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                // Navigate to LoginActivity when "Login" is clicked
                val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        spannableString.setSpan(clickableSpan, text.indexOf("Login"), text.length, 0)
        spannableString.setSpan(ForegroundColorSpan(0xFF0000FF.toInt()), text.indexOf("Login"), text.length, 0)
        alreadyHaveAccountTextView.highlightColor = android.graphics.Color.TRANSPARENT

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
