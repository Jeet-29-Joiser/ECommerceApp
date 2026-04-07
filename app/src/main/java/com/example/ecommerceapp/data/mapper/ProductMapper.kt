package com.example.ecommerceapp.data.mapper

import com.example.ecommerceapp.data.db.ProductEntity
import com.example.ecommerceapp.data.model.ProductDto
import com.example.ecommerceapp.domain.model.Product

fun ProductDto.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        image = thumbnail,
        rating = rating,
        discountPercentage = discountPercentage
    )
}

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        image = thumbnail,
        images = images,
        rating = rating,
        discountPercentage = discountPercentage
    )
}

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        image = image,
        images = listOf(image),
        rating = rating,
        discountPercentage = discountPercentage
    )
}