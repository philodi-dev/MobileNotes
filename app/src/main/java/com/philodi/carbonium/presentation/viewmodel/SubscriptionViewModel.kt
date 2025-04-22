package com.philodi.carbonium.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.philodi.carbonium.data.model.*
import com.philodi.carbonium.data.repository.FakeDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val fakeDataRepository: FakeDataRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SubscriptionUiState())
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()
    
    init {
        loadSubscriptionPlans()
    }
    
    fun loadSubscriptionPlans() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Generate a list of subscription plans
                val plans = (1..4).map { fakeDataRepository.generateSubscriptionPlan() }
                
                _uiState.update { 
                    it.copy(
                        subscriptionPlans = plans,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load subscription plans: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    fun loadUserSubscription(userId: String?) {
        if (userId == null) {
            _uiState.update { it.copy(error = "User ID is required to load subscription") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // For demo purposes, we'll generate a subscription about 70% of the time
                // and return null the rest of the time to simulate a user without a subscription
                val hasSubscription = Math.random() < 0.7
                
                if (hasSubscription) {
                    val subscription = fakeDataRepository.generateSubscription()
                    _uiState.update { 
                        it.copy(
                            userSubscription = subscription,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            userSubscription = null,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load user subscription: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    fun subscribeToNewPlan(userId: String?, plan: SubscriptionPlan, paymentMethod: PaymentMethod, billingAddress: Address) {
        if (userId == null) {
            _uiState.update { it.copy(error = "User ID is required to subscribe") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Simulate network delay
                delay(1500)
                
                // Create a new subscription
                val now = Date()
                
                val daysToAdd = when (plan.billingCycle) {
                    BillingCycle.MONTHLY -> 30L
                    BillingCycle.QUARTERLY -> 90L
                    BillingCycle.BIANNUALLY -> 180L
                    BillingCycle.ANNUALLY -> 365L
                }
                
                val nextBillingDate = Date(now.time + TimeUnit.DAYS.toMillis(daysToAdd))
                
                val newSubscription = Subscription(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    plan = plan,
                    status = SubscriptionStatus.ACTIVE,
                    startDate = now,
                    nextBillingDate = nextBillingDate,
                    paymentMethod = paymentMethod,
                    autoRenew = true,
                    billingAddress = billingAddress
                )
                
                _uiState.update { 
                    it.copy(
                        userSubscription = newSubscription,
                        isLoading = false,
                        error = null,
                        message = "Subscribed to ${plan.name} successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to subscribe: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    fun cancelSubscription(subscriptionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Simulate network delay
                delay(1000)
                
                val currentSubscription = _uiState.value.userSubscription ?: throw Exception("No active subscription found")
                
                if (currentSubscription.id != subscriptionId) {
                    throw Exception("Subscription ID mismatch")
                }
                
                // Update the subscription status to cancelled
                val updatedSubscription = currentSubscription.copy(
                    status = SubscriptionStatus.CANCELED
                )
                
                _uiState.update { 
                    it.copy(
                        userSubscription = updatedSubscription,
                        isLoading = false,
                        error = null,
                        message = "Subscription cancelled successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to cancel subscription: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    fun toggleAutoRenew(subscriptionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Simulate network delay
                delay(500)
                
                val currentSubscription = _uiState.value.userSubscription ?: throw Exception("No active subscription found")
                
                if (currentSubscription.id != subscriptionId) {
                    throw Exception("Subscription ID mismatch")
                }
                
                // Toggle the auto-renew setting
                val updatedSubscription = currentSubscription.copy(
                    autoRenew = !currentSubscription.autoRenew
                )
                
                val message = if (updatedSubscription.autoRenew) {
                    "Auto-renew enabled for your subscription"
                } else {
                    "Auto-renew disabled for your subscription"
                }
                
                _uiState.update { 
                    it.copy(
                        userSubscription = updatedSubscription,
                        isLoading = false,
                        error = null,
                        message = message
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to update auto-renew setting: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
    
    fun loadSampleSubscription() {
        viewModelScope.launch {
            try {
                val sampleSubscription = fakeDataRepository.generateSubscription()
                _uiState.update { 
                    it.copy(
                        userSubscription = sampleSubscription,
                        message = "Sample subscription loaded"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to load sample subscription: ${e.localizedMessage}"
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

data class SubscriptionUiState(
    val subscriptionPlans: List<SubscriptionPlan> = emptyList(),
    val userSubscription: Subscription? = null,
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)