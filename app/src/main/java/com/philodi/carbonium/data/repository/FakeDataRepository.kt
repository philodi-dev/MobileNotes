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
    
    fun generateProducts(count: Int = 30): List<Product> {
        return (1..count).map { generateProduct() }
    }
    
    fun generateProduct(): Product {
        val category = ProductCategory.values().random()
        val id = UUID.randomUUID().toString()
        
        // Generate product name based on category
        val name = when (category) {
            ProductCategory.ELECTRONICS -> "${faker.commerce().productName()} ${faker.company().name().split(" ").first()}"
            ProductCategory.CLOTHING -> "${faker.color().name()} ${faker.commerce().productName()}"
            ProductCategory.HOME -> "${faker.commerce().productName()} for ${faker.space().planet()}"
            ProductCategory.BEAUTY -> "${faker.commerce().productName()} by ${faker.company().name()}"
            ProductCategory.FOOD -> "${faker.food().dish()} from ${faker.country().name()}"
            ProductCategory.SPORTS -> "${faker.commerce().productName()} for ${faker.sport().name()}"
            ProductCategory.BOOKS -> "${faker.book().title()} by ${faker.book().author()}"
            ProductCategory.TOYS -> "${faker.commerce().productName()} for ${faker.demographic().demonym()}"
            ProductCategory.OTHER -> faker.commerce().productName()
        }
        
        // Generate a longer product description
        val description = """
            ${faker.lorem().paragraph(3)} 
            
            Features:
            - ${faker.lorem().sentence()}
            - ${faker.lorem().sentence()}
            - ${faker.lorem().sentence()}
            
            ${faker.lorem().paragraph(2)}
        """.trimIndent()
        
        // Generate realistic price
        val basePrice = when (category) {
            ProductCategory.ELECTRONICS -> Random.nextDouble(50.0, 1500.0)
            ProductCategory.CLOTHING -> Random.nextDouble(20.0, 200.0)
            ProductCategory.HOME -> Random.nextDouble(30.0, 500.0)
            ProductCategory.BEAUTY -> Random.nextDouble(10.0, 100.0)
            ProductCategory.FOOD -> Random.nextDouble(5.0, 50.0)
            ProductCategory.SPORTS -> Random.nextDouble(15.0, 300.0)
            ProductCategory.BOOKS -> Random.nextDouble(10.0, 40.0)
            ProductCategory.TOYS -> Random.nextDouble(15.0, 150.0)
            ProductCategory.OTHER -> Random.nextDouble(10.0, 200.0)
        }
        val price = BigDecimal.valueOf(Math.round(basePrice * 100) / 100.0)
        
        // Generate random rating between 3.0 and 5.0 with most products above 4.0
        val rating = if (Random.nextFloat() < 0.7f) {
            4.0f + Random.nextFloat() * 1.0f
        } else {
            3.0f + Random.nextFloat() * 2.0f
        }
        
        // Generate realistic review count
        val reviewCount = Random.nextInt(0, 500)
        
        // Majority of products should be in stock
        val inStock = Random.nextFloat() < 0.85f
        
        // Generate product image URL based on category
        val imageNumber = Random.nextInt(1, 1000)
        val imageUrl = "https://picsum.photos/seed/$id/500"
        
        // Generate product attributes based on category
        val attributes = when (category) {
            ProductCategory.ELECTRONICS -> mapOf(
                "Brand" to faker.company().name(),
                "Model" to "${faker.app().name()} ${Random.nextInt(1000, 9999)}",
                "Color" to faker.color().name(),
                "Weight" to "${Random.nextDouble(0.5, 5.0)} kg",
                "Warranty" to "${Random.nextInt(1, 5)} years"
            )
            ProductCategory.CLOTHING -> mapOf(
                "Brand" to faker.company().name(),
                "Color" to faker.color().name(),
                "Size" to listOf("XS", "S", "M", "L", "XL", "XXL").random(),
                "Material" to faker.commerce().material(),
                "Gender" to listOf("Men", "Women", "Unisex").random()
            )
            else -> mapOf(
                "Brand" to faker.company().name(),
                "Color" to faker.color().name(),
                "Weight" to "${Random.nextDouble(0.1, 10.0)} kg"
            )
        }
        
        return Product(
            id = id,
            name = name,
            description = description,
            price = price,
            category = category,
            imageUrl = imageUrl,
            rating = rating,
            reviewCount = reviewCount,
            inStock = inStock,
            attributes = attributes
        )
    }
    
    fun generateUser(): User {
        val firstName = faker.name().firstName()
        val lastName = faker.name().lastName()
        
        return User(
            id = UUID.randomUUID().toString(),
            email = faker.internet().emailAddress(firstName.lowercase() + "." + lastName.lowercase()),
            firstName = firstName,
            lastName = lastName,
            phoneNumber = faker.phoneNumber().cellPhone(),
            addresses = listOf(generateAddress(true)),
            hasActiveSubscription = Random.nextBoolean()
        )
    }
    
    fun generateAddress(isDefault: Boolean = false): Address {
        return Address(
            id = UUID.randomUUID().toString(),
            streetAddress = faker.address().streetAddress(),
            apartment = if (Random.nextBoolean()) faker.address().secondaryAddress() else null,
            city = faker.address().city(),
            state = faker.address().state(),
            zipCode = faker.address().zipCode(),
            country = faker.address().country(),
            isDefault = isDefault,
            label = listOf("Home", "Work", "Other").random()
        )
    }
    
    fun generateCart(): Cart {
        val items = (1..Random.nextInt(1, 8)).map { generateCartItem() }
        val subtotal = items.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.subtotal) }
        val tax = subtotal.multiply(BigDecimal.valueOf(0.08))
        val shipping = if (subtotal > BigDecimal.valueOf(100)) BigDecimal.ZERO else BigDecimal.valueOf(12.99)
        val discount = if (Random.nextBoolean()) subtotal.multiply(BigDecimal.valueOf(0.1)) else BigDecimal.ZERO
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
    
    fun generateCartItem(): CartItem {
        val product = generateProduct()
        return CartItem(
            id = UUID.randomUUID().toString(),
            productId = product.id,
            name = product.name,
            imageUrl = product.imageUrl,
            price = product.price,
            quantity = Random.nextInt(1, 5),
            attributes = product.attributes.filter { Random.nextBoolean() }
        )
    }
    
    fun generateOrder(user: User? = null): Order {
        val actualUser = user ?: generateUser()
        val cart = generateCart()
        val now = Date()
        val estimatedDelivery = Date(now.time + TimeUnit.DAYS.toMillis(Random.nextLong(1, 14)))
        
        return Order(
            id = UUID.randomUUID().toString(),
            userId = actualUser.id,
            items = cart.items,
            shippingAddress = actualUser.addresses.firstOrNull() ?: generateAddress(),
            billingAddress = actualUser.addresses.firstOrNull() ?: generateAddress(),
            paymentMethod = generatePaymentMethod(),
            status = OrderStatus.values().random(),
            subtotal = cart.subtotal,
            tax = cart.tax,
            shipping = cart.shipping,
            discount = cart.discount,
            total = cart.total,
            createdAt = Date(now.time - TimeUnit.DAYS.toMillis(Random.nextLong(0, 30))),
            updatedAt = now,
            estimatedDelivery = estimatedDelivery,
            trackingNumber = if (Random.nextBoolean()) faker.code().isbn13() else null
        )
    }
    
    fun generatePaymentMethod(isDefault: Boolean = true): PaymentMethod {
        return PaymentMethod(
            id = UUID.randomUUID().toString(),
            type = PaymentType.values().random(),
            lastFourDigits = (1000..9999).random().toString(),
            expiryDate = "${(1..12).random()}/${2025 + (0..5).random()}",
            cardholderName = faker.name().fullName(),
            isDefault = isDefault
        )
    }
    
    fun generateSubscriptionPlan(): SubscriptionPlan {
        val tier = listOf("Basic", "Standard", "Premium", "Enterprise").random()
        val cycle = BillingCycle.values().random()
        
        val baseMonthlyPrice = when (tier) {
            "Basic" -> 9.99
            "Standard" -> 19.99
            "Premium" -> 29.99
            else -> 49.99
        }
        
        val multiplier = when (cycle) {
            BillingCycle.MONTHLY -> 1.0
            BillingCycle.QUARTERLY -> 3.0 * 0.9 // 10% discount
            BillingCycle.BIANNUALLY -> 6.0 * 0.85 // 15% discount
            BillingCycle.ANNUALLY -> 12.0 * 0.75 // 25% discount
        }
        
        val price = BigDecimal.valueOf(baseMonthlyPrice * multiplier)
        
        val baseFeatures = listOf(
            "Customer Support",
            "Product Updates",
            "Mobile App Access"
        )
        
        val additionalFeatures = when (tier) {
            "Basic" -> listOf("Basic Analytics")
            "Standard" -> listOf("Advanced Analytics", "Priority Support")
            "Premium" -> listOf("Advanced Analytics", "Priority Support", "Custom Branding", "API Access")
            else -> listOf("Advanced Analytics", "Priority Support", "Custom Branding", "API Access", "Dedicated Account Manager", "Custom Integrations")
        }
        
        return SubscriptionPlan(
            id = UUID.randomUUID().toString(),
            name = "$tier ${cycle.name.toLowerCase().capitalize()}",
            description = "The perfect plan for ${faker.company().bs()}",
            price = price,
            billingCycle = cycle,
            features = baseFeatures + additionalFeatures
        )
    }
    
    fun generateSubscription(user: User? = null): Subscription {
        val actualUser = user ?: generateUser()
        val plan = generateSubscriptionPlan()
        val now = Date()
        
        val startDate = Date(now.time - TimeUnit.DAYS.toMillis(Random.nextLong(1, 365)))
        
        val daysToAdd = when (plan.billingCycle) {
            BillingCycle.MONTHLY -> 30L
            BillingCycle.QUARTERLY -> 90L
            BillingCycle.BIANNUALLY -> 180L
            BillingCycle.ANNUALLY -> 365L
        }
        
        val nextBillingDate = Date(startDate.time + TimeUnit.DAYS.toMillis(daysToAdd))
        
        return Subscription(
            id = UUID.randomUUID().toString(),
            userId = actualUser.id,
            plan = plan,
            status = SubscriptionStatus.values().random(),
            startDate = startDate,
            nextBillingDate = nextBillingDate,
            paymentMethod = generatePaymentMethod(),
            autoRenew = Random.nextBoolean(),
            billingAddress = actualUser.addresses.firstOrNull() ?: generateAddress()
        )
    }
}