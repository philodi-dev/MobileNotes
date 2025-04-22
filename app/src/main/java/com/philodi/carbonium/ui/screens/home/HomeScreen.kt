package com.philodi.carbonium.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.philodi.carbonium.R
import com.philodi.carbonium.data.remote.model.Product
import com.philodi.carbonium.data.remote.model.Service
import com.philodi.carbonium.ui.components.BottomNavigation
import com.philodi.carbonium.ui.components.FullScreenError
import com.philodi.carbonium.ui.components.FullScreenLoading
import com.philodi.carbonium.ui.components.ProductItem
import com.philodi.carbonium.ui.components.SearchBar
import com.philodi.carbonium.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProductCatalog: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToSubscriptions: () -> Unit,
    onNavigateToServices: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartItemCount by viewModel.cartItemCount.collectAsState(initial = 0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = { /* Open search */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search)
                        )
                    }
                    
                    IconButton(onClick = onNavigateToCart) {
                        BadgedBox(
                            badge = {
                                if (cartItemCount > 0) {
                                    Badge { Text(text = cartItemCount.toString()) }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = stringResource(id = R.string.cart)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigation(
                onHomeClick = { /* Already on home */ },
                onCatalogClick = onNavigateToProductCatalog,
                onServicesClick = onNavigateToServices,
                onSubscriptionsClick = onNavigateToSubscriptions,
                onProfileClick = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> FullScreenLoading()
            uiState.errorMessage != null -> FullScreenError(
                message = uiState.errorMessage ?: "An error occurred",
                onRetry = viewModel::loadHomeData
            )
            else -> HomeContent(
                featuredProducts = uiState.featuredProducts,
                popularProducts = uiState.popularProducts,
                services = uiState.services,
                onProductClick = onNavigateToProductDetail,
                onServiceClick = { serviceId -> onNavigateToServices() },
                onViewAllProductsClick = onNavigateToProductCatalog,
                onViewAllServicesClick = onNavigateToServices,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun HomeContent(
    featuredProducts: List<Product>,
    popularProducts: List<Product>,
    services: List<Service>,
    onProductClick: (String) -> Unit,
    onServiceClick: (String) -> Unit,
    onViewAllProductsClick: () -> Unit,
    onViewAllServicesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search bar
            SearchBar(
                onSearch = { /* Handle search */ },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Featured products section
        item {
            SectionHeader(
                title = stringResource(id = R.string.featured),
                onViewAllClick = onViewAllProductsClick
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(featuredProducts) { product ->
                    ProductItem(
                        product = product,
                        onClick = { onProductClick(product.id) },
                        modifier = Modifier.padding(end = 16.dp, bottom = 8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Popular products section
        item {
            SectionHeader(
                title = stringResource(id = R.string.popular),
                onViewAllClick = onViewAllProductsClick
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(popularProducts) { product ->
                    ProductItem(
                        product = product,
                        onClick = { onProductClick(product.id) },
                        modifier = Modifier.padding(end = 16.dp, bottom = 8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Services section
        item {
            SectionHeader(
                title = stringResource(id = R.string.services),
                onViewAllClick = onViewAllServicesClick
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Service items would be displayed here
            // For now, just showing a placeholder
            
            Spacer(modifier = Modifier.height(80.dp)) // Extra space at bottom
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            androidx.compose.material3.TextButton(onClick = onViewAllClick) {
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
