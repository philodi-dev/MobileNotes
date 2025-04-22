package com.philodi.carbonium.ui.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philodi.carbonium.data.remote.model.Product
import com.philodi.carbonium.data.repository.CartRepository
import com.philodi.carbonium.data.repository.ProductRepository
import com.philodi.carbonium.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// State for the product catalog screen
data class ProductCatalogUiState(
    val isLoading: Boolean = true,
    val products: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val selectedCategoryFilters: Set<String> = emptySet(),
    val searchQuery: String = "",
    val sortOption: String = "",
    val errorMessage: String? = null
)

@HiltViewModel
class ProductCatalogViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductCatalogUiState())
    val uiState: StateFlow<ProductCatalogUiState> = _uiState.asStateFlow()
    
    // Cart item count
    val cartItemCount: Flow<Int> = cartRepository.getCartItemCount()
    
    // Sample categories (in a real app, these would be fetched from the API)
    private val availableCategories = listOf(
        "All",
        "Electronics",
        "Clothing",
        "Books",
        "Home",
        "Toys"
    )

    init {
        _uiState.update { it.copy(categories = availableCategories) }
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // Refresh products from API
            try {
                productRepository.refreshProducts()
                
                // Get all products or filtered by category
                val category = if (_uiState.value.selectedCategory == "All" || _uiState.value.selectedCategory == null) {
                    null
                } else {
                    _uiState.value.selectedCategory
                }
                
                productRepository.getProducts(category = category).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            var filteredProducts = result.data
                            
                            // Apply search filter if needed
                            if (_uiState.value.searchQuery.isNotEmpty()) {
                                filteredProducts = filteredProducts.filter { product ->
                                    product.name.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                                    product.description.contains(_uiState.value.searchQuery, ignoreCase = true)
                                }
                            }
                            
                            // Apply category filters if needed
                            if (_uiState.value.selectedCategoryFilters.isNotEmpty()) {
                                filteredProducts = filteredProducts.filter { product ->
                                    _uiState.value.selectedCategoryFilters.contains(product.category)
                                }
                            }
                            
                            // Apply sorting
                            filteredProducts = when (_uiState.value.sortOption) {
                                "Price: Low to High" -> filteredProducts.sortedBy { it.getFinalPrice() }
                                "Price: High to Low" -> filteredProducts.sortedByDescending { it.getFinalPrice() }
                                "Newest First" -> filteredProducts.sortedByDescending { it.createdAt }
                                "Popularity" -> filteredProducts // Would need a popularity metric
                                "Rating" -> filteredProducts.sortedByDescending { it.rating }
                                else -> filteredProducts
                            }
                            
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    products = filteredProducts
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

    // Select category and refresh products
    fun selectCategory(category: String) {
        _uiState.update { 
            it.copy(
                selectedCategory = if (category == "All") null else category
            )
        }
        loadProducts()
    }

    // Search products
    fun searchProducts(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadProducts()
    }

    // Toggle category filter
    fun toggleCategoryFilter(category: String) {
        val currentFilters = _uiState.value.selectedCategoryFilters
        val newFilters = if (currentFilters.contains(category)) {
            currentFilters - category
        } else {
            currentFilters + category
        }
        _uiState.update { it.copy(selectedCategoryFilters = newFilters) }
    }

    // Clear all filters
    fun clearFilters() {
        _uiState.update { 
            it.copy(
                selectedCategoryFilters = emptySet(),
                searchQuery = "",
                sortOption = ""
            )
        }
        loadProducts()
    }

    // Apply filters
    fun applyFilters() {
        loadProducts()
    }

    // Set sort option
    fun setSortOption(option: String) {
        _uiState.update { it.copy(sortOption = option) }
        loadProducts()
    }
}
