package com.philodi.carbonium.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philodi.carbonium.data.repository.AuthRepository
import com.philodi.carbonium.util.Resource
import com.philodi.carbonium.util.isValidPhoneNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Navigation events
sealed class PhoneAuthNavigationEvent {
    object NavigateToCompleteProfile : PhoneAuthNavigationEvent()
    object NavigateBack : PhoneAuthNavigationEvent()
}

// State for the phone auth screen
data class PhoneAuthUiState(
    val isLoading: Boolean = false,
    val phoneNumber: String = "",
    val isPhoneValid: Boolean = false,
    val verificationId: String? = null,
    val verificationCode: String = "",
    val isVerificationCodeValid: Boolean = false,
    val errorMessage: String? = null,
    val navigationEvent: PhoneAuthNavigationEvent? = null
)

@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhoneAuthUiState())
    val uiState: StateFlow<PhoneAuthUiState> = _uiState.asStateFlow()

    // Update phone number
    fun updatePhoneNumber(phoneNumber: String) {
        _uiState.update { 
            it.copy(
                phoneNumber = phoneNumber,
                isPhoneValid = phoneNumber.isValidPhoneNumber()
            )
        }
    }

    // Update verification code
    fun updateVerificationCode(code: String) {
        _uiState.update { 
            it.copy(
                verificationCode = code,
                isVerificationCodeValid = code.length == 6 && code.all { c -> c.isDigit() }
            )
        }
    }

    // Send verification code
    fun sendVerificationCode() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = authRepository.signInWithPhone(_uiState.value.phoneNumber)) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            verificationId = result.data
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
    }

    // Resend verification code
    fun resendVerificationCode() {
        sendVerificationCode()
    }

    // Verify phone code
    fun verifyPhoneCode() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val verificationId = _uiState.value.verificationId
            if (verificationId == null) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Verification ID is missing. Please try again."
                    )
                }
                return@launch
            }
            
            when (val result = authRepository.verifyPhoneCode(
                verificationId, 
                _uiState.value.verificationCode
            )) {
                is Resource.Success -> {
                    val user = result.data
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            navigationEvent = PhoneAuthNavigationEvent.NavigateToCompleteProfile
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
    }

    // Reset navigation event after handling
    fun resetNavigationEvent() {
        _uiState.update { it.copy(navigationEvent = null) }
    }
}
