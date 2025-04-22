package com.philodi.carbonium.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philodi.carbonium.data.remote.model.Product
import com.philodi.carbonium.data.remote.model.Service
import com.philodi.carbonium.data.repository.CartRepository
import com.philodi.carbonium.data.repository.ProductRepository
import com.philodi.carbonium.data.repository.SubscriptionRepository
import com.philodi.carbonium.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// State for the home screen
data class HomeUiState(
    val isLoading: Boolean = true,
    val featuredProducts: List<Product> = emptyList(),
    val popularProducts: List<Product> = emptyList(),
    val services: List<Service> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    // Cart item count
    val cartItemCount: Flow<Int> = cartRepository.getCartItemCount()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                // Refresh products from API
                productRepository.refreshProducts()
                
                // Load featured products
                productRepository.getFeaturedProducts().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _uiState.update { 
                                it.copy(featuredProducts = result.data)
                            }
                        }
                        is Resource.Error -> {
                            _uiState.update { 
                                it.copy(errorMessage = result.message)
                            }
                        }
                        is Resource.Loading -> {
                            // Already set loading state
                        }
                    }
                }
                
                // Load popular products (using regular products for now)
                productRepository.getProducts().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _uiState.update { 
                                it.copy(popularProducts = result.data.take(10))
                            }
                        }
                        is Resource.Error -> {
                            if (_uiState.value.errorMessage == null) {
                                _uiState.update { 
                                    it.copy(errorMessage = result.message)
                                }
                            }
                        }
                        is Resource.Loading -> {
                            // Already set loading state
                        }
                    }
                }
                
                // Load services
                when (val servicesResult = subscriptionRepository.getServices()) {
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(services = servicesResult.data)
                        }
                    }
                    is Resource.Error -> {
                        if (_uiState.value.errorMessage == null) {
                            _uiState.update { 
                                it.copy(errorMessage = servicesResult.message)
                            }
                        }
                    }
                    is Resource.Loading -> {
                        // Already set loading state
                    }
                }
                
                // Update loading state when all data is loaded
                _uiState.update { it.copy(isLoading = false) }
                
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
}
