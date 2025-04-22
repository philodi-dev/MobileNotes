package com.philodi.carbonium.data.remote.model

import java.util.Date

/**
 * User model class
 */
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: Address? = null,
    val profileCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

/**
 * Address model
 */
data class Address(
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = ""
) {
    fun getFullAddress(): String {
        return "$street, $city, $state $zipCode, $country"
    }
}

/**
 * Child profile model
 */
data class Child(
    val id: String = "",
    val name: String = "",
    val dateOfBirth: Date? = null,
    val gender: String = "",
    val parentId: String = ""
) {
    fun getAge(): Int {
        dateOfBirth?.let {
            val today = Date()
            val diffInMillis = today.time - it.time
            return (diffInMillis / (1000L * 60 * 60 * 24 * 365.25)).toInt()
        }
        return 0
    }
}
