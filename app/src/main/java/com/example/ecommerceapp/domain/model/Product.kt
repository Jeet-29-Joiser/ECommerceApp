package com.example.ecommerceapp.domain.model

data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val images: List<String>,
    val rating: Double,
    val discountPercentage: Double
)