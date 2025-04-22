package com.philodi.carbonium.ui.screens.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.philodi.carbonium.R
import com.philodi.carbonium.ui.components.FullScreenError
import com.philodi.carbonium.ui.components.FullScreenLoading
import com.philodi.carbonium.ui.components.ShowToastEffect
import com.philodi.carbonium.util.formatAsCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onPaymentSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // State for payment form
    val paymentUiState by viewModel.paymentUiState.collectAsState()

    // Navigate to payment confirmation screen
    LaunchedEffect(paymentUiState.paymentSuccessful) {
        if (paymentUiState.paymentSuccessful) {
            onPaymentSuccess()
        }
    }

    // Show error messages
    ShowToastEffect(message = paymentUiState.errorMessage ?: uiState.errorMessage)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.payment)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> FullScreenLoading()
                paymentUiState.isLoading -> FullScreenLoading()
                paymentUiState.errorMessage != null -> FullScreenError(
                    message = paymentUiState.errorMessage ?: "An error occurred",
                    onRetry = viewModel::resetPaymentError
                )
                else -> PaymentContent(
                    cardNumber = paymentUiState.cardNumber,
                    cardHolder = paymentUiState.cardHolder,
                    expiryDate = paymentUiState.expiryDate,
                    cvv = paymentUiState.cvv,
                    total = uiState.total,
                    showCardNumberError = paymentUiState.showCardNumberError,
                    showCardHolderError = paymentUiState.showCardHolderError,
                    showExpiryDateError = paymentUiState.showExpiryDateError,
                    showCvvError = paymentUiState.showCvvError,
                    isProcessingPayment = paymentUiState.isProcessingPayment,
                    onCardNumberChange = viewModel::updateCardNumber,
                    onCardHolderChange = viewModel::updateCardHolder,
                    onExpiryDateChange = viewModel::updateExpiryDate,
                    onCvvChange = viewModel::updateCvv,
                    onPayClick = viewModel::processPayment
                )
            }
        }
    }
}

@Composable
fun PaymentContent(
    cardNumber: String,
    cardHolder: String,
    expiryDate: String,
    cvv: String,
    total: Double,
    showCardNumberError: Boolean,
    showCardHolderError: Boolean,
    showExpiryDateError: Boolean,
    showCvvError: Boolean,
    isProcessingPayment: Boolean,
    onCardNumberChange: (String) -> Unit,
    onCardHolderChange: (String) -> Unit,
    onExpiryDateChange: (String) -> Unit,
    onCvvChange: (String) -> Unit,
    onPayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Card details form
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Card Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Card number
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = onCardNumberChange,
                    label = { Text(text = stringResource(id = R.string.card_number)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = showCardNumberError,
                    supportingText = {
                        if (showCardNumberError) {
                            Text(
                                text = "Please enter a valid card number",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Card holder
                OutlinedTextField(
                    value = cardHolder,
                    onValueChange = onCardHolderChange,
                    label = { Text(text = stringResource(id = R.string.card_holder)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    isError = showCardHolderError,
                    supportingText = {
                        if (showCardHolderError) {
                            Text(
                                text = "Please enter the card holder name",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Expiry date
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = onExpiryDateChange,
                        label = { Text(text = stringResource(id = R.string.expiry_date)) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        isError = showExpiryDateError,
                        supportingText = {
                            if (showExpiryDateError) {
                                Text(
                                    text = "Invalid format (MM/YY)",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        placeholder = { Text(text = "MM/YY") }
                    )
                    
                    // CVV
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = onCvvChange,
                        label = { Text(text = stringResource(id = R.string.cvv)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        isError = showCvvError,
                        supportingText = {
                            if (showCvvError) {
                                Text(
                                    text = "Invalid CVV",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Order summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Payment Summary",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.total),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = total.formatAsCurrency(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Divider()
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "Secure payment",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Pay button
        Button(
            onClick = onPayClick,
            enabled = !isProcessingPayment,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (isProcessingPayment) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(id = R.string.pay_now) + " - " + total.formatAsCurrency(),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Payment security notice
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = "Your payment information is secure. We use encryption to protect your data.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
