package com.example.ecommerceapp.ui.category

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.FragmentCategoryBinding
import com.example.ecommerceapp.ui.state.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : Fragment(R.layout.fragment_category) {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryViewModel by viewModels()

    private var categorySlug: String = ""
    private var categoryTitle: String = ""

    private lateinit var adapter: CategoryProductsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCategoryBinding.bind(view)

        categorySlug = arguments?.getString("categorySlug") ?: ""
        categoryTitle = arguments?.getString("categoryTitle") ?: ""

        binding.tvCategoryTitle.text = categoryTitle
        binding.tvBreadcrumb.text = "Home  >  $categoryTitle"

        adapter = CategoryProductsAdapter { product ->
            val bundle = Bundle().apply {
                putInt("productId", product.id)
            }
            findNavController().navigate(R.id.productDetailFragment, bundle)
        }

        binding.rvCategoryProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCategoryProducts.adapter = adapter

        observeData()
        viewModel.fetchProductsByCategory(categorySlug)
    }

    private fun observeData() {
        viewModel.categoryProductsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(state.data)
                    binding.tvProductCount.text =
                        "Showing 1-${state.data.size} of ${state.data.size} Products"

                    if (state.isFromCache) {
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text = "Showing offline data"
                    } else {
                        binding.tvError.visibility = View.GONE
                    }
                }

                is UiState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = "No products found in this category"
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}