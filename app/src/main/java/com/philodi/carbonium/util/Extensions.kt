package com.philodi.carbonium.util

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Format price with currency symbol
 */
fun Double.formatAsCurrency(): String {
    val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
    return format.format(this)
}

/**
 * Format date as string with pattern
 */
fun Date.format(pattern: String = "dd MMM yyyy"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}

/**
 * Show toast message
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

/**
 * Throttle events (e.g. button clicks)
 */
fun <T> throttleFirst(periodMillis: Long = 300L, initialValue: T? = null): Flow<T> = flow {
    var lastEmissionTime = 0L
    var currentValue = initialValue
    
    while (true) {
        val currentTime = System.currentTimeMillis()
        if (currentValue != null && currentTime - lastEmissionTime > periodMillis) {
            lastEmissionTime = currentTime
            emit(currentValue!!)
            currentValue = null
        }
    }
}

/**
 * Composable to show a toast message from LaunchedEffect
 */
@Composable
fun ShowToastEffect(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    val context = LocalContext.current
    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, duration).show()
        }
    }
}

/**
 * Validate email format
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Validate phone number
 */
fun String.isValidPhoneNumber(): Boolean {
    return this.replace("[^0-9]".toRegex(), "").length >= 10
}
