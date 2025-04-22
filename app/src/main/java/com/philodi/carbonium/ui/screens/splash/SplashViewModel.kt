package com.philodi.carbonium.ui.screens.splash

import androidx.lifecycle.ViewModel
import com.philodi.carbonium.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean = runBlocking {
        authRepository.isUserLoggedIn().first()
    }
}
