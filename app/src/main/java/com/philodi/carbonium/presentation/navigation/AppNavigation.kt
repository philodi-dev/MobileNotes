package com.philodi.carbonium.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.philodi.carbonium.presentation.screens.auth.LoginScreen
import com.philodi.carbonium.presentation.screens.auth.RegisterScreen
import com.philodi.carbonium.presentation.screens.cart.CartScreen
import com.philodi.carbonium.presentation.screens.checkout.CheckoutScreen
import com.philodi.carbonium.presentation.screens.home.HomeScreen
import com.philodi.carbonium.presentation.screens.product.ProductDetailScreen
import com.philodi.carbonium.presentation.screens.product.ProductListScreen
import com.philodi.carbonium.presentation.screens.subscription.SubscriptionManagementScreen
import com.philodi.carbonium.presentation.screens.subscription.SubscriptionPlansScreen
import com.philodi.carbonium.presentation.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavigationRoute.Home.route,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = if (authState.isLoggedIn) startDestination else NavigationRoute.Login.route
    ) {
        // Auth screens
        composable(NavigationRoute.Login.route) {
            LoginScreen(
                navigateToRegister = {
                    navController.navigate(NavigationRoute.Register.route)
                },
                navigateToHome = {
                    navController.navigate(NavigationRoute.Home.route) {
                        popUpTo(NavigationRoute.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(NavigationRoute.Register.route) {
            RegisterScreen(
                navigateToLogin = {
                    navController.navigate(NavigationRoute.Login.route) {
                        popUpTo(NavigationRoute.Register.route) { inclusive = true }
                    }
                },
                navigateToHome = {
                    navController.navigate(NavigationRoute.Home.route) {
                        popUpTo(NavigationRoute.Register.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Main screens
        composable(NavigationRoute.Home.route) {
            HomeScreen(
                navigateToProductList = { category ->
                    navController.navigate("${NavigationRoute.ProductList.route}?category=$category")
                },
                navigateToCart = {
                    navController.navigate(NavigationRoute.Cart.route)
                },
                navigateToSubscription = {
                    navController.navigate(NavigationRoute.SubscriptionPlans.route)
                },
                navigateToLogin = {
                    navController.navigate(NavigationRoute.Login.route) {
                        popUpTo(NavigationRoute.Home.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(
            route = "${NavigationRoute.ProductList.route}?category={category}",
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category")
            ProductListScreen(
                category = category,
                navigateToProductDetail = { productId ->
                    navController.navigate("${NavigationRoute.ProductDetail.route}/$productId")
                },
                navigateUp = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(
            route = "${NavigationRoute.ProductDetail.route}/{productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                navigateToCart = {
                    navController.navigate(NavigationRoute.Cart.route)
                },
                navigateUp = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(NavigationRoute.Cart.route) {
            CartScreen(
                navigateToCheckout = {
                    navController.navigate(NavigationRoute.Checkout.route)
                },
                navigateToProductDetail = { productId ->
                    navController.navigate("${NavigationRoute.ProductDetail.route}/$productId")
                },
                navigateUp = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(NavigationRoute.Checkout.route) {
            CheckoutScreen(
                navigateToOrderConfirmation = { orderId ->
                    navController.navigate("${NavigationRoute.OrderDetail.route}/$orderId") {
                        popUpTo(NavigationRoute.Cart.route) { inclusive = true }
                    }
                },
                navigateUp = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(
            route = "${NavigationRoute.OrderDetail.route}/{orderId}",
            arguments = listOf(
                navArgument("orderId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            // OrderDetailScreen not yet implemented
            // For now, just redirect to home
            navController.navigate(NavigationRoute.Home.route) {
                popUpTo(NavigationRoute.Home.route) { inclusive = true }
            }
        }
        
        composable(NavigationRoute.SubscriptionPlans.route) {
            SubscriptionPlansScreen(
                navigateToSubscriptionManagement = { subscriptionId ->
                    navController.navigate("${NavigationRoute.SubscriptionManagement.route}/$subscriptionId")
                },
                navigateUp = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(
            route = "${NavigationRoute.SubscriptionManagement.route}/{subscriptionId}",
            arguments = listOf(
                navArgument("subscriptionId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val subscriptionId = backStackEntry.arguments?.getString("subscriptionId") ?: ""
            SubscriptionManagementScreen(
                subscriptionId = subscriptionId,
                navigateUp = {
                    navController.navigateUp()
                }
            )
        }
    }
}

sealed class NavigationRoute(val route: String) {
    object Login : NavigationRoute("login")
    object Register : NavigationRoute("register")
    object Home : NavigationRoute("home")
    object ProductList : NavigationRoute("products")
    object ProductDetail : NavigationRoute("product")
    object Cart : NavigationRoute("cart")
    object Checkout : NavigationRoute("checkout")
    object OrderDetail : NavigationRoute("order")
    object SubscriptionPlans : NavigationRoute("subscription-plans")
    object SubscriptionManagement : NavigationRoute("subscription")
}