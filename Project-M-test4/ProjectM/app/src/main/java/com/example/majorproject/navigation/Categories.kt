package com.example.majorproject.navigation

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.majorproject.R

class Categories : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_categories)

        // Handling system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // List of category buttons
        val categoryButtons: List<ImageView> = listOf(
            findViewById(R.id.kidsCategory),
            findViewById(R.id.menCategory),
            findViewById(R.id.womenCategory),
            findViewById(R.id.necklaceCategory),
            findViewById(R.id.ringCategory),
            findViewById(R.id.braceletCategory)
        )


        // Setting up click listeners to switch background
        categoryButtons.forEach { button ->
            button.setOnClickListener {
                // Set selected background for clicked button
                button.setBackgroundResource(R.drawable.circular_categories_selected_bg)

                // Revert other buttons to default background
                categoryButtons.filter { it != button }.forEach { otherButton ->
                    otherButton.setBackgroundResource(R.drawable.circular_categories_bg)
                }
            }
        }
    }
}
