package com.philodi.carbonium.data.remote.model

import java.util.Date

/**
 * Service model class
 */
data class Service(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val plans: List<Plan> = emptyList(),
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

/**
 * Pricing plan model
 */
data class Plan(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val monthlyPrice: Double = 0.0,
    val quarterlyPrice: Double = 0.0,
    val yearlyPrice: Double = 0.0,
    val features: List<String> = emptyList(),
    val isPopular: Boolean = false,
    val isRecommended: Boolean = false
) {
    fun getPriceForCycle(cycle: BillingCycle): Double {
        return when (cycle) {
            BillingCycle.MONTHLY -> monthlyPrice
            BillingCycle.QUARTERLY -> quarterlyPrice
            BillingCycle.YEARLY -> yearlyPrice
        }
    }
}
