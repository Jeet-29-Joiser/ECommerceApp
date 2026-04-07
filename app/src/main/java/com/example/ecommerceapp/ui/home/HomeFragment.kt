package com.example.ecommerceapp.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.FragmentHomeBinding
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.ui.state.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var newArrivalAdapter: ProductAdapter
    private lateinit var topSellingAdapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        setupRecyclerViews()
        observeProducts()
        observeCategories()

        binding.btnRetry.setOnClickListener {
            viewModel.fetchHomeProducts()
            viewModel.fetchHomeCategories()
        }

        viewModel.fetchHomeProducts()
        viewModel.fetchHomeCategories()
    }

    private fun setupRecyclerViews() {
        newArrivalAdapter = ProductAdapter { product ->
            onProductClicked(product)
        }

        topSellingAdapter = ProductAdapter { product ->
            onProductClicked(product)
        }

        binding.rvNewArrivals.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = newArrivalAdapter
        }

        binding.rvTopSelling.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = topSellingAdapter
        }

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeProducts() {
        viewModel.homeProductsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE

                    if (state.isFromCache) {
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text = "Showing offline data"
                    } else {
                        binding.tvError.visibility = View.GONE
                    }

                    setHomeSections(state.data)
                }

                is UiState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.layoutError.visibility = View.VISIBLE
                    binding.tvError.text = "No products available"
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                }
            }
        }
    }

    private fun observeCategories() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val previewCategories = categories.take(8)

            categoryAdapter = CategoryAdapter(previewCategories) { category ->
                val bundle = Bundle().apply {
                    putString("categorySlug", category.apiSlug)
                    putString("categoryTitle", category.title)
                }
                findNavController().navigate(R.id.categoryFragment, bundle)
            }

            binding.rvCategories.adapter = categoryAdapter
        }

        binding.tvViewAllCategories.setOnClickListener {
            findNavController().navigate(R.id.allCategoriesFragment)
        }
    }

    private fun setHomeSections(products: List<Product>) {
        if (products.isEmpty()) {
            binding.layoutError.visibility = View.VISIBLE
            binding.tvError.text = "No products found"
            return
        }

        binding.layoutError.visibility = View.GONE

        val newArrivals = products.take(5)
        val topSelling = products.sortedByDescending { it.rating }.take(5)

        newArrivalAdapter.submitList(newArrivals)
        topSellingAdapter.submitList(topSelling)
    }

    private fun onProductClicked(product: Product) {
        val bundle = Bundle().apply {
            putInt("productId", product.id)
        }
        findNavController().navigate(R.id.productDetailFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}