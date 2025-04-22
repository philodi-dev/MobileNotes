package com.philodi.carbonium.data.repository

import com.philodi.carbonium.data.remote.ApiService
import com.philodi.carbonium.data.remote.CreateOrderRequest
import com.philodi.carbonium.data.remote.model.Address
import com.philodi.carbonium.data.remote.model.Order
import com.philodi.carbonium.data.remote.model.TrackingInfo
import com.philodi.carbonium.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Repository interface for order operations
 */
interface OrderRepository {
    suspend fun getOrders(): Resource<List<Order>>
    suspend fun getOrder(id: String): Resource<Order>
    suspend fun getOrderTracking(id: String): Resource<TrackingInfo>
    suspend fun createOrder(
        shippingAddress: Address,
        billingAddress: Address,
        paymentMethod: String,
        paymentId: String
    ): Resource<Order>
}

/**
 * Implementation of OrderRepository
 */
class OrderRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val cartRepository: CartRepository
) : OrderRepository {
    
    override suspend fun getOrders(): Resource<List<Order>> {
        return try {
            val orders = apiService.getOrders()
            Resource.Success(orders)
        } catch (e: Exception) {
            Resource.Error("Failed to get orders: ${e.message}", e)
        }
    }
    
    override suspend fun getOrder(id: String): Resource<Order> {
        return try {
            val order = apiService.getOrder(id)
            Resource.Success(order)
        } catch (e: Exception) {
            Resource.Error("Failed to get order details: ${e.message}", e)
        }
    }
    
    override suspend fun getOrderTracking(id: String): Resource<TrackingInfo> {
        return try {
            val tracking = apiService.getOrderTracking(id)
            Resource.Success(tracking)
        } catch (e: Exception) {
            Resource.Error("Failed to get order tracking: ${e.message}", e)
        }
    }
    
    override suspend fun createOrder(
        shippingAddress: Address,
        billingAddress: Address,
        paymentMethod: String,
        paymentId: String
    ): Resource<Order> {
        return try {
            val order = apiService.createOrder(
                CreateOrderRequest(
                    shippingAddress = shippingAddress,
                    billingAddress = billingAddress,
                    paymentMethod = paymentMethod,
                    paymentId = paymentId
                )
            )
            
            // Clear cart after successful order
            cartRepository.clearCart()
            
            Resource.Success(order)
        } catch (e: Exception) {
            Resource.Error("Failed to create order: ${e.message}", e)
        }
    }
}
