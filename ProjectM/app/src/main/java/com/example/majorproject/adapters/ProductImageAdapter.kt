package com.example.majorproject.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.majorproject.R
import com.squareup.picasso.Picasso

class ProductImageAdapter(private var images: List<String?>) : RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        // Use Glide to load the image from the URL
        Log.d("ProductDescriptions", "Image URLs: ${images.get(
            position
        )}")
       Picasso.get().load(images[position]).into(holder.imageView)
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    // Method to update the entire list of images
    fun updateImages(newImages: List<String>) {
        images = newImages
        notifyDataSetChanged()
    }
}
