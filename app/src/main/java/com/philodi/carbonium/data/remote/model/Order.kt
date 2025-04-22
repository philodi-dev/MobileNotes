package com.philodi.carbonium.data.remote.model

import java.util.Date

/**
 * Order model class
 */
data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<OrderItem> = emptyList(),
    val status: OrderStatus = OrderStatus.PENDING,
    val shippingAddress: Address = Address(),
    val billingAddress: Address = Address(),
    val paymentMethod: String = "",
    val paymentId: String = "",
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val trackingNumber: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    fun getItemCount(): Int = items.sumOf { it.quantity }
}

/**
 * Order item model
 */
data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val total: Double = 0.0
)

/**
 * Order status enum
 */
enum class OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED;
    
    fun getStatusText(): String {
        return when (this) {
            PENDING -> "Pending"
            PROCESSING -> "Processing"
            SHIPPED -> "Shipped"
            OUT_FOR_DELIVERY -> "Out for Delivery"
            DELIVERED -> "Delivered"
            CANCELLED -> "Cancelled"
        }
    }
}

/**
 * Tracking information model
 */
data class TrackingInfo(
    val orderId: String = "",
    val trackingId: String = "",
    val carrier: String = "",
    val estimatedDelivery: Date? = null,
    val trackingEvents: List<TrackingEvent> = emptyList()
)

/**
 * Tracking event model
 */
data class TrackingEvent(
    val status: OrderStatus = OrderStatus.PENDING,
    val location: String = "",
    val description: String = "",
    val timestamp: Date = Date()
)
