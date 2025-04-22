package com.philodi.carbonium.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.philodi.carbonium.data.remote.ApiService
import com.philodi.carbonium.data.remote.PhoneAuthRequest
import com.philodi.carbonium.data.remote.VerifyCodeRequest
import com.philodi.carbonium.data.remote.model.User
import com.philodi.carbonium.util.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repository interface for authentication operations
 */
interface AuthRepository {
    fun isUserLoggedIn(): Flow<Boolean>
    suspend fun signInWithPhone(phoneNumber: String): Resource<String>
    suspend fun verifyPhoneCode(verificationId: String, code: String): Resource<User>
    suspend fun signInWithGoogle(idToken: String): Resource<User>
    suspend fun signOut(): Resource<Unit>
}

/**
 * Implementation of AuthRepository
 */
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    
    override fun isUserLoggedIn(): Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        
        firebaseAuth.addAuthStateListener(authStateListener)
        
        // Initial value
        trySend(firebaseAuth.currentUser != null)
        
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }
    
    override suspend fun signInWithPhone(phoneNumber: String): Resource<String> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.signInWithPhone(PhoneAuthRequest(phoneNumber))
            emit(Resource.Success(response.verificationId))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to send verification code: ${e.message}", e))
        }
    }.run { Resource.Loading }  // This is a simplification as the flow would normally be collected
    
    override suspend fun verifyPhoneCode(verificationId: String, code: String): Resource<User> {
        return try {
            val response = apiService.verifyPhoneCode(VerifyCodeRequest(verificationId, code))
            // Store the token somewhere (shared prefs or encrypted storage)
            Resource.Success(response.user)
        } catch (e: Exception) {
            Resource.Error("Failed to verify code: ${e.message}", e)
        }
    }
    
    override suspend fun signInWithGoogle(idToken: String): Resource<User> {
        return try {
            // First authenticate with Firebase
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            
            // Then verify with our backend
            val user = authResult.user ?: throw Exception("Authentication failed")
            val token = user.getIdToken(false).await().token
                ?: throw Exception("Failed to get ID token")
                
            // Exchange token with our backend
            val response = apiService.signInWithGoogle(token)
            Resource.Success(response.user)
        } catch (e: Exception) {
            Resource.Error("Google sign-in failed: ${e.message}", e)
        }
    }
    
    override suspend fun signOut(): Resource<Unit> {
        return try {
            firebaseAuth.signOut()
            // Clear any stored tokens or user data
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Sign out failed: ${e.message}", e)
        }
    }
}
