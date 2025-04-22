package com.philodi.carbonium.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philodi.carbonium.data.repository.AuthRepository
import com.philodi.carbonium.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Navigation events
sealed class SignInNavigationEvent {
    object NavigateToHome : SignInNavigationEvent()
    object NavigateToPhoneAuth : SignInNavigationEvent()
    object NavigateToCompleteProfile : SignInNavigationEvent()
}

// State for the sign-in screen
data class SignInUiState(
    val isLoading: Boolean = false,
    val googleSignInStatus: Resource<Unit>? = null,
    val errorMessage: String? = null,
    val navigationEvent: SignInNavigationEvent? = null
)

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    init {
        // Check if user is already logged in
        viewModelScope.launch {
            authRepository.isUserLoggedIn().collect { isLoggedIn ->
                if (isLoggedIn) {
                    _uiState.update { it.copy(navigationEvent = SignInNavigationEvent.NavigateToHome) }
                }
            }
        }
    }

    // Handle Google Sign In
    fun signInWithGoogle() {
        _uiState.update { it.copy(isLoading = true) }
        
        // In a real app, this would involve the Google Sign-In API
        // For now, we'll simulate the process
        viewModelScope.launch {
            // This is a placeholder - in a real app, you'd get the ID token from Google Sign-In API
            val dummyIdToken = "dummy_token" 
            
            when (val result = authRepository.signInWithGoogle(dummyIdToken)) {
                is Resource.Success -> {
                    val user = result.data
                    if (user.profileCompleted) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                navigationEvent = SignInNavigationEvent.NavigateToHome
                            )
                        }
                    } else {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                navigationEvent = SignInNavigationEvent.NavigateToCompleteProfile
                            )
                        }
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
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    // Navigate to phone authentication
    fun navigateToPhoneAuth() {
        _uiState.update { it.copy(navigationEvent = SignInNavigationEvent.NavigateToPhoneAuth) }
    }

    // Reset navigation event after handling
    fun resetNavigationEvent() {
        _uiState.update { it.copy(navigationEvent = null) }
    }
}
