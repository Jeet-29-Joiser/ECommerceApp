package com.example.ecommerceapp.ui.detail

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.FragmentProductDetailBinding
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.ui.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class ProductDetailFragment : Fragment(R.layout.fragment_product_detail) {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductDetailViewModel by viewModels()

    private var productId: Int = -1
    private lateinit var imageAdapter: ProductImageAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProductDetailBinding.bind(view)

        productId = arguments?.getInt("productId") ?: -1

        binding.rvProductImages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        observeData()

        if (productId != -1) {
            viewModel.fetchProductDetail(productId)
        }

        binding.btnAddToCart.setOnClickListener {
            Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeData() {
        viewModel.productState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                    bindProduct(state.data)
                }

                is UiState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = "Product details not available"
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                }
            }
        }
    }

    private fun bindProduct(product: Product) {
        binding.tvBreadcrumb.text = "Home  >  Shop  >  ${product.category}  >  ${product.title}"
        binding.tvProductTitle.text = product.title
        binding.tvRating.text = "⭐ ${product.rating}/5"
        binding.tvPrice.text = "$${product.price.toInt()}"

        val oldPrice = product.price / (1 - product.discountPercentage / 100)
        binding.tvOldPrice.text = "$${oldPrice.roundToInt()}"
        binding.tvOldPrice.paintFlags =
            binding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        binding.tvDiscount.text = "-${product.discountPercentage.toInt()}%"
        binding.tvDescription.text = product.description

        Glide.with(binding.ivMainProduct.context)
            .load(product.image)
            .fitCenter()
            .into(binding.ivMainProduct)

        imageAdapter = ProductImageAdapter(product.images) { imageUrl ->
            Glide.with(binding.ivMainProduct.context)
                .load(imageUrl)
                .fitCenter()
                .into(binding.ivMainProduct)
        }

        binding.rvProductImages.adapter = imageAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}