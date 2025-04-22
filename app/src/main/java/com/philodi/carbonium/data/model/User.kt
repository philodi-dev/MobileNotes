package com.philodi.carbonium.data.model

data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String? = null,
    val addresses: List<Address> = emptyList(),
    val hasActiveSubscription: Boolean = false
)

data class Address(
    val id: String,
    val streetAddress: String,
    val apartment: String? = null,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
    val isDefault: Boolean = false,
    val label: String? = null // e.g., "Home", "Work", etc.
)