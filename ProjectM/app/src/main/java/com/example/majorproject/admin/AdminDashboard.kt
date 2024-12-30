package com.example.majorproject.admin

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.majorproject.R

class AdminDashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


//        findViewById<ImageView>(R.id.admin_appointment).setOnClickListener {
//
//            val intent = Intent(this, AdminAppointmentsActivity::class.java)
//            startActivity(intent)
//        }
//
//           findViewById<CardView>(R.id.admin_track_status).setOnClickListener {
//
//            val intent = Intent(this, EditOrderStatusActivity::class.java)
//            startActivity(intent)
//        }

        findViewById<CardView>(R.id.cardOrders).setOnClickListener {

            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.cardAddProducts).setOnClickListener {

            val intent = Intent(this, AddProducts::class.java)
            startActivity(intent)
        }
    }
    }
