package com.philodi.carbonium.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philodi.carbonium.data.remote.model.Address
import com.philodi.carbonium.data.remote.model.User
import com.philodi.carbonium.data.repository.UserRepository
import com.philodi.carbonium.util.Resource
import com.philodi.carbonium.util.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

// State for the complete profile screen
data class CompleteProfileUiState(
    val isLoading: Boolean = false,
    val name: String = "",
    val email: String = "",
    val dateOfBirth: Date? = null,
    val gender: String = "",
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = "",
    val showNameError: Boolean = false,
    val showEmailError: Boolean = false,
    val showDobError: Boolean = false,
    val showStreetError: Boolean = false,
    val showCityError: Boolean = false,
    val showStateError: Boolean = false,
    val showZipCodeError: Boolean = false,
    val showCountryError: Boolean = false,
    val errorMessage: String? = null,
    val isProfileCompleted: Boolean = false
)

@HiltViewModel
class CompleteProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompleteProfileUiState())
    val uiState: StateFlow<CompleteProfileUiState> = _uiState.asStateFlow()

    init {
        // Load current user data if available
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            userRepository.getCurrentUser().collect { result ->
                if (result is Resource.Success) {
                    result.data?.let { user ->
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                name = user.name,
                                email = user.email,
                                street = user.address?.street ?: "",
                                city = user.address?.city ?: "",
                                state = user.address?.state ?: "",
                                zipCode = user.address?.zipCode ?: "",
                                country = user.address?.country ?: ""
                            )
                        }
                    }
                } else if (result is Resource.Error) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    // Update name
    fun updateName(name: String) {
        _uiState.update { 
            it.copy(
                name = name,
                showNameError = false
            )
        }
    }

    // Update email
    fun updateEmail(email: String) {
        _uiState.update { 
            it.copy(
                email = email,
                showEmailError = false
            )
        }
    }

    // Update date of birth
    fun updateDateOfBirth(dateOfBirth: Date) {
        _uiState.update { 
            it.copy(
                dateOfBirth = dateOfBirth,
                showDobError = false
            )
        }
    }

    // Update gender
    fun updateGender(gender: String) {
        _uiState.update { it.copy(gender = gender) }
    }

    // Update street
    fun updateStreet(street: String) {
        _uiState.update { 
            it.copy(
                street = street,
                showStreetError = false
            )
        }
    }

    // Update city
    fun updateCity(city: String) {
        _uiState.update { 
            it.copy(
                city = city,
                showCityError = false
            )
        }
    }

    // Update state
    fun updateState(state: String) {
        _uiState.update { 
            it.copy(
                state = state,
                showStateError = false
            )
        }
    }

    // Update zip code
    fun updateZipCode(zipCode: String) {
        _uiState.update { 
            it.copy(
                zipCode = zipCode,
                showZipCodeError = false
            )
        }
    }

    // Update country
    fun updateCountry(country: String) {
        _uiState.update { 
            it.copy(
                country = country,
                showCountryError = false
            )
        }
    }

    // Validate inputs
    private fun validateInputs(): Boolean {
        var isValid = true
        
        if (_uiState.value.name.isBlank()) {
            _uiState.update { it.copy(showNameError = true) }
            isValid = false
        }
        
        if (_uiState.value.email.isBlank() || !_uiState.value.email.isValidEmail()) {
            _uiState.update { it.copy(showEmailError = true) }
            isValid = false
        }
        
        if (_uiState.value.street.isBlank()) {
            _uiState.update { it.copy(showStreetError = true) }
            isValid = false
        }
        
        if (_uiState.value.city.isBlank()) {
            _uiState.update { it.copy(showCityError = true) }
            isValid = false
        }
        
        if (_uiState.value.state.isBlank()) {
            _uiState.update { it.copy(showStateError = true) }
            isValid = false
        }
        
        if (_uiState.value.zipCode.isBlank()) {
            _uiState.update { it.copy(showZipCodeError = true) }
            isValid = false
        }
        
        if (_uiState.value.country.isBlank()) {
            _uiState.update { it.copy(showCountryError = true) }
            isValid = false
        }
        
        return isValid
    }

    // Save profile
    fun saveProfile() {
        if (!validateInputs()) {
            _uiState.update { 
                it.copy(errorMessage = "Please fill in all required fields correctly.")
            }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Create address object
            val address = Address(
                street = _uiState.value.street,
                city = _uiState.value.city,
                state = _uiState.value.state,
                zipCode = _uiState.value.zipCode,
                country = _uiState.value.country
            )
            
            // Get current user to update
            userRepository.getCurrentUser().collect { result ->
                if (result is Resource.Success) {
                    val currentUser = result.data ?: User()
                    
                    // Create updated user
                    val updatedUser = currentUser.copy(
                        name = _uiState.value.name,
                        email = _uiState.value.email,
                        address = address,
                        profileCompleted = true
                    )
                    
                    // Update user in repository
                    when (val updateResult = userRepository.updateUser(updatedUser)) {
                        is Resource.Success -> {
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    isProfileCompleted = true
                                )
                            }
                        }
                        is Resource.Error -> {
                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    errorMessage = updateResult.message
                                )
                            }
                        }
                        is Resource.Loading -> {
                            // Already set loading state
                        }
                    }
                    
                    // Only collect once
                    return@collect
                } else if (result is Resource.Error) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                    return@collect
                }
            }
        }
    }
}
