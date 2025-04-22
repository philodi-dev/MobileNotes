package com.philodi.carbonium.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.philodi.carbonium.ui.screens.auth.CompleteProfileScreen
import com.philodi.carbonium.ui.screens.auth.PhoneAuthScreen
import com.philodi.carbonium.ui.screens.auth.SignInScreen
import com.philodi.carbonium.ui.screens.cart.CartScreen
import com.philodi.carbonium.ui.screens.catalog.ProductCatalogScreen
import com.philodi.carbonium.ui.screens.checkout.CheckoutScreen
import com.philodi.carbonium.ui.screens.checkout.PaymentConfirmationScreen
import com.philodi.carbonium.ui.screens.checkout.PaymentScreen
import com.philodi.carbonium.ui.screens.home.HomeScreen
import com.philodi.carbonium.ui.screens.onboarding.OnboardingScreen
import com.philodi.carbonium.ui.screens.orders.OrderDetailScreen
import com.philodi.carbonium.ui.screens.orders.OrderTrackingScreen
import com.philodi.carbonium.ui.screens.orders.OrdersScreen
import com.philodi.carbonium.ui.screens.product.ProductDetailScreen
import com.philodi.carbonium.ui.screens.profile.AddChildScreen
import com.philodi.carbonium.ui.screens.profile.ChildInfoScreen
import com.philodi.carbonium.ui.screens.profile.ChildrenScreen
import com.philodi.carbonium.ui.screens.profile.ProfileScreen
import com.philodi.carbonium.ui.screens.services.PricingPlanScreen
import com.philodi.carbonium.ui.screens.services.ServiceDetailsScreen
import com.philodi.carbonium.ui.screens.services.ServicesScreen
import com.philodi.carbonium.ui.screens.settings.SettingsScreen
import com.philodi.carbonium.ui.screens.splash.SplashScreen
import com.philodi.carbonium.ui.screens.subscription.NewSubscriptionScreen
import com.philodi.carbonium.ui.screens.subscription.SubscriptionDetailsScreen
import com.philodi.carbonium.ui.screens.subscription.SubscriptionsScreen
import kotlinx.coroutines.delay

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth flow
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.SignIn.route) {
            SignInScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onNavigateToPhoneAuth = {
                    navController.navigate(Screen.PhoneAuth.route)
                },
                onNavigateToCompleteProfile = {
                    navController.navigate(Screen.CompleteProfile.route)
                }
            )
        }
        
        composable(Screen.PhoneAuth.route) {
            PhoneAuthScreen(
                onNavigateToCompleteProfile = {
                    navController.navigate(Screen.CompleteProfile.route) {
                        popUpTo(Screen.PhoneAuth.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.CompleteProfile.route) {
            CompleteProfileScreen(
                onProfileCompleted = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.CompleteProfile.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Main flow
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProductCatalog = {
                    navController.navigate(Screen.ProductCatalog.route)
                },
                onNavigateToProductDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                },
                onNavigateToSubscriptions = {
                    navController.navigate(Screen.Subscriptions.route)
                },
                onNavigateToServices = {
                    navController.navigate(Screen.Services.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToOrders = {
                    navController.navigate(Screen.Orders.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        // Product flows
        composable(Screen.ProductCatalog.route) {
            ProductCatalogScreen(
                onNavigateToProductDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Cart and checkout flows
        composable(Screen.Cart.route) {
            CartScreen(
                onNavigateToCheckout = {
                    navController.navigate(Screen.Checkout.route)
                },
                onNavigateToContinueShopping = {
                    navController.navigate(Screen.ProductCatalog.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Checkout.route) {
            CheckoutScreen(
                onNavigateToPayment = {
                    navController.navigate(Screen.Payment.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Payment.route) {
            PaymentScreen(
                onPaymentSuccess = {
                    navController.navigate(Screen.PaymentConfirmation.route) {
                        popUpTo(Screen.Payment.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.PaymentConfirmation.route) {
            PaymentConfirmationScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Cart.route) { inclusive = true }
                    }
                },
                onNavigateToOrderTracking = { orderId ->
                    navController.navigate(Screen.OrderTracking.createRoute(orderId))
                }
            )
        }
        
        // Subscription flows
        composable(Screen.Subscriptions.route) {
            SubscriptionsScreen(
                onNavigateToSubscriptionDetails = { subscriptionId ->
                    navController.navigate(Screen.SubscriptionDetails.createRoute(subscriptionId))
                },
                onNavigateToNewSubscription = {
                    navController.navigate(Screen.NewSubscription.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.SubscriptionDetails.route,
            arguments = listOf(
                navArgument("subscriptionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val subscriptionId = backStackEntry.arguments?.getString("subscriptionId") ?: ""
            SubscriptionDetailsScreen(
                subscriptionId = subscriptionId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.NewSubscription.route) {
            NewSubscriptionScreen(
                onNavigateToServices = {
                    navController.navigate(Screen.Services.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Service flows
        composable(Screen.Services.route) {
            ServicesScreen(
                onNavigateToServiceDetails = { serviceId ->
                    navController.navigate(Screen.ServiceDetails.createRoute(serviceId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.ServiceDetails.route,
            arguments = listOf(
                navArgument("serviceId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            ServiceDetailsScreen(
                serviceId = serviceId,
                onNavigateToPricingPlan = {
                    navController.navigate(Screen.PricingPlan.createRoute(serviceId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.PricingPlan.route,
            arguments = listOf(
                navArgument("serviceId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            PricingPlanScreen(
                serviceId = serviceId,
                onSubscribe = {
                    navController.navigate(Screen.Payment.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Profile flows
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToChildren = {
                    navController.navigate(Screen.Children.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Children.route) {
            ChildrenScreen(
                onNavigateToAddChild = {
                    navController.navigate(Screen.AddChild.route)
                },
                onNavigateToChildInfo = { childId ->
                    navController.navigate(Screen.ChildInfo.createRoute(childId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.AddChild.route) {
            AddChildScreen(
                onChildAdded = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.ChildInfo.route,
            arguments = listOf(
                navArgument("childId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: ""
            ChildInfoScreen(
                childId = childId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Order flows
        composable(Screen.Orders.route) {
            OrdersScreen(
                onNavigateToOrderDetail = { orderId ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(
                orderId = orderId,
                onNavigateToOrderTracking = {
                    navController.navigate(Screen.OrderTracking.createRoute(orderId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.OrderTracking.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderTrackingScreen(
                orderId = orderId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Settings
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
