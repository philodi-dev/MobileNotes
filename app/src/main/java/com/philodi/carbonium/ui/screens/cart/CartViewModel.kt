package com.philodi.carbonium.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philodi.carbonium.data.remote.CartItem
import com.philodi.carbonium.data.repository.CartRepository
import com.philodi.carbonium.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// State for the cart screen
data class CartUiState(
    val isLoading: Boolean = true,
    val cartItems: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val errorMessage: String? = null
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun loadCartItems() {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true,
                    errorMessage = null
                ) 
            }
            
            try {
                cartRepository.getCartItems().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            // Calculate totals
                            val cartItems = result.data
                            val subtotal = cartItems.sumOf { it.price }
                            val tax = subtotal * 0.1 // Assuming 10% tax
                            val shipping = if (subtotal > 50.0) 0.0 else 5.99 // Free shipping over $50
                            val total = subtotal + tax + shipping
                            
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    cartItems = cartItems,
                                    subtotal = subtotal,
                                    tax = tax,
                                    shipping = shipping,
                                    total = total
                                )
                            }
                        }
                        is Resource.Error -> {
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    errorMessage = result.message
                                )
                            }
                        }
                        is Resource.Loading -> {
                            // Already set loading state
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }

    fun removeCartItem(itemId: String) {
        viewModelScope.launch {
            try {
                // Set loading state for better UX
                _uiState.update { it.copy(isLoading = true) }
                
                when (val result = cartRepository.removeCartItem(itemId)) {
                    is Resource.Success -> {
                        // Refresh cart items
                        loadCartItems()
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                    is Resource.Loading -> {
                        // Already set loading state
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to remove item from cart"
                    )
                }
            }
        }
    }

    fun updateCartItemQuantity(itemId: String, quantity: Int) {
        if (quantity <= 0) return
        
        viewModelScope.launch {
            try {
                // Set loading state for better UX
                _uiState.update { it.copy(isLoading = true) }
                
                when (val result = cartRepository.updateCartItemQuantity(itemId, quantity)) {
                    is Resource.Success -> {
                        // Refresh cart items
                        loadCartItems()
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                    is Resource.Loading -> {
                        // Already set loading state
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to update cart"
                    )
                }
            }
        }
    }
}
