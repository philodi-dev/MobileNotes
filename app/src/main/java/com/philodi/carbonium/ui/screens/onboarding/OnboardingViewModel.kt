package com.philodi.carbonium.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {
    
    // In a real app, this would save to DataStore or SharedPreferences
    fun setOnboardingCompleted() {
        viewModelScope.launch(Dispatchers.IO) {
            // Save onboarding completion status
            // Example: dataStore.saveOnboardingCompleted(true)
        }
    }
}
