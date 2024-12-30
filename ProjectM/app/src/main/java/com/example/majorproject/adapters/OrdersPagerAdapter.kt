package com.example.majorproject.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.fragment.app.FragmentActivity
import com.example.majorproject.admin.CompletedFragment
import com.example.majorproject.admin.PendingFragment
import com.example.majorproject.admin.ProcessFragment

class OrdersPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CompletedFragment()
            1 -> ProcessFragment()
            2 -> PendingFragment()
            else -> CompletedFragment()
        }
    }
}
