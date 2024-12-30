package com.example.majorproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.majorproject.R
import com.example.majorproject.dataClass.ProductSpecification

class ProductSpecificationAdapter(private val specList: List<ProductSpecification>) :
    RecyclerView.Adapter<ProductSpecificationAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val labelText: TextView = itemView.findViewById(R.id.label_text)
        private val valueText: TextView = itemView.findViewById(R.id.value_text)

        fun bind(spec: ProductSpecification) {
            labelText.text = spec.label
            valueText.text = spec.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_specification_item, parent, false) // Ensure item_card is the correct layout
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spec = specList[position]
        holder.bind(spec)
    }

    override fun getItemCount(): Int = specList.size
}
