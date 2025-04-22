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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.philodi.carbonium.R
import com.philodi.carbonium.ui.components.FullScreenError
import com.philodi.carbonium.ui.components.FullScreenLoading
import com.philodi.carbonium.ui.components.ShowToastEffect
import com.philodi.carbonium.util.formatAsCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onNavigateToPayment: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Load checkout data
    LaunchedEffect(Unit) {
        viewModel.loadCheckoutData()
    }

    // Show error messages
    ShowToastEffect(message = uiState.errorMessage)

    // Navigate to payment screen
    LaunchedEffect(uiState.navigateToPayment) {
        if (uiState.navigateToPayment) {
            onNavigateToPayment()
            viewModel.resetNavigationEvent()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.checkout)) },
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
                uiState.errorMessage != null -> FullScreenError(
                    message = uiState.errorMessage ?: "An error occurred",
                    onRetry = viewModel::loadCheckoutData
                )
                else -> CheckoutContent(
                    name = uiState.name,
                    email = uiState.email,
                    phone = uiState.phone,
                    shippingStreet = uiState.shippingStreet,
                    shippingCity = uiState.shippingCity,
                    shippingState = uiState.shippingState,
                    shippingZip = uiState.shippingZip,
                    shippingCountry = uiState.shippingCountry,
                    billingStreet = uiState.billingStreet,
                    billingCity = uiState.billingCity,
                    billingState = uiState.billingState,
                    billingZip = uiState.billingZip,
                    billingCountry = uiState.billingCountry,
                    sameAsBilling = uiState.sameAsBilling,
                    paymentMethod = uiState.paymentMethod,
                    subtotal = uiState.subtotal,
                    tax = uiState.tax,
                    shipping = uiState.shipping,
                    total = uiState.total,
                    showNameError = uiState.showNameError,
                    showEmailError = uiState.showEmailError,
                    showPhoneError = uiState.showPhoneError,
                    showShippingStreetError = uiState.showShippingStreetError,
                    showShippingCityError = uiState.showShippingCityError,
                    showShippingStateError = uiState.showShippingStateError,
                    showShippingZipError = uiState.showShippingZipError,
                    showShippingCountryError = uiState.showShippingCountryError,
                    showBillingStreetError = uiState.showBillingStreetError,
                    showBillingCityError = uiState.showBillingCityError,
                    showBillingStateError = uiState.showBillingStateError,
                    showBillingZipError = uiState.showBillingZipError,
                    showBillingCountryError = uiState.showBillingCountryError,
                    onNameChange = viewModel::updateName,
                    onEmailChange = viewModel::updateEmail,
                    onPhoneChange = viewModel::updatePhone,
                    onShippingStreetChange = viewModel::updateShippingStreet,
                    onShippingCityChange = viewModel::updateShippingCity,
                    onShippingStateChange = viewModel::updateShippingState,
                    onShippingZipChange = viewModel::updateShippingZip,
                    onShippingCountryChange = viewModel::updateShippingCountry,
                    onBillingStreetChange = viewModel::updateBillingStreet,
                    onBillingCityChange = viewModel::updateBillingCity,
                    onBillingStateChange = viewModel::updateBillingState,
                    onBillingZipChange = viewModel::updateBillingZip,
                    onBillingCountryChange = viewModel::updateBillingCountry,
                    onSameAsBillingChange = viewModel::updateSameAsBilling,
                    onPaymentMethodChange = viewModel::updatePaymentMethod,
                    onContinueClick = viewModel::validateAndContinue
                )
            }
        }
    }
}

