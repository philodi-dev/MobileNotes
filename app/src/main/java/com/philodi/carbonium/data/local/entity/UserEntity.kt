package com.philodi.carbonium.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.philodi.carbonium.data.remote.model.Address
import com.philodi.carbonium.data.remote.model.User
import java.util.Date

/**
 * User entity for Room database
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val street: String?,
    val city: String?,
    val state: String?,
    val zipCode: String?,
    val country: String?,
    val profileCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun fromUser(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                name = user.name,
                email = user.email,
                phone = user.phone,
                street = user.address?.street,
                city = user.address?.city,
                state = user.address?.state,
                zipCode = user.address?.zipCode,
                country = user.address?.country,
                profileCompleted = user.profileCompleted,
                createdAt = user.createdAt.time,
                updatedAt = user.updatedAt.time
            )
        }
    }
    
    fun toUser(): User {
        val address = if (street != null && city != null && state != null && zipCode != null && country != null) {
            Address(
                street = street,
                city = city,
                state = state,
                zipCode = zipCode,
                country = country
            )
        } else null
        
        return User(
            id = id,
            name = name,
            email = email,
            phone = phone,
            address = address,
            profileCompleted = profileCompleted,
            createdAt = Date(createdAt),
            updatedAt = Date(updatedAt)
        )
    }
}
