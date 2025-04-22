package com.philodi.carbonium.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philodi.carbonium.data.model.User
import com.philodi.carbonium.data.repository.FakeDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val fakeDataRepository: FakeDataRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        // Check if there's a logged-in user (in a real app, this would check SharedPreferences or a local DB)
        checkLoggedInUser()
    }
    
    private fun checkLoggedInUser() {
        // In a real app, this would check for a saved user token/session
        // For now, we'll start with a logged-out state
        _uiState.update { it.copy(isLoading = false, isLoggedIn = false) }
    }
    
    fun login(email: String, password: String) {
        // Validate inputs
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Email and password are required") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Simulate network delay
                delay(1000)
                
                // In a real app, this would call an API
                // For demo purposes, we'll just accept any email/password combo
                // and generate a fake user
                val generatedUser = if (email.contains("@")) {
                    // Use the email to create a semi-realistic user
                    val nameParts = email.split("@")[0].split(".")
                    val firstName = nameParts.getOrNull(0)?.capitalize() ?: "User"
                    val lastName = nameParts.getOrNull(1)?.capitalize() ?: "Account"
                    
                    User(
                        id = UUID.randomUUID().toString(),
                        email = email,
                        firstName = firstName,
                        lastName = lastName
                    )
                } else {
                    // Just use a fully generated user
                    fakeDataRepository.generateUser().copy(email = email)
                }
                
                _uiState.update { 
                    it.copy(
                        user = generatedUser,
                        isLoggedIn = true,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Login failed: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    fun register(email: String, password: String, firstName: String, lastName: String) {
        // Validate inputs
        if (email.isBlank() || password.isBlank() || firstName.isBlank() || lastName.isBlank()) {
            _uiState.update { it.copy(error = "All fields are required") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Simulate network delay
                delay(1500)
                
                // In a real app, this would call an API
                // For demo purposes, we'll just create a new user
                val newUser = User(
                    id = UUID.randomUUID().toString(),
                    email = email,
                    firstName = firstName,
                    lastName = lastName
                )
                
                _uiState.update { 
                    it.copy(
                        user = newUser,
                        isLoggedIn = true,
                        isLoading = false,
                        error = null,
                        message = "Account created successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Registration failed: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Simulate network delay
            delay(500)
            
            // In a real app, this would clear tokens and call a logout API endpoint
            _uiState.update { 
                it.copy(
                    user = null,
                    isLoggedIn = false,
                    isLoading = false,
                    message = "Logged out successfully"
                )
            }
        }
    }
    
    fun loadSampleUser() {
        viewModelScope.launch {
            try {
                val sampleUser = fakeDataRepository.generateUser()
                _uiState.update { 
                    it.copy(
                        user = sampleUser,
                        isLoggedIn = true,
                        message = "Sample user loaded"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to load sample user: ${e.localizedMessage}"
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

data class AuthUiState(
    val user: User? = null,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = true,
    val message: String? = null,
    val error: String? = null
)

// Extension function to capitalize first letter of a string
private fun String.capitalize(): String {
    return this.replaceFirstChar { it.uppercase() }
}