package com.philodi.carbonium.data.model

import java.math.BigDecimal

data class Cart(
    val id: String,
    val items: List<CartItem> = emptyList(),
    val subtotal: BigDecimal = BigDecimal.ZERO,
    val tax: BigDecimal = BigDecimal.ZERO,
    val shipping: BigDecimal = BigDecimal.ZERO,
    val discount: BigDecimal = BigDecimal.ZERO,
    val total: BigDecimal = BigDecimal.ZERO
) {
    val itemCount: Int get() = items.sumOf { it.quantity }
}

data class CartItem(
    val id: String,
    val productId: String,
    val name: String,
    val imageUrl: String,
    val price: BigDecimal,
    val quantity: Int,
    val attributes: Map<String, String> = emptyMap()
) {
    val subtotal: BigDecimal get() = price.multiply(BigDecimal(quantity))
}