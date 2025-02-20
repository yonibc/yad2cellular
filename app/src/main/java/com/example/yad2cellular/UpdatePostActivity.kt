package com.example.yad2cellular

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UpdatePostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_post)

        // Dropdown values for category and location
        val categories = arrayOf("Cars", "Electronics", "Houses")
        val locations = arrayOf("Tel Aviv", "Jerusalem")

        // Find the Spinners
        val categorySpinner = findViewById<Spinner>(R.id.item_category_spinner_create_post_activity)
        val locationSpinner = findViewById<Spinner>(R.id.item_location_spinner_create_post_activity)

        // Set adapters for both Spinners
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        val locationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locations)

        categorySpinner.adapter = categoryAdapter
        locationSpinner.adapter = locationAdapter
    }
}