# Carbonium - Data Models Implementation

This document provides detailed implementation of the data models used in the Carbonium app.

## Product Models

### Product.kt

```kotlin
package com.philodi.carbonium.data.model

import java.math.BigDecimal

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val category: ProductCategory,
    val imageUrl: String,
    val attributes: Map<String, String> = emptyMap(),
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val inStock: Boolean = true
)

enum class ProductCategory {
    ELECTRONICS,
    CLOTHING,
    HOME_AND_GARDEN,
    BEAUTY,
    SPORTS,
    BOOKS,
    FOOD_AND_BEVERAGES,
    TOYS
}
```

## User Models

### User.kt

```kotlin
package com.philodi.carbonium.data.model

data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String? = null,
    val addresses: List<Address> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList()
)

data class Address(
    val id: String,
    val type: AddressType,
    val fullName: String,
    val streetAddress: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String,
    val isDefault: Boolean = false
)

enum class AddressType {
    SHIPPING,
    BILLING,
    BOTH
}

data class PaymentMethod(
    val id: String,
    val type: PaymentType,
    val cardNumber: String? = null,
    val cardHolder: String? = null,
    val expiryMonth: Int? = null,
    val expiryYear: Int? = null,
    val isDefault: Boolean = false
)

enum class PaymentType {
    CREDIT_CARD,
    DEBIT_CARD,
    PAYPAL,
    BANK_TRANSFER,
    APPLE_PAY,
    GOOGLE_PAY
}
```

## Cart Models

### Cart.kt

```kotlin
package com.philodi.carbonium.data.model

import java.math.BigDecimal

data class Cart(
    val id: String,
    val items: List<CartItem>,
    val subtotal: BigDecimal,
    val tax: BigDecimal,
    val shipping: BigDecimal,
    val discount: BigDecimal,
    val total: BigDecimal
)

data class CartItem(
    val id: String,
    val productId: String,
    val name: String,
    val imageUrl: String,
    val price: BigDecimal,
    val quantity: Int,
    val attributes: Map<String, String> = emptyMap()
)
```

## Order Models

### Order.kt

```kotlin
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
```

## Subscription Models

### Subscription.kt

```kotlin
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
    val features: List<String>,
    val isPopular: Boolean = false
)

enum class SubscriptionStatus {
    ACTIVE,
    CANCELED,
    EXPIRED,
    TRIAL,
    PAST_DUE
}

enum class BillingCycle {
    MONTHLY,
    QUARTERLY,
    BIANNUALLY,
    ANNUALLY
}
```

## Data Repository for Faker

### FakeDataRepository.kt

