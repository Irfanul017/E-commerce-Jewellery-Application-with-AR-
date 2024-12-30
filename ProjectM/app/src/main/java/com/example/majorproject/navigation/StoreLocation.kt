package com.example.majorproject.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.majorproject.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class StoreLocation : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_store_loaction, container, false)

        // Use childFragmentManager to get the SupportMapFragment inside a Fragment
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker for Mahavir Gems in BKC, Mumbai
        val bkcMumbai = LatLng(19.0700, 72.8700)
        mMap.addMarker(MarkerOptions().position(bkcMumbai).title("Mahavir Gems"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bkcMumbai, 15f))
    }

}
