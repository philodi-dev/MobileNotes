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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.philodi.carbonium.R
import com.philodi.carbonium.ui.components.FullScreenLoading
import com.philodi.carbonium.ui.components.ShowToastEffect
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(
    onProfileCompleted: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CompleteProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle navigation
    LaunchedEffect(uiState.isProfileCompleted) {
        if (uiState.isProfileCompleted) {
            onProfileCompleted()
        }
    }

    // Show error messages
    ShowToastEffect(message = uiState.errorMessage)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.complete_profile)) },
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Name field
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = viewModel::updateName,
                        label = { Text(text = stringResource(id = R.string.full_name)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        isError = uiState.showNameError
                    )
                    
                    // Email field
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::updateEmail,
                        label = { Text(text = stringResource(id = R.string.email)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        isError = uiState.showEmailError
                    )

                    // Date of Birth field - in a real app would use a date picker
                    var showDatePicker by remember { mutableStateOf(false) }
                    
                    OutlinedTextField(
                        value = uiState.dateOfBirth?.let { 
                            SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(it) 
                        } ?: "",
                        onValueChange = { /* Date picked from dialog */ },
                        label = { Text(text = stringResource(id = R.string.dob)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        readOnly = true,
                        isError = uiState.showDobError
                    )
                    
                    // Gender selection
                    Text(
                        text = stringResource(id = R.string.gender),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val genderOptions = listOf(
                            stringResource(id = R.string.male),
                            stringResource(id = R.string.female),
                            stringResource(id = R.string.other)
                        )
                        
                        genderOptions.forEach { gender ->
                            Row(
                                modifier = Modifier
                                    .selectable(
                                        selected = uiState.gender == gender,
                                        onClick = { viewModel.updateGender(gender) },
                                        role = Role.RadioButton
                                    )
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = uiState.gender == gender,
                                    onClick = null // Handled by selectable modifier
                                )
                                Text(
                                    text = gender,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                    
                    // Address fields
                    OutlinedTextField(
                        value = uiState.street,
                        onValueChange = viewModel::updateStreet,
                        label = { Text(text = stringResource(id = R.string.address)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        isError = uiState.showStreetError
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.city,
                            onValueChange = viewModel::updateCity,
                            label = { Text(text = stringResource(id = R.string.city)) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            isError = uiState.showCityError
                        )
                        
                        OutlinedTextField(
                            value = uiState.state,
                            onValueChange = viewModel::updateState,
                            label = { Text(text = stringResource(id = R.string.state)) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            isError = uiState.showStateError
                        )
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.zipCode,
                            onValueChange = viewModel::updateZipCode,
                            label = { Text(text = stringResource(id = R.string.zip_code)) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            isError = uiState.showZipCodeError
                        )
                        
                        OutlinedTextField(
                            value = uiState.country,
                            onValueChange = viewModel::updateCountry,
                            label = { Text(text = stringResource(id = R.string.country)) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            isError = uiState.showCountryError
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Save button
                    Button(
                        onClick = viewModel::saveProfile,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.save),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
