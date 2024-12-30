package com.example.majorproject.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.majorproject.fragments.SignUpFragment
import com.example.majorproject.fragments.SignInFragment

class SignInSignUpAdapter(
    fragmentManager: FragmentManager,
    lifecycle: androidx.lifecycle.Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 ->SignInFragment()
            1 -> SignUpFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
