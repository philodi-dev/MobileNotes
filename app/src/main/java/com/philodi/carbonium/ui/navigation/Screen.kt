package com.philodi.carbonium.ui.navigation

/**
 * Screen route definitions for navigation
 */
sealed class Screen(val route: String) {
    // Authentication flows
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object SignIn : Screen("sign_in")
    object PhoneAuth : Screen("phone_auth")
    object CompleteProfile : Screen("complete_profile")
    
    // Main navigation
    object Home : Screen("home")
    
    // Product screens
    object ProductCatalog : Screen("product_catalog")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    
    // Cart and checkout flows
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object Payment : Screen("payment")
    object PaymentConfirmation : Screen("payment_confirmation")
    
    // Subscription flows
    object Subscriptions : Screen("subscriptions")
    object SubscriptionDetails : Screen("subscription_details/{subscriptionId}") {
        fun createRoute(subscriptionId: String) = "subscription_details/$subscriptionId"
    }
    object NewSubscription : Screen("new_subscription")
    
    // Service screens
    object Services : Screen("services")
    object ServiceDetails : Screen("service_details/{serviceId}") {
        fun createRoute(serviceId: String) = "service_details/$serviceId"
    }
    object PricingPlan : Screen("pricing_plan/{serviceId}") {
        fun createRoute(serviceId: String) = "pricing_plan/$serviceId"
    }
    
    // Profile and settings
    object Profile : Screen("profile")
    object Children : Screen("children")
    object AddChild : Screen("add_child")
    object ChildInfo : Screen("child_info/{childId}") {
        fun createRoute(childId: String) = "child_info/$childId"
    }
    
    // Orders
    object Orders : Screen("orders")
    object OrderDetail : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: String) = "order_detail/$orderId"
    }
    object OrderTracking : Screen("order_tracking/{orderId}") {
        fun createRoute(orderId: String) = "order_tracking/$orderId"
    }
    
    // Settings
    object Settings : Screen("settings")
}
