package com.philodi.carbonium.data.model

import java.math.BigDecimal
import java.util.Date

data class Subscription(
    val id: String,
    val userId: String,
    val plan: SubscriptionPlan,
    val status: SubscriptionStatus,
    val startDate: Date,
    val nextBillingDate: Date,
    val paymentMethod: PaymentMethod,
    val autoRenew: Boolean = true,
    val billingAddress: Address
)

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val billingCycle: BillingCycle,
    val features: List<String>
)

enum class BillingCycle {
    MONTHLY,
    QUARTERLY,
    BIANNUALLY,
    ANNUALLY
}

enum class SubscriptionStatus {
    ACTIVE,
    CANCELED,
    EXPIRED,
    PENDING,
    SUSPENDED
}