package com.example.ecommerceapp.ui.detail

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
class ProductDetailViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _productState = MutableLiveData<UiState<Product>>()
    val productState: LiveData<UiState<Product>> = _productState

    fun fetchProductDetail(productId: Int) {
        _productState.value = UiState.Loading

        viewModelScope.launch {
            _productState.value = repository.getProductDetail(productId)
        }
    }
}