package com.example.majorproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.majorproject.R

class ImageSliderAdapter(private var images: MutableList<Int>) : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    fun getImageAtPosition(position: Int): Int {
        return if (position in images.indices) {
            images[position]
        } else {
            R.drawable.model_img_1 // Default image in case of an invalid position
        }
    }

    // Method to update the entire list of images
    fun updateImages(newImages: List<Int>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }
    fun addImages(newImages: List<Int>) {
        val startIndex = images.size
        images.addAll(newImages)
        notifyItemRangeInserted(startIndex, newImages.size)
    }
}

