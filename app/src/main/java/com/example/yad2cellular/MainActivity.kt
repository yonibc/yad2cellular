package com.example.yad2cellular

import android.net.wifi.hotspot2.pps.HomeSp
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

    var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment: NavHostFragment? = supportFragmentManager.findFragmentById(R.id.my_nav) as? NavHostFragment

        navController = navHostFragment?.navController

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        navController?.let { NavigationUI.setupWithNavController(bottomNavigationView, it) }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navController?.popBackStack()
                true
            }
            else -> {
                navController?.let { NavigationUI.onNavDestinationSelected(item, it) } ?: false
            }
        }
    }


//    private fun makeCurrentFragment(fragment: Fragment) =
//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.my_nav, fragment)
//            commit()
//        }
}
