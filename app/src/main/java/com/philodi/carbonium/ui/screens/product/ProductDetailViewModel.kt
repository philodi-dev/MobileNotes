package com.philodi.carbonium.ui.screens.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philodi.carbonium.data.remote.model.Product
import com.philodi.carbonium.data.repository.CartRepository
import com.philodi.carbonium.data.repository.ProductRepository
import com.philodi.carbonium.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// State for the product detail screen
data class ProductDetailUiState(
    val isLoading: Boolean = true,
    val product: Product? = null,
    val quantity: Int = 1,
    val similarProducts: List<Product> = emptyList(),
    val isAddingToCart: Boolean = false,
    val addToCartSuccess: Boolean = false,
    val navigateToCart: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true,
                    errorMessage = null
                ) 
            }
            
            try {
                when (val result = productRepository.getProductById(productId)) {
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                product = result.data,
                                quantity = 1 // Reset quantity when loading new product
                            )
                        }
                        loadSimilarProducts(result.data.category)
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
                        errorMessage = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }

    private fun loadSimilarProducts(category: String) {
        viewModelScope.launch {
            try {
                productRepository.getProducts(category = category).collect { result ->
                    if (result is Resource.Success) {
                        // Filter out current product and limit to 5 items
                        val similar = result.data
                            .filter { it.id != _uiState.value.product?.id }
                            .take(5)
                            
                        _uiState.update { 
                            it.copy(similarProducts = similar)
                        }
                    }
                }
            } catch (e: Exception) {
                // Just log error for similar products, don't update UI state
                // as this is not critical functionality
                e.printStackTrace()
            }
        }
    }

    fun incrementQuantity() {
        val currentQuantity = _uiState.value.quantity
        val maxQuantity = _uiState.value.product?.stock ?: 0
        
        if (currentQuantity < maxQuantity) {
            _uiState.update { 
                it.copy(quantity = currentQuantity + 1)
            }
        }
    }

    fun decrementQuantity() {
        val currentQuantity = _uiState.value.quantity
        
        if (currentQuantity > 1) {
            _uiState.update { 
                it.copy(quantity = currentQuantity - 1)
            }
        }
    }

    fun addToCart(onSuccess: () -> Unit) {
        val product = _uiState.value.product ?: return
        val quantity = _uiState.value.quantity
        
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isAddingToCart = true,
                    errorMessage = null
                )
            }
            
            try {
                when (val result = cartRepository.addToCart(product.id, quantity)) {
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(
                                isAddingToCart = false,
                                addToCartSuccess = true
                            )
                        }
                        onSuccess()
                    }
                    is Resource.Error -> {
                        _uiState.update { 
                            it.copy(
                                isAddingToCart = false,
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
                        isAddingToCart = false,
                        errorMessage = e.message ?: "Failed to add product to cart"
                    )
                }
            }
        }
    }

    fun resetAddToCartSuccess() {
        _uiState.update { it.copy(addToCartSuccess = false) }
    }

    fun setNavigateToCart() {
        _uiState.update { it.copy(navigateToCart = true) }
    }

    fun resetNavigateToCart() {
        _uiState.update { it.copy(navigateToCart = false) }
    }
}
