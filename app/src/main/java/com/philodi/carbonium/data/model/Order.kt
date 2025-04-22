package com.philodi.carbonium.data.model

import java.math.BigDecimal
import java.util.Date

data class Order(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val shippingAddress: Address,
    val billingAddress: Address,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus,
    val subtotal: BigDecimal,
    val tax: BigDecimal,
    val shipping: BigDecimal,
    val discount: BigDecimal,
    val total: BigDecimal,
    val createdAt: Date,
    val updatedAt: Date,
    val estimatedDelivery: Date? = null,
    val trackingNumber: String? = null
)

enum class OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}

data class PaymentMethod(
    val id: String,
    val type: PaymentType,
    val lastFourDigits: String? = null,
    val expiryDate: String? = null,
    val cardholderName: String? = null,
    val isDefault: Boolean = false
)

enum class PaymentType {
    CREDIT_CARD,
    DEBIT_CARD,
    PAYPAL,
    BANK_TRANSFER,
    CASH_ON_DELIVERY
}