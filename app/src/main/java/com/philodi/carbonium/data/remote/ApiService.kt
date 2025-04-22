package com.philodi.carbonium.data.remote

import com.philodi.carbonium.data.remote.model.Child
import com.philodi.carbonium.data.remote.model.Order
import com.philodi.carbonium.data.remote.model.Product
import com.philodi.carbonium.data.remote.model.Service
import com.philodi.carbonium.data.remote.model.Subscription
import com.philodi.carbonium.data.remote.model.TrackingInfo
import com.philodi.carbonium.data.remote.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API service interface for network requests
 */
interface ApiService {
    
    // Authentication
    @POST("auth/phone")
    suspend fun signInWithPhone(@Body request: PhoneAuthRequest): PhoneAuthResponse
    
    @POST("auth/verify-code")
    suspend fun verifyPhoneCode(@Body request: VerifyCodeRequest): SignInResponse
    
    @GET("auth/google")
    suspend fun signInWithGoogle(@Query("token") token: String): SignInResponse
    
    // User
    @GET("users/me")
    suspend fun getCurrentUser(): User
    
    @PUT("users/me")
    suspend fun updateUser(@Body user: User): User
    
    // Children
    @GET("users/me/children")
    suspend fun getChildren(): List<Child>
    
    @POST("users/me/children")
    suspend fun addChild(@Body child: Child): Child
    
    @GET("users/me/children/{id}")
    suspend fun getChild(@Path("id") id: String): Child
    
    @PUT("users/me/children/{id}")
    suspend fun updateChild(@Path("id") id: String, @Body child: Child): Child
    
    @DELETE("users/me/children/{id}")
    suspend fun deleteChild(@Path("id") id: String)
    
    // Products
    @GET("products")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("sort") sort: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): List<Product>
    
    @GET("products/featured")
    suspend fun getFeaturedProducts(): List<Product>
    
    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: String): Product
    
    // Cart
    @POST("cart/add")
    suspend fun addToCart(@Body request: AddToCartRequest): Cart
    
    @PUT("cart/update")
    suspend fun updateCartItem(@Body request: UpdateCartItemRequest): Cart
    
    @DELETE("cart/items/{itemId}")
    suspend fun removeCartItem(@Path("itemId") itemId: String): Cart
    
    @GET("cart")
    suspend fun getCart(): Cart
    
    // Orders
    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Order
    
    @GET("orders")
    suspend fun getOrders(): List<Order>
    
    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: String): Order
    
    @GET("orders/{id}/tracking")
    suspend fun getOrderTracking(@Path("id") id: String): TrackingInfo
    
    // Services
    @GET("services")
    suspend fun getServices(): List<Service>
    
    @GET("services/{id}")
    suspend fun getService(@Path("id") id: String): Service
    
    // Subscriptions
    @GET("subscriptions")
    suspend fun getSubscriptions(): List<Subscription>
    
    @POST("subscriptions")
    suspend fun createSubscription(@Body request: CreateSubscriptionRequest): Subscription
    
    @GET("subscriptions/{id}")
    suspend fun getSubscription(@Path("id") id: String): Subscription
    
    @PUT("subscriptions/{id}/renew")
    suspend fun renewSubscription(@Path("id") id: String): Subscription
    
    @PUT("subscriptions/{id}/cancel")
    suspend fun cancelSubscription(@Path("id") id: String): Subscription
}

/**
 * Request and response models for API calls
 */
data class PhoneAuthRequest(val phoneNumber: String)
data class PhoneAuthResponse(val verificationId: String)
data class VerifyCodeRequest(val verificationId: String, val code: String)
data class SignInResponse(val token: String, val user: User)

data class AddToCartRequest(val productId: String, val quantity: Int)
data class UpdateCartItemRequest(val itemId: String, val quantity: Int)
data class Cart(val items: List<CartItem>, val subtotal: Double, val total: Double)
data class CartItem(val id: String, val product: Product, val quantity: Int, val price: Double)

data class CreateOrderRequest(
    val shippingAddress: com.philodi.carbonium.data.remote.model.Address,
    val billingAddress: com.philodi.carbonium.data.remote.model.Address,
    val paymentMethod: String,
    val paymentId: String
)

data class CreateSubscriptionRequest(
    val serviceId: String,
    val planId: String,
    val billingCycle: com.philodi.carbonium.data.remote.model.BillingCycle,
    val autoRenew: Boolean,
    val paymentMethod: String
)
