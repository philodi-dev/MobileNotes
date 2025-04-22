package com.philodi.carbonium.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philodi.carbonium.data.model.Address
import com.philodi.carbonium.data.model.Cart
import com.philodi.carbonium.data.model.Order
import com.philodi.carbonium.data.model.OrderStatus
import com.philodi.carbonium.data.model.PaymentMethod
import com.philodi.carbonium.data.repository.FakeDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val fakeDataRepository: FakeDataRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()
    
    fun loadUserOrders(userId: String?) {
        if (userId == null) {
            _uiState.update { it.copy(error = "User ID is required to load orders") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Simulate network delay
                delay(1000)
                
                // Generate between 0 and 5 past orders
                val orderCount = (0..5).random()
                val pastOrders = (1..orderCount).map { fakeDataRepository.generateOrder() }
                
                _uiState.update { 
                    it.copy(
                        orders = pastOrders,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load orders: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    fun getOrderById(orderId: String): Order? {
        return _uiState.value.orders.find { it.id == orderId }
    }
    
    fun placeOrder(
        userId: String?,
        cart: Cart,
        shippingAddress: Address,
        billingAddress: Address,
        paymentMethod: PaymentMethod
    ) {
        if (userId == null) {
            _uiState.update { it.copy(error = "User ID is required to place an order") }
            return
        }
        
        if (cart.items.isEmpty()) {
            _uiState.update { it.copy(error = "Cart is empty") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Simulate network delay
                delay(2000)
                
                // Create a new order
                val now = Date()
                
                val newOrder = Order(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    items = cart.items,
                    shippingAddress = shippingAddress,
                    billingAddress = billingAddress,
                    paymentMethod = paymentMethod,
                    status = OrderStatus.PENDING,
                    subtotal = cart.subtotal,
                    tax = cart.tax,
                    shipping = cart.shipping,
                    discount = cart.discount,
                    total = cart.total,
                    createdAt = now,
                    updatedAt = now,
                    estimatedDelivery = Date(now.time + (7 * 24 * 60 * 60 * 1000)), // Estimated delivery in 7 days
                    trackingNumber = null // No tracking number yet
                )
                
                // Add the new order to the list
                _uiState.update { state ->
                    state.copy(
                        orders = listOf(newOrder) + state.orders,
                        currentOrder = newOrder,
                        isLoading = false,
                        error = null,
                        message = "Order placed successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to place order: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Simulate network delay
                delay(1000)
                
                // Find the order
                val orderIndex = _uiState.value.orders.indexOfFirst { it.id == orderId }
                
                if (orderIndex == -1) {
                    throw Exception("Order not found")
                }
                
                val order = _uiState.value.orders[orderIndex]
                
                // Only PENDING orders can be cancelled
                if (order.status != OrderStatus.PENDING && order.status != OrderStatus.PROCESSING) {
                    throw Exception("Only pending or processing orders can be cancelled")
                }
                
                // Update the order status
                val updatedOrder = order.copy(
                    status = OrderStatus.CANCELLED,
                    updatedAt = Date()
                )
                
                // Update the order list
                _uiState.update { state ->
                    val updatedOrders = state.orders.toMutableList()
                    updatedOrders[orderIndex] = updatedOrder
                    
                    state.copy(
                        orders = updatedOrders,
                        currentOrder = if (state.currentOrder?.id == orderId) updatedOrder else state.currentOrder,
                        isLoading = false,
                        error = null,
                        message = "Order cancelled successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to cancel order: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    fun loadOrderDetails(orderId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Find the order
                val order = _uiState.value.orders.find { it.id == orderId }
                    ?: throw Exception("Order not found")
                    
                _uiState.update { 
                    it.copy(
                        currentOrder = order,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load order details: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    fun loadSampleOrders() {
        viewModelScope.launch {
            try {
                val sampleOrders = (1..3).map { fakeDataRepository.generateOrder() }
                _uiState.update { 
                    it.copy(
                        orders = sampleOrders,
                        message = "Sample orders loaded"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to load sample orders: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class OrderUiState(
    val orders: List<Order> = emptyList(),
    val currentOrder: Order? = null,
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)