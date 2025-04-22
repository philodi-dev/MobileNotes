package com.philodi.carbonium.ui.screens.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philodi.carbonium.data.remote.model.Address
import com.philodi.carbonium.data.repository.CartRepository
import com.philodi.carbonium.data.repository.UserRepository
import com.philodi.carbonium.util.Resource
import com.philodi.carbonium.util.isValidEmail
import com.philodi.carbonium.util.isValidPhoneNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// State for the checkout screen
data class CheckoutUiState(
    val isLoading: Boolean = true,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val shippingStreet: String = "",
    val shippingCity: String = "",
    val shippingState: String = "",
    val shippingZip: String = "",
    val shippingCountry: String = "",
    val billingStreet: String = "",
    val billingCity: String = "",
    val billingState: String = "",
    val billingZip: String = "",
    val billingCountry: String = "",
    val sameAsBilling: Boolean = true,
    val paymentMethod: String = "credit_card",
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val showNameError: Boolean = false,
    val showEmailError: Boolean = false,
    val showPhoneError: Boolean = false,
    val showShippingStreetError: Boolean = false,
    val showShippingCityError: Boolean = false,
    val showShippingStateError: Boolean = false,
    val showShippingZipError: Boolean = false,
    val showShippingCountryError: Boolean = false,
    val showBillingStreetError: Boolean = false,
    val showBillingCityError: Boolean = false,
    val showBillingStateError: Boolean = false,
    val showBillingZipError: Boolean = false,
    val showBillingCountryError: Boolean = false,
    val navigateToPayment: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    fun loadCheckoutData() {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true,
                    errorMessage = null
                ) 
            }
            
            try {
                // Load user data
                userRepository.getCurrentUser().collect { result ->
                    if (result is Resource.Success) {
                        val user = result.data
                        if (user != null) {
                            _uiState.update { state ->
                                state.copy(
                                    name = user.name,
                                    email = user.email,
                                    phone = user.phone,
                                    shippingStreet = user.address?.street ?: "",
                                    shippingCity = user.address?.city ?: "",
                                    shippingState = user.address?.state ?: "",
                                    shippingZip = user.address?.zipCode ?: "",
                                    shippingCountry = user.address?.country ?: "",
                                    billingStreet = user.address?.street ?: "",
                                    billingCity = user.address?.city ?: "",
                                    billingState = user.address?.state ?: "",
                                    billingZip = user.address?.zipCode ?: "",
                                    billingCountry = user.address?.country ?: ""
                                )
                            }
                        }
                    }
                }
                
                // Load cart data for totals
                cartRepository.getCartItems().collect { result ->
                    if (result is Resource.Success) {
                        val cartItems = result.data
                        val subtotal = cartItems.sumOf { it.price }
                        val tax = subtotal * 0.1 // Assuming 10% tax
                        val shipping = if (subtotal > 50.0) 0.0 else 5.99 // Free shipping over $50
                        val total = subtotal + tax + shipping
                        
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                subtotal = subtotal,
                                tax = tax,
                                shipping = shipping,
                                total = total
                            )
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

    // Update functions for form fields
    fun updateName(name: String) {
        _uiState.update { 
            it.copy(
                name = name,
                showNameError = false
            )
        }
    }
    
    fun updateEmail(email: String) {
        _uiState.update { 
            it.copy(
                email = email,
                showEmailError = false
            )
        }
    }
    
    fun updatePhone(phone: String) {
        _uiState.update { 
            it.copy(
                phone = phone,
                showPhoneError = false
            )
        }
    }
    
    fun updateShippingStreet(street: String) {
        _uiState.update { 
            it.copy(
                shippingStreet = street,
                showShippingStreetError = false
            )
        }
    }
    
    fun updateShippingCity(city: String) {
        _uiState.update { 
            it.copy(
                shippingCity = city,
                showShippingCityError = false
            )
        }
    }
    
    fun updateShippingState(state: String) {
        _uiState.update { 
            it.copy(
                shippingState = state,
                showShippingStateError = false
            )
        }
    }
    
    fun updateShippingZip(zip: String) {
        _uiState.update { 
            it.copy(
                shippingZip = zip,
                showShippingZipError = false
            )
        }
    }
    
    fun updateShippingCountry(country: String) {
        _uiState.update { 
            it.copy(
                shippingCountry = country,
                showShippingCountryError = false
            )
        }
    }
    
    fun updateBillingStreet(street: String) {
        _uiState.update { 
            it.copy(
                billingStreet = street,
                showBillingStreetError = false
            )
        }
    }
    
    fun updateBillingCity(city: String) {
        _uiState.update { 
            it.copy(
                billingCity = city,
                showBillingCityError = false
            )
        }
    }
    
    fun updateBillingState(state: String) {
        _uiState.update { 
            it.copy(
                billingState = state,
                showBillingStateError = false
            )
        }
    }
    
    fun updateBillingZip(zip: String) {
        _uiState.update { 
            it.copy(
                billingZip = zip,
                showBillingZipError = false
            )
        }
    }
    
    fun updateBillingCountry(country: String) {
        _uiState.update { 
            it.copy(
                billingCountry = country,
                showBillingCountryError = false
            )
        }
    }
    
    fun updateSameAsBilling(sameAsBilling: Boolean) {
        _uiState.update { 
            it.copy(sameAsBilling = sameAsBilling)
        }
        
        // If same as billing is checked, copy shipping address to billing address
        if (sameAsBilling) {
            _uiState.update { state ->
                state.copy(
                    billingStreet = state.shippingStreet,
                    billingCity = state.shippingCity,
                    billingState = state.shippingState,
                    billingZip = state.shippingZip,
                    billingCountry = state.shippingCountry,
                    showBillingStreetError = false,
                    showBillingCityError = false,
                    showBillingStateError = false,
                    showBillingZipError = false,
                    showBillingCountryError = false
                )
            }
        }
    }
    
    fun updatePaymentMethod(paymentMethod: String) {
        _uiState.update { 
            it.copy(paymentMethod = paymentMethod)
        }
    }

    // Validate the form and continue to payment
    fun validateAndContinue() {
        var isValid = true
        
        // Validate contact information
        if (_uiState.value.name.isBlank()) {
            _uiState.update { it.copy(showNameError = true) }
            isValid = false
        }
        
        if (_uiState.value.email.isBlank() || !_uiState.value.email.isValidEmail()) {
            _uiState.update { it.copy(showEmailError = true) }
            isValid = false
        }
        
        if (_uiState.value.phone.isBlank() || !_uiState.value.phone.isValidPhoneNumber()) {
            _uiState.update { it.copy(showPhoneError = true) }
            isValid = false
        }
        
        // Validate shipping address
        if (_uiState.value.shippingStreet.isBlank()) {
            _uiState.update { it.copy(showShippingStreetError = true) }
            isValid = false
        }
        
        if (_uiState.value.shippingCity.isBlank()) {
            _uiState.update { it.copy(showShippingCityError = true) }
            isValid = false
        }
        
        if (_uiState.value.shippingState.isBlank()) {
            _uiState.update { it.copy(showShippingStateError = true) }
            isValid = false
        }
        
        if (_uiState.value.shippingZip.isBlank()) {
            _uiState.update { it.copy(showShippingZipError = true) }
            isValid = false
        }
        
        if (_uiState.value.shippingCountry.isBlank()) {
            _uiState.update { it.copy(showShippingCountryError = true) }
            isValid = false
        }
        
        // Validate billing address if not same as shipping
        if (!_uiState.value.sameAsBilling) {
            if (_uiState.value.billingStreet.isBlank()) {
                _uiState.update { it.copy(showBillingStreetError = true) }
                isValid = false
            }
            
            if (_uiState.value.billingCity.isBlank()) {
                _uiState.update { it.copy(showBillingCityError = true) }
                isValid = false
            }
            
            if (_uiState.value.billingState.isBlank()) {
                _uiState.update { it.copy(showBillingStateError = true) }
                isValid = false
            }
            
            if (_uiState.value.billingZip.isBlank()) {
                _uiState.update { it.copy(showBillingZipError = true) }
                isValid = false
            }
            
            if (_uiState.value.billingCountry.isBlank()) {
                _uiState.update { it.copy(showBillingCountryError = true) }
                isValid = false
            }
        }
        
        // Check if payment method is selected
        if (_uiState.value.paymentMethod.isBlank()) {
            _uiState.update { 
                it.copy(
                    errorMessage = "Please select a payment method."
                )
            }
            isValid = false
        }
        
        // Continue to payment if valid
        if (isValid) {
            // Save checkout data to shared ViewModel or SharedPreferences
            // ...
            
            // Navigate to payment screen
            _uiState.update { 
                it.copy(navigateToPayment = true)
            }
        } else {
            _uiState.update { 
                it.copy(
                    errorMessage = "Please fill in all required fields correctly."
                )
            }
        }
    }

    fun resetNavigationEvent() {
        _uiState.update { it.copy(navigateToPayment = false) }
    }
}
