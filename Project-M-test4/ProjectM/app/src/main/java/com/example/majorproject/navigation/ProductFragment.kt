package com.example.majorproject.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.majorproject.R

class ProductFragment : Fragment() {

    private var someParameter: String? = null

    companion object {
        fun newInstance(param: String): ProductFragment {
            val fragment = ProductFragment()
            val args = Bundle()
            args.putString("param_key", param)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            someParameter = it.getString("param_key")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product, container, false)
    }
}
