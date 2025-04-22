package com.philodi.carbonium.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.philodi.carbonium.data.model.ProductCategory
import com.philodi.carbonium.presentation.viewmodel.AuthViewModel
import com.philodi.carbonium.presentation.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToProductList: (String?) -> Unit,
    navigateToCart: () -> Unit,
    navigateToSubscription: () -> Unit,
    navigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val productsState by productViewModel.uiState.collectAsState()
    
    var showMenu by remember { mutableStateOf(false) }
    
    LaunchedEffect(key1 = Unit) {
        productViewModel.loadProducts()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carbonium") },
                actions = {
                    IconButton(onClick = navigateToCart) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.Person, contentDescription = "Account")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        if (authState.isLoggedIn) {
                            DropdownMenuItem(
                                text = { Text("My Account") },
                                onClick = { 
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("My Orders") },
                                onClick = { 
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Subscriptions") },
                                onClick = { 
                                    showMenu = false
                                    navigateToSubscription()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = { 
                                    authViewModel.logout()
                                    showMenu = false
                                    navigateToLogin()
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Login") },
                                onClick = { 
                                    showMenu = false
                                    navigateToLogin()
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                // Welcome section with user name if logged in
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (authState.isLoggedIn && authState.user != null) {
                            Text(
                                text = "Welcome back, ${authState.user?.firstName}!",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        } else {
                            Text(
                                text = "Welcome to Carbonium",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = navigateToLogin) {
                                Text("Login / Register")
                            }
                        }
                    }
                }
            }
            
            item {
                // Categories section
                Text(
                    text = "Shop by Category",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ProductCategory.values()) { category ->
                        CategoryItem(
                            category = category,
                            onClick = { navigateToProductList(category.name) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                // Featured products section
                Text(
                    text = "Featured Products",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            if (productsState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (productsState.error != null) {
                item {
                    Text(
                        text = productsState.error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            } else {
                // Show featured products (we'll just take the first few)
                val featuredProducts = productsState.products.take(6)
                items(featuredProducts.chunked(2)) { rowProducts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowProducts.forEach { product ->
                            ProductItem(
                                product = product,
                                onClick = { navigateToProductList(null) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        // If there's only one product in the row, add an empty space
                        if (rowProducts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            item {
                // Subscription promotion
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Join our Premium Membership",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Subscribe to get exclusive deals, free shipping, and more!",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = navigateToSubscription) {
                            Text("View Plans")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}