@Composable
fun CheckoutContent(
    name: String,
    email: String,
    phone: String,
    shippingStreet: String,
    shippingCity: String,
    shippingState: String,
    shippingZip: String,
    shippingCountry: String,
    billingStreet: String,
    billingCity: String,
    billingState: String,
    billingZip: String,
    billingCountry: String,
    sameAsBilling: Boolean,
    paymentMethod: String,
    subtotal: Double,
    tax: Double,
    shipping: Double,
    total: Double,
    showNameError: Boolean,
    showEmailError: Boolean,
    showPhoneError: Boolean,
    showShippingStreetError: Boolean,
    showShippingCityError: Boolean,
    showShippingStateError: Boolean,
    showShippingZipError: Boolean,
    showShippingCountryError: Boolean,
    showBillingStreetError: Boolean,
    showBillingCityError: Boolean,
    showBillingStateError: Boolean,
    showBillingZipError: Boolean,
    showBillingCountryError: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onShippingStreetChange: (String) -> Unit,
    onShippingCityChange: (String) -> Unit,
    onShippingStateChange: (String) -> Unit,
    onShippingZipChange: (String) -> Unit,
    onShippingCountryChange: (String) -> Unit,
    onBillingStreetChange: (String) -> Unit,
    onBillingCityChange: (String) -> Unit,
    onBillingStateChange: (String) -> Unit,
    onBillingZipChange: (String) -> Unit,
    onBillingCountryChange: (String) -> Unit,
    onSameAsBillingChange: (Boolean) -> Unit,
    onPaymentMethodChange: (String) -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Contact Information
        Text(
            text = "Contact Information",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(text = stringResource(id = R.string.full_name)) },
            modifier = Modifier.fillMaxWidth(),
            isError = showNameError
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(text = stringResource(id = R.string.email)) },
            modifier = Modifier.fillMaxWidth(),
            isError = showEmailError
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text(text = stringResource(id = R.string.phone)) },
            modifier = Modifier.fillMaxWidth(),
            isError = showPhoneError
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Shipping Address
        Text(
            text = stringResource(id = R.string.shipping_address),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = shippingStreet,
            onValueChange = onShippingStreetChange,
            label = { Text(text = stringResource(id = R.string.address)) },
            modifier = Modifier.fillMaxWidth(),
            isError = showShippingStreetError
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = shippingCity,
                onValueChange = onShippingCityChange,
                label = { Text(text = stringResource(id = R.string.city)) },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                isError = showShippingCityError
            )
            
            OutlinedTextField(
                value = shippingState,
                onValueChange = onShippingStateChange,
                label = { Text(text = stringResource(id = R.string.state)) },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                isError = showShippingStateError
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = shippingZip,
                onValueChange = onShippingZipChange,
                label = { Text(text = stringResource(id = R.string.zip_code)) },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                isError = showShippingZipError
            )
            
            OutlinedTextField(
                value = shippingCountry,
                onValueChange = onShippingCountryChange,
                label = { Text(text = stringResource(id = R.string.country)) },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                isError = showShippingCountryError
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Billing Address
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.billing_address),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = sameAsBilling,
                    onCheckedChange = onSameAsBillingChange
                )
                
                Text(
                    text = stringResource(id = R.string.same_as_shipping),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (!sameAsBilling) {
            OutlinedTextField(
                value = billingStreet,
                onValueChange = onBillingStreetChange,
                label = { Text(text = stringResource(id = R.string.address)) },
                modifier = Modifier.fillMaxWidth(),
                isError = showBillingStreetError
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = billingCity,
                    onValueChange = onBillingCityChange,
                    label = { Text(text = stringResource(id = R.string.city)) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp),
                    isError = showBillingCityError
                )
                
                OutlinedTextField(
                    value = billingState,
                    onValueChange = onBillingStateChange,
                    label = { Text(text = stringResource(id = R.string.state)) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    isError = showBillingStateError
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = billingZip,
                    onValueChange = onBillingZipChange,
                    label = { Text(text = stringResource(id = R.string.zip_code)) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp),
                    isError = showBillingZipError
                )
                
                OutlinedTextField(
                    value = billingCountry,
                    onValueChange = onBillingCountryChange,
                    label = { Text(text = stringResource(id = R.string.country)) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    isError = showBillingCountryError
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Payment Method
        Text(
            text = stringResource(id = R.string.payment_method),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Credit Card option
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            RadioButton(
                selected = paymentMethod == "credit_card",
                onClick = { onPaymentMethodChange("credit_card") }
            )
            
            Text(
                text = stringResource(id = R.string.credit_card),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // PayPal option
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            RadioButton(
                selected = paymentMethod == "paypal",
                onClick = { onPaymentMethodChange("paypal") }
            )
            
            Text(
                text = stringResource(id = R.string.paypal),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Order Summary
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
                    text = stringResource(id = R.string.order_summary),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Subtotal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.subtotal),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Text(
                        text = subtotal.formatAsCurrency(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Tax
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.tax),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Text(
                        text = tax.formatAsCurrency(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Shipping
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.shipping),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Text(
                        text = shipping.formatAsCurrency(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Divider()
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Total
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
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Continue to Payment Button
        Button(
            onClick = onContinueClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Continue to Payment",
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
