package com.example.majorproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.majorproject.R

class WeightItemAdapter(private val weightMap: Map<String, Any>) :RecyclerView.Adapter<WeightItemAdapter.WeightViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ring_weight, parent, false)
        return WeightViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        val weightKey = weightMap.keys.elementAt(position)
        holder.textView.text = weightMap[weightKey].toString()
        holder.textView.isSelected = selectedPosition == position

        val colorRes = if (holder.textView.isSelected) R.color.offwhite else R.color.black_olive

        holder.textView.setTextColor(ContextCompat.getColor(holder.textView.context, colorRes))

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = if (selectedPosition == position) {
                RecyclerView.NO_POSITION
            } else {
                position
            }
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    override fun getItemCount(): Int {
        return weightMap.size
    }

    inner class WeightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.weightTextView)
    }
}