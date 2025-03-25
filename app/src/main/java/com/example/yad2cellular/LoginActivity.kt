package com.example.yad2cellular

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.yad2cellular.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        val emailEditText: EditText = findViewById(R.id.email_edit_text_login_activity)
        val passwordEditText: EditText = findViewById(R.id.password_edit_text_login_activity)
        val loginButton: Button = findViewById(R.id.login_button_login_activity)
        val createAccountTextView: TextView = findViewById(R.id.create_account_text_view_login_activity)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            viewModel.login(email, password)
        }

        viewModel.loginSuccess.observe(this) {
            if (it) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        createAccountTextView.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}

