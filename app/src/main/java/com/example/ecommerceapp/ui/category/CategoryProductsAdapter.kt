package com.example.ecommerceapp.ui.category

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerceapp.databinding.ItemCategoryProductBinding
import com.example.ecommerceapp.domain.model.Product
import kotlin.math.roundToInt

class CategoryProductsAdapter(
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<CategoryProductsAdapter.CategoryProductViewHolder>() {

    private val items = mutableListOf<Product>()

    fun submitList(list: List<Product>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class CategoryProductViewHolder(
        private val binding: ItemCategoryProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.title
            binding.tvRating.text = "⭐ ${product.rating}/5"

            val currentPrice = product.price
            val oldPrice = currentPrice / (1 - product.discountPercentage / 100)

            binding.tvPrice.text = "$${currentPrice.toInt()}"
            binding.tvOldPrice.text = "$${oldPrice.roundToInt()}"
            binding.tvOldPrice.paintFlags =
                binding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            binding.tvDiscount.text = "-${product.discountPercentage.toInt()}%"

            Glide.with(binding.ivProduct.context)
                .load(product.image)
                .fitCenter()
                .into(binding.ivProduct)

            binding.root.setOnClickListener {
                onClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryProductViewHolder {
        val binding = ItemCategoryProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryProductViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}