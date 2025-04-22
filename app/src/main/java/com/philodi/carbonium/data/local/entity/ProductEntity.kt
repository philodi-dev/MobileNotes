package com.philodi.carbonium.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.philodi.carbonium.data.remote.model.Product
import java.util.Date

/**
 * Product entity for Room database
 */
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val discountPrice: Double?,
    val imageUrl: String,
    val category: String,
    val stock: Int,
    val rating: Float,
    val attributes: String, // JSON string of attributes
    val featured: Boolean,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun fromProduct(product: Product): ProductEntity {
            return ProductEntity(
                id = product.id,
                name = product.name,
                description = product.description,
                price = product.price,
                discountPrice = product.discountPrice,
                imageUrl = product.imageUrl,
                category = product.category,
                stock = product.stock,
                rating = product.rating,
                attributes = product.attributes.toString(), // Simple conversion - would need proper JSON serialization
                featured = product.featured,
                createdAt = product.createdAt.time,
                updatedAt = product.updatedAt.time
            )
        }
    }
    
    fun toProduct(): Product {
        return Product(
            id = id,
            name = name,
            description = description,
            price = price,
            discountPrice = discountPrice,
            imageUrl = imageUrl,
            category = category,
            stock = stock,
            rating = rating,
            attributes = emptyMap(), // Simple conversion - would need proper JSON deserialization
            featured = featured,
            createdAt = Date(createdAt),
            updatedAt = Date(updatedAt)
        )
    }
}