```kotlin
package com.philodi.carbonium.data.repository

import com.github.javafaker.Faker
import com.philodi.carbonium.data.model.*
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FakeDataRepository @Inject constructor() {
    private val faker = Faker()
    
    fun generateUser(): User {
        val firstName = faker.name().firstName()
        val lastName = faker.name().lastName()
        val email = "${firstName.lowercase()}.${lastName.lowercase()}@${faker.internet().domainName()}"
        
        val addressCount = Random.nextInt(1, 3)
        val addresses = (1..addressCount).map { generateRandomAddress() }
        
        val paymentMethodCount = Random.nextInt(1, 3)
        val paymentMethods = (1..paymentMethodCount).map { generateRandomPaymentMethod() }
        
        return User(
            id = UUID.randomUUID().toString(),
            email = email,
            firstName = firstName,
            lastName = lastName,
            phone = faker.phoneNumber().phoneNumber(),
            addresses = addresses,
            paymentMethods = paymentMethods
        )
    }
    
    fun generateProducts(count: Int = 20): List<Product> {
        return (1..count).map { generateProduct() }
    }
    
    fun generateProduct(): Product {
        val category = ProductCategory.values().random()
        val price = BigDecimal(String.format("%.2f", faker.number().randomDouble(2, 5, 1000)))
        
        val attributes = mutableMapOf<String, String>()
        val attributeCount = Random.nextInt(0, 5)
        
        repeat(attributeCount) {
            attributes[faker.commerce().material()] = faker.commerce().productName()
        }
        
        return Product(
            id = UUID.randomUUID().toString(),
            name = faker.commerce().productName(),
            description = faker.lorem().paragraph(Random.nextInt(2, 6)),
            price = price,
            category = category,
            imageUrl = "https://picsum.photos/seed/${Random.nextInt(1, 1000)}/500/500",
            attributes = attributes,
            rating = Random.nextFloat() * 4 + 1, // 1.0 to 5.0
            reviewCount = Random.nextInt(0, 500),
            inStock = Random.nextBoolean()
        )
    }
    
    fun generateCart(): Cart {
        val itemCount = Random.nextInt(1, 6)
        val items = (1..itemCount).map { generateCartItem() }
        
        val subtotal = items.fold(BigDecimal.ZERO) { acc, item -> 
            acc.add(item.price.multiply(BigDecimal(item.quantity))) 
        }
        
        val tax = subtotal.multiply(BigDecimal.valueOf(0.08))
        val shipping = if (subtotal > BigDecimal.valueOf(100)) 
            BigDecimal.ZERO else BigDecimal.valueOf(12.99)
        val discount = if (Random.nextBoolean()) 
            subtotal.multiply(BigDecimal.valueOf(0.1)) else BigDecimal.ZERO
        val total = subtotal.add(tax).add(shipping).subtract(discount)
        
        return Cart(
            id = UUID.randomUUID().toString(),
            items = items,
            subtotal = subtotal,
            tax = tax,
            shipping = shipping,
            discount = discount,
            total = total
        )
    }
    
    private fun generateCartItem(): CartItem {
        val product = generateProduct()
        val quantity = Random.nextInt(1, 5)
        
        return CartItem(
            id = UUID.randomUUID().toString(),
            productId = product.id,
            name = product.name,
            imageUrl = product.imageUrl,
            price = product.price,
            quantity = quantity,
            attributes = product.attributes
        )
    }
    
    fun generateOrder(): Order {
        val cart = generateCart()
        val user = generateUser()
        val shippingAddress = if (user.addresses.isNotEmpty()) 
            user.addresses.random() else generateRandomAddress()
        val billingAddress = if (Random.nextBoolean() && user.addresses.isNotEmpty()) 
            user.addresses.random() else generateRandomAddress()
        val paymentMethod = if (user.paymentMethods.isNotEmpty()) 
            user.paymentMethods.random() else generateRandomPaymentMethod()
        
        val createdAt = Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(Random.nextLong(0, 30)))
        val updatedAt = Date(createdAt.time + TimeUnit.HOURS.toMillis(Random.nextLong(1, 48)))
        
        val status = OrderStatus.values().random()
        val estimatedDelivery = if (status == OrderStatus.PENDING || status == OrderStatus.PROCESSING) 
            Date(createdAt.time + TimeUnit.DAYS.toMillis(Random.nextLong(3, 10))) else null
        val trackingNumber = if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) 
            faker.code().isbn13() else null
        
        return Order(
            id = UUID.randomUUID().toString(),
            userId = user.id,
            items = cart.items,
            shippingAddress = shippingAddress,
            billingAddress = billingAddress,
            paymentMethod = paymentMethod,
            status = status,
            subtotal = cart.subtotal,
            tax = cart.tax,
            shipping = cart.shipping,
            discount = cart.discount,
            total = cart.total,
            createdAt = createdAt,
            updatedAt = updatedAt,
            estimatedDelivery = estimatedDelivery,
            trackingNumber = trackingNumber
        )
    }
    
    fun generateSubscriptionPlan(): SubscriptionPlan {
        val billingCycle = BillingCycle.values().random()
        
        val basePrice = Random.nextInt(5, 50)
        val price = when (billingCycle) {
            BillingCycle.MONTHLY -> BigDecimal.valueOf(basePrice.toLong())
            BillingCycle.QUARTERLY -> BigDecimal.valueOf((basePrice * 3 * 0.9).toLong())  // 10% discount
            BillingCycle.BIANNUALLY -> BigDecimal.valueOf((basePrice * 6 * 0.85).toLong()) // 15% discount
            BillingCycle.ANNUALLY -> BigDecimal.valueOf((basePrice * 12 * 0.8).toLong())  // 20% discount
        }
        
        val featureCount = Random.nextInt(3, 8)
        val features = (1..featureCount).map { 
            faker.lorem().words(Random.nextInt(2, 5)).joinToString(" ") 
        }
        
        return SubscriptionPlan(
            id = UUID.randomUUID().toString(),
            name = "${faker.commerce().productName()} Plan",
            description = faker.lorem().paragraph(Random.nextInt(1, 3)),
            price = price,
            billingCycle = billingCycle,
            features = features,
            isPopular = Random.nextBoolean()
        )
    }
    
    fun generateSubscription(): Subscription {
        val user = generateUser()
        val plan = generateSubscriptionPlan()
        val paymentMethod = if (user.paymentMethods.isNotEmpty()) 
            user.paymentMethods.random() else generateRandomPaymentMethod()
        val billingAddress = if (user.addresses.isNotEmpty()) 
            user.addresses.random() else generateRandomAddress()
        
        val startDate = Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(Random.nextLong(1, 180)))
        
        val daysToAdd = when (plan.billingCycle) {
            BillingCycle.MONTHLY -> 30L
            BillingCycle.QUARTERLY -> 90L
            BillingCycle.BIANNUALLY -> 180L
            BillingCycle.ANNUALLY -> 365L
        }
        
        val nextBillingDate = Date(startDate.time + TimeUnit.DAYS.toMillis(daysToAdd))
        
        val status = if (nextBillingDate.after(Date())) {
            SubscriptionStatus.values().random()
        } else {
            SubscriptionStatus.EXPIRED
        }
        
        return Subscription(
            id = UUID.randomUUID().toString(),
            userId = user.id,
            plan = plan,
            status = status,
            startDate = startDate,
            nextBillingDate = nextBillingDate,
            paymentMethod = paymentMethod,
            autoRenew = Random.nextBoolean(),
            billingAddress = billingAddress
        )
    }
    
    private fun generateRandomAddress(): Address {
        val type = AddressType.values().random()
        
        return Address(
            id = UUID.randomUUID().toString(),
            type = type,
            fullName = faker.name().fullName(),
            streetAddress = faker.address().streetAddress(),
            city = faker.address().city(),
            state = faker.address().state(),
            postalCode = faker.address().zipCode(),
            country = faker.address().country(),
            isDefault = Random.nextBoolean()
        )
    }
    
    private fun generateRandomPaymentMethod(): PaymentMethod {
        val type = PaymentType.values().random()
        
        return when (type) {
            PaymentType.CREDIT_CARD, PaymentType.DEBIT_CARD -> {
                PaymentMethod(
                    id = UUID.randomUUID().toString(),
                    type = type,
                    cardNumber = faker.business().creditCardNumber().replace("-", ""),
                    cardHolder = faker.name().fullName(),
                    expiryMonth = Random.nextInt(1, 13),
                    expiryYear = Random.nextInt(2023, 2030),
                    isDefault = Random.nextBoolean()
                )
            }
            else -> {
                PaymentMethod(
                    id = UUID.randomUUID().toString(),
                    type = type,
                    isDefault = Random.nextBoolean()
                )
            }
        }
    }
}
```