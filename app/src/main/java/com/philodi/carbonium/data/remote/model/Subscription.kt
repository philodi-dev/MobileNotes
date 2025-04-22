package com.philodi.carbonium.data.remote.model

import java.util.Date

/**
 * Subscription model class
 */
data class Subscription(
    val id: String = "",
    val userId: String = "",
    val serviceId: String = "",
    val serviceName: String = "",
    val planId: String = "",
    val planName: String = "",
    val price: Double = 0.0,
    val billingCycle: BillingCycle = BillingCycle.MONTHLY,
    val status: SubscriptionStatus = SubscriptionStatus.ACTIVE,
    val startDate: Date = Date(),
    val endDate: Date? = null,
    val nextBillingDate: Date? = null,
    val autoRenew: Boolean = true,
    val paymentMethod: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    fun isActive(): Boolean = status == SubscriptionStatus.ACTIVE
    
    fun daysUntilRenewal(): Int {
        nextBillingDate?.let {
            val today = Date()
            val diffInMillis = it.time - today.time
            return (diffInMillis / (1000L * 60 * 60 * 24)).toInt()
        }
        return 0
    }
}

/**
 * Subscription status enum
 */
enum class SubscriptionStatus {
    ACTIVE,
    CANCELED,
    EXPIRED,
    PENDING,
    FAILED;
    
    fun getStatusText(): String {
        return when (this) {
            ACTIVE -> "Active"
            CANCELED -> "Canceled"
            EXPIRED -> "Expired"
            PENDING -> "Pending"
            FAILED -> "Failed"
        }
    }
}

/**
 * Billing cycle enum
 */
enum class BillingCycle {
    MONTHLY,
    QUARTERLY,
    YEARLY;
    
    fun getDisplayText(): String {
        return when (this) {
            MONTHLY -> "Monthly"
            QUARTERLY -> "Quarterly"
            YEARLY -> "Yearly"
        }
    }
    
    fun getDurationInMonths(): Int {
        return when (this) {
            MONTHLY -> 1
            QUARTERLY -> 3
            YEARLY -> 12
        }
    }
}
