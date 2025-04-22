package com.philodi.carbonium.ui.screens.auth

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.philodi.carbonium.R
import com.philodi.carbonium.ui.components.FullScreenLoading
import com.philodi.carbonium.ui.components.ShowToastEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneAuthScreen(
    onNavigateToCompleteProfile: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PhoneAuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle navigation based on auth state
    LaunchedEffect(uiState.navigationEvent) {
        uiState.navigationEvent?.let { event ->
            when (event) {
                PhoneAuthNavigationEvent.NavigateToCompleteProfile -> onNavigateToCompleteProfile()
                PhoneAuthNavigationEvent.NavigateBack -> onNavigateBack()
            }
            // Reset navigation event after handling
            viewModel.resetNavigationEvent()
        }
    }

    // Show error messages
    ShowToastEffect(message = uiState.errorMessage)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.sign_in_with_phone)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                FullScreenLoading()
            } else {
                if (uiState.verificationId == null) {
                    // Phone input screen
                    PhoneInputContent(
                        phoneNumber = uiState.phoneNumber,
                        onPhoneNumberChange = viewModel::updatePhoneNumber,
                        onContinueClick = viewModel::sendVerificationCode,
                        isPhoneValid = uiState.isPhoneValid
                    )
                } else {
                    // Verification code input screen
                    VerificationCodeContent(
                        verificationCode = uiState.verificationCode,
                        onVerificationCodeChange = viewModel::updateVerificationCode,
                        onVerifyClick = viewModel::verifyPhoneCode,
                        onResendClick = viewModel::resendVerificationCode,
                        isVerificationCodeValid = uiState.isVerificationCodeValid
                    )
                }
            }
        }
    }
}

@Composable
fun PhoneInputContent(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    onContinueClick: () -> Unit,
    isPhoneValid: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "Enter your phone number",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Description
        Text(
            text = "We'll send you a verification code to confirm your identity",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Phone input field
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text(text = stringResource(id = R.string.phone_number)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = phoneNumber.isNotEmpty() && !isPhoneValid,
            supportingText = {
                if (phoneNumber.isNotEmpty() && !isPhoneValid) {
                    Text(
                        text = "Please enter a valid phone number",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Continue button
        Button(
            onClick = onContinueClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = isPhoneValid
        ) {
            Text(text = stringResource(id = R.string.continue_text))
        }
    }
}

@Composable
fun VerificationCodeContent(
    verificationCode: String,
    onVerificationCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit,
    isVerificationCodeValid: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = stringResource(id = R.string.verify_code),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Description
        Text(
            text = "Enter the verification code we sent to your phone",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Verification code input field
        OutlinedTextField(
            value = verificationCode,
            onValueChange = onVerificationCodeChange,
            label = { Text(text = stringResource(id = R.string.verification_code)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = verificationCode.isNotEmpty() && !isVerificationCodeValid,
            supportingText = {
                if (verificationCode.isNotEmpty() && !isVerificationCodeValid) {
                    Text(
                        text = "Please enter a valid verification code",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Resend code button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onResendClick) {
                Text(text = stringResource(id = R.string.resend_code))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Verify button
        Button(
            onClick = onVerifyClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = isVerificationCodeValid
        ) {
            Text(text = stringResource(id = R.string.verify_code))
        }
    }
}
