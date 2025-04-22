package com.philodi.carbonium.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philodi.carbonium.data.model.Product
import com.philodi.carbonium.data.model.ProductCategory
import com.philodi.carbonium.data.repository.FakeDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val fakeDataRepository: FakeDataRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()
    
    init {
        loadProducts()
    }
    
    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val products = fakeDataRepository.generateProducts(50)
                _uiState.update { 
                    it.copy(
                        products = products,
                        filteredProducts = products,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load products: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    fun getProductDetails(productId: String): Product? {
        return _uiState.value.products.find { it.id == productId }
    }
    
    fun filterProductsByCategory(category: ProductCategory?) {
        _uiState.update { state ->
            val filteredList = if (category == null) {
                state.products
            } else {
                state.products.filter { it.category == category }
            }
            state.copy(
                selectedCategory = category,
                filteredProducts = filteredList
            )
        }
    }
    
    fun searchProducts(query: String) {
        if (query.isBlank()) {
            resetSearch()
            return
        }
        
        _uiState.update { state ->
            val searchResults = state.products.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                product.description.contains(query, ignoreCase = true) ||
                product.category.name.contains(query, ignoreCase = true)
            }
            state.copy(
                searchQuery = query,
                filteredProducts = searchResults
            )
        }
    }
    
    fun resetSearch() {
        _uiState.update { state ->
            val filteredList = if (state.selectedCategory == null) {
                state.products
            } else {
                state.products.filter { it.category == state.selectedCategory }
            }
            state.copy(
                searchQuery = "",
                filteredProducts = filteredList
            )
        }
    }
    
    fun sortProducts(sortType: SortType) {
        _uiState.update { state ->
            val sortedList = when (sortType) {
                SortType.NAME_ASC -> state.filteredProducts.sortedBy { it.name }
                SortType.NAME_DESC -> state.filteredProducts.sortedByDescending { it.name }
                SortType.PRICE_ASC -> state.filteredProducts.sortedBy { it.price }
                SortType.PRICE_DESC -> state.filteredProducts.sortedByDescending { it.price }
                SortType.RATING -> state.filteredProducts.sortedByDescending { it.rating }
            }
            state.copy(
                sortType = sortType,
                filteredProducts = sortedList
            )
        }
    }
}

data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val selectedCategory: ProductCategory? = null,
    val searchQuery: String = "",
    val sortType: SortType = SortType.NAME_ASC,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class SortType {
    NAME_ASC,
    NAME_DESC,
    PRICE_ASC,
    PRICE_DESC,
    RATING
}