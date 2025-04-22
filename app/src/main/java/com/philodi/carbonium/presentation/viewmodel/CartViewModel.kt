package com.philodi.carbonium.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philodi.carbonium.data.model.Cart
import com.philodi.carbonium.data.model.CartItem
import com.philodi.carbonium.data.model.Product
import com.philodi.carbonium.data.repository.FakeDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val fakeDataRepository: FakeDataRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()
    
    init {
        // Initially load an empty cart
        _uiState.update { 
            it.copy(
                cart = Cart(
                    id = UUID.randomUUID().toString(),
                    items = emptyList(),
                    subtotal = BigDecimal.ZERO,
                    tax = BigDecimal.ZERO,
                    shipping = BigDecimal.ZERO,
                    discount = BigDecimal.ZERO,
                    total = BigDecimal.ZERO
                )
            )
        }
    }
    
    fun addToCart(product: Product, quantity: Int = 1, selectedAttributes: Map<String, String> = emptyMap()) {
        viewModelScope.launch {
            try {
                _uiState.update { state ->
                    val currentCart = state.cart
                    
                    // Check if this product is already in the cart
                    val existingItemIndex = currentCart.items.indexOfFirst { it.productId == product.id }
                    
                    val updatedItems = if (existingItemIndex >= 0) {
                        // Update the quantity of the existing item
                        val existingItem = currentCart.items[existingItemIndex]
                        val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
                        currentCart.items.toMutableList().apply {
                            set(existingItemIndex, updatedItem)
                        }
                    } else {
                        // Add a new item to the cart
                        val newItem = CartItem(
                            id = UUID.randomUUID().toString(),
                            productId = product.id,
                            name = product.name,
                            imageUrl = product.imageUrl,
                            price = product.price,
                            quantity = quantity,
                            attributes = selectedAttributes
                        )
                        currentCart.items + newItem
                    }
                    
                    // Recalculate cart totals
                    val subtotal = updatedItems.fold(BigDecimal.ZERO) { acc, item -> 
                        acc.add(item.price.multiply(BigDecimal(item.quantity))) 
                    }
                    val tax = subtotal.multiply(BigDecimal.valueOf(0.08))
                    val shipping = if (subtotal > BigDecimal.valueOf(100)) 
                        BigDecimal.ZERO else BigDecimal.valueOf(12.99)
                    val discount = state.promoCodeApplied ? 
                        subtotal.multiply(BigDecimal.valueOf(0.1)) : BigDecimal.ZERO
                    val total = subtotal.add(tax).add(shipping).subtract(discount)
                    
                    state.copy(
                        cart = currentCart.copy(
                            items = updatedItems,
                            subtotal = subtotal,
                            tax = tax,
                            shipping = shipping,
                            discount = discount,
                            total = total
                        ),
                        message = "Added ${product.name} to cart"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to add item to cart: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    fun removeFromCart(itemId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { state ->
                    val currentCart = state.cart
                    val itemToRemove = currentCart.items.find { it.id == itemId }
                    
                    if (itemToRemove == null) {
                        return@update state.copy(
                            error = "Item not found in cart"
                        )
                    }
                    
                    val updatedItems = currentCart.items.filter { it.id != itemId }
                    
                    // Recalculate cart totals
                    val subtotal = updatedItems.fold(BigDecimal.ZERO) { acc, item -> 
                        acc.add(item.price.multiply(BigDecimal(item.quantity))) 
                    }
                    val tax = subtotal.multiply(BigDecimal.valueOf(0.08))
                    val shipping = if (subtotal > BigDecimal.valueOf(100) || subtotal == BigDecimal.ZERO) 
                        BigDecimal.ZERO else BigDecimal.valueOf(12.99)
                    val discount = state.promoCodeApplied ? 
                        subtotal.multiply(BigDecimal.valueOf(0.1)) : BigDecimal.ZERO
                    val total = subtotal.add(tax).add(shipping).subtract(discount)
                    
                    state.copy(
                        cart = currentCart.copy(
                            items = updatedItems,
                            subtotal = subtotal,
                            tax = tax,
                            shipping = shipping,
                            discount = discount,
                            total = total
                        ),
                        message = "Removed ${itemToRemove.name} from cart"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to remove item from cart: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    fun updateItemQuantity(itemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(itemId)
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { state ->
                    val currentCart = state.cart
                    val itemIndex = currentCart.items.indexOfFirst { it.id == itemId }
                    
                    if (itemIndex < 0) {
                        return@update state.copy(
                            error = "Item not found in cart"
                        )
                    }
                    
                    val updatedItems = currentCart.items.toMutableList().apply {
                        val currentItem = get(itemIndex)
                        set(itemIndex, currentItem.copy(quantity = newQuantity))
                    }
                    
                    // Recalculate cart totals
                    val subtotal = updatedItems.fold(BigDecimal.ZERO) { acc, item -> 
                        acc.add(item.price.multiply(BigDecimal(item.quantity))) 
                    }
                    val tax = subtotal.multiply(BigDecimal.valueOf(0.08))
                    val shipping = if (subtotal > BigDecimal.valueOf(100)) 
                        BigDecimal.ZERO else BigDecimal.valueOf(12.99)
                    val discount = state.promoCodeApplied ? 
                        subtotal.multiply(BigDecimal.valueOf(0.1)) : BigDecimal.ZERO
                    val total = subtotal.add(tax).add(shipping).subtract(discount)
                    
                    state.copy(
                        cart = currentCart.copy(
                            items = updatedItems,
                            subtotal = subtotal,
                            tax = tax,
                            shipping = shipping,
                            discount = discount,
                            total = total
                        ),
                        message = "Updated quantity"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to update item quantity: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    fun applyPromoCode(promoCode: String) {
        viewModelScope.launch {
            // In a real app, you would validate the promo code with an API
            // For this example, we'll just apply a 10% discount for any code
            val isValid = promoCode.isNotBlank()
            
            _uiState.update { state ->
                val currentCart = state.cart
                
                if (!isValid) {
                    return@update state.copy(
                        error = "Invalid promo code"
                    )
                }
                
                // Apply 10% discount
                val discount = currentCart.subtotal.multiply(BigDecimal.valueOf(0.1))
                val total = currentCart.subtotal.add(currentCart.tax).add(currentCart.shipping).subtract(discount)
                
                state.copy(
                    promoCodeApplied = true,
                    promoCode = promoCode,
                    cart = currentCart.copy(
                        discount = discount,
                        total = total
                    ),
                    message = "Promo code applied: $promoCode"
                )
            }
        }
    }
    
    fun clearCart() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    cart = Cart(
                        id = UUID.randomUUID().toString(),
                        items = emptyList(),
                        subtotal = BigDecimal.ZERO,
                        tax = BigDecimal.ZERO,
                        shipping = BigDecimal.ZERO,
                        discount = BigDecimal.ZERO,
                        total = BigDecimal.ZERO
                    ),
                    promoCodeApplied = false,
                    promoCode = "",
                    message = "Cart cleared"
                )
            }
        }
    }
    
    fun loadSampleCart() {
        viewModelScope.launch {
            try {
                val sampleCart = fakeDataRepository.generateCart()
                _uiState.update { 
                    it.copy(
                        cart = sampleCart,
                        message = "Sample cart loaded"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to load sample cart: ${e.localizedMessage}"
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

data class CartUiState(
    val cart: Cart? = null,
    val promoCodeApplied: Boolean = false,
    val promoCode: String = "",
    val message: String? = null,
    val error: String? = null
)