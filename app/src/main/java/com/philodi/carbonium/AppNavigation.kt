package com.philodi.carbonium

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.philodi.carbonium.ui.screens.home.HomeScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToProductCatalog = { navController.navigate("catalog") },
                onNavigateToProductDetail = { productId -> navController.navigate("product/$productId") },
                onNavigateToCart = { navController.navigate("cart") },
                onNavigateToSubscriptions = { navController.navigate("subscriptions") },
                onNavigateToServices = { navController.navigate("services") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToOrders = { navController.navigate("orders") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        
        // Add other screens navigation here
        // For example:
        
        /*
        composable("catalog") {
            ProductCatalogScreen(
                onNavigateToProductDetail = { productId -> navController.navigate("product/$productId") },
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        composable("product/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                onNavigateToCart = { navController.navigate("cart") },
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        composable("cart") {
            CartScreen(
                onNavigateToCheckout = { navController.navigate("checkout") },
                onNavigateToContinueShopping = { navController.navigate("catalog") },
                onNavigateBack = { navController.navigateUp() }
            )
        }
        */
    }
}