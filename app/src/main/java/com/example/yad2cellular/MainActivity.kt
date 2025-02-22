package com.example.yad2cellular

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yad2cellular.model.Post
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val posts = listOf(
            Post("Laptop", 999.99, "Electronics", "https://example.com/laptop.jpg"),
            Post("Smartphone", 499.99, "Electronics", "https://example.com/smartphone.jpg"),
            Post("iPhone 7", 299.99, "Electronics", "https://example.com/smartphone.jpg"),
            Post("Samsung Galaxy", 9.99, "Electronics", "https://example.com/smartphone.jpg")
        )

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this) // Makes it scrollable
        recyclerView.adapter = PostAdapter(posts)
    }

}
