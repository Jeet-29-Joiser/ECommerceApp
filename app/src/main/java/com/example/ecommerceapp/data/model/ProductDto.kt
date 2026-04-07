package com.example.ecommerceapp.data.model

data class ProductDto(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val rating: Double,
    val thumbnail: String,
    val images: List<String>,
    val discountPercentage: Double
)