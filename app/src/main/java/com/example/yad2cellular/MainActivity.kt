package com.example.yad2cellular

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController

            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigationView.setupWithNavController(navController)

            // Fix: Ensure clicking "Account" always navigates to MyProfileFragment
            bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.myProfileFragment -> {
                        navController.navigate(R.id.myProfileFragment) // Always go to MyProfileFragment
                        true
                    }
                    R.id.postsFragment -> {
                        navController.navigate(R.id.postsFragment) // Always go to PostsFragment
                        true
                    }
                    else -> {
                        navController.navigate(item.itemId) // Normal behavior
                        true
                    }
                }
            }
        }
    }
}
