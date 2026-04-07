package com.example.ecommerceapp.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.repository.ProductRepository
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _categoryProductsState = MutableLiveData<UiState<List<Product>>>()
    val categoryProductsState: LiveData<UiState<List<Product>>> = _categoryProductsState

    fun fetchProductsByCategory(category: String) {
        _categoryProductsState.value = UiState.Loading

        viewModelScope.launch {
            _categoryProductsState.value = repository.getProductsByCategory(category)
        }
    }
}