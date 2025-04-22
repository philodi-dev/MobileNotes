package com.philodi.carbonium.data.model

import java.math.BigDecimal

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val category: ProductCategory,
    val imageUrl: String,
    val rating: Float,
    val reviewCount: Int,
    val inStock: Boolean,
    val attributes: Map<String, String> = emptyMap()
)

enum class ProductCategory {
    ELECTRONICS,
    CLOTHING,
    HOME,
    BEAUTY,
    FOOD,
    SPORTS,
    BOOKS,
    TOYS,
    OTHER
}