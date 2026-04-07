package com.example.ecommerceapp.ui.home

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
class HomeViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _homeProductsState = MutableLiveData<UiState<List<Product>>>()
    val homeProductsState: LiveData<UiState<List<Product>>> = _homeProductsState

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    fun fetchHomeProducts() {
        _homeProductsState.value = UiState.Loading
        viewModelScope.launch {
            _homeProductsState.value = repository.getHomeProducts()
        }
    }

    fun fetchHomeCategories() {
        viewModelScope.launch {
            _categories.value = repository.getHomeCategoriesFromProducts()
        }
    }
}