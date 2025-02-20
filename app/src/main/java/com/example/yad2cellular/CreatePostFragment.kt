package com.example.yad2cellular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment

class CreatePostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        val categories = arrayOf("Cars", "Electronics", "Houses")
        val locations = arrayOf("Tel Aviv", "Jerusalem")

        val categorySpinner = view.findViewById<Spinner>(R.id.item_category_spinner_create_post_activity)
        val locationSpinner = view.findViewById<Spinner>(R.id.item_location_spinner_create_post_activity)

        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        val locationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, locations)

        categorySpinner.adapter = categoryAdapter
        locationSpinner.adapter = locationAdapter

        return view
    }
}
