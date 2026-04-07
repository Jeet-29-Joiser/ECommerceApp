package com.example.ecommerceapp.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerceapp.databinding.ItemProductThumbnailBinding

class ProductImageAdapter(
    private val images: List<String>,
    private val onImageClick: (String) -> Unit
) : RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder>() {

    private var selectedPosition = 0

    inner class ImageViewHolder(
        private val binding: ItemProductThumbnailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String, isSelected: Boolean) {
            Glide.with(binding.ivThumb.context)
                .load(imageUrl)
                .fitCenter()
                .into(binding.ivThumb)

            binding.root.isSelected = isSelected

            binding.root.setOnClickListener {
                val oldPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(oldPosition)
                notifyItemChanged(selectedPosition)
                onImageClick(imageUrl)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemProductThumbnailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = images.size
}