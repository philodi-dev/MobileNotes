package com.philodi.carbonium.data.remote.model

import java.util.Date

/**
 * Product model class
 */
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val discountPrice: Double? = null,
    val imageUrl: String = "",
    val category: String = "",
    val stock: Int = 0,
    val rating: Float = 0f,
    val attributes: Map<String, String> = emptyMap(),
    val featured: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    fun isOnSale(): Boolean = discountPrice != null && discountPrice < price
    
    fun getDiscountPercentage(): Int {
        if (discountPrice != null && discountPrice < price) {
            return ((price - discountPrice) / price * 100).toInt()
        }
        return 0
    }
    
    fun getFinalPrice(): Double = discountPrice ?: price
}

/**
 * Product category model
 */
data class Category(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val parentId: String? = null
)

/**
 * Product review model
 */
data class Review(
    val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val date: Date = Date()
)
