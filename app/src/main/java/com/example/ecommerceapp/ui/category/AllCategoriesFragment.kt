package com.example.ecommerceapp.ui.category

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.FragmentAllCategoriesBinding
import com.example.ecommerceapp.ui.home.CategoryAdapter
import com.example.ecommerceapp.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllCategoriesFragment : Fragment(R.layout.fragment_all_categories) {

    private var _binding: FragmentAllCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var adapter: CategoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllCategoriesBinding.bind(view)

        adapter = CategoryAdapter(emptyList()) { category ->
            val bundle = Bundle().apply {
                putString("categorySlug", category.apiSlug)
                putString("categoryTitle", category.title)
            }
            findNavController().navigate(R.id.categoryFragment, bundle)
        }

        binding.rvAllCategories.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvAllCategories.adapter = adapter
        binding.rvAllCategories.setHasFixedSize(true)

        if (binding.rvAllCategories.itemDecorationCount == 0) {
            binding.rvAllCategories.addItemDecoration(
                GridSpacingItemDecoration(2, 16, true)
            )
        }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.submitList(categories)
        }

        if (viewModel.categories.value.isNullOrEmpty()) {
            viewModel.fetchHomeCategories()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}