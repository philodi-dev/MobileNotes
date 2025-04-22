# Carbonium - Android E-commerce and Subscription App

## Architecture Overview

Carbonium is an Android e-commerce application built with Kotlin and Jetpack Compose, following the MVVM (Model-View-ViewModel) architecture. The app includes features for user authentication, product browsing, shopping cart management, checkout process, and subscription management.

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Navigation**: Jetpack Navigation Compose
- **Networking**: Retrofit + OkHttp
- **Local Storage**: Room Database
- **Image Loading**: Coil
- **Test Data Generation**: JavaFaker

## Project Structure

The project follows a clean architecture approach with the following package structure:

```
com.philodi.carbonium/
├── data/
│   ├── model/         # Data models/entities
│   ├── repository/    # Repository implementations
│   ├── local/         # Local data sources (Room Database)
│   └── remote/        # Remote data sources (Retrofit APIs)
├── di/                # Dependency injection modules
├── domain/
│   ├── repository/    # Repository interfaces
│   └── usecase/       # Business logic use cases
├── presentation/
│   ├── navigation/    # Navigation setup
│   ├── screens/       # UI screens organized by feature
│   └── viewmodel/     # ViewModels for each feature
├── ui/
│   ├── theme/         # App theme, colors, typography
│   └── components/    # Reusable UI components
└── util/              # Utility classes and extensions
```

## Data Models

### Product

```kotlin
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val category: ProductCategory,
    val imageUrl: String,
    val attributes: Map<String, String>,
    val rating: Float,
    val reviewCount: Int,
    val inStock: Boolean
)

enum class ProductCategory {
    ELECTRONICS,
    CLOTHING,
    HOME_AND_GARDEN,
    BEAUTY,
    SPORTS,
    BOOKS,
    FOOD_AND_BEVERAGES,
    TOYS
}
```

### User

```kotlin
data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String? = null,
    val addresses: List<Address> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList()
)
```

### Cart

```kotlin
data class Cart(
    val id: String,
    val items: List<CartItem>,
    val subtotal: BigDecimal,
    val tax: BigDecimal,
    val shipping: BigDecimal,
    val discount: BigDecimal,
    val total: BigDecimal
)

data class CartItem(
    val id: String,
    val productId: String,
    val name: String,
    val imageUrl: String,
    val price: BigDecimal,
    val quantity: Int,
    val attributes: Map<String, String>
)
```

### Order

```kotlin
data class Order(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val shippingAddress: Address,
    val billingAddress: Address,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus,
    val subtotal: BigDecimal,
    val tax: BigDecimal,
    val shipping: BigDecimal,
    val discount: BigDecimal,
    val total: BigDecimal,
    val createdAt: Date,
    val updatedAt: Date,
    val estimatedDelivery: Date?,
    val trackingNumber: String?
)

enum class OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}
```

### Subscription

```kotlin
data class Subscription(
    val id: String,
    val userId: String,
    val plan: SubscriptionPlan,
    val status: SubscriptionStatus,
    val startDate: Date,
    val nextBillingDate: Date,
    val paymentMethod: PaymentMethod,
    val autoRenew: Boolean,
    val billingAddress: Address
)

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val billingCycle: BillingCycle,
    val features: List<String>,
    val isPopular: Boolean = false
)

enum class SubscriptionStatus {
    ACTIVE,
    CANCELED,
    EXPIRED,
    TRIAL,
    PAST_DUE
}

enum class BillingCycle {
    MONTHLY,
    QUARTERLY,
    BIANNUALLY,
    ANNUALLY
}
```

## ViewModels

### AuthViewModel

Handles user authentication including login, registration, and session management.

```kotlin
class AuthViewModel @Inject constructor(
    private val fakeDataRepository: FakeDataRepository
) : ViewModel() {
    // StateFlow for UI state
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    // Methods for authentication actions
    fun login(email: String, password: String) { ... }
    fun register(email: String, password: String, firstName: String, lastName: String) { ... }
    fun logout() { ... }
    
    // UI state data class
    data class AuthUiState(
        val user: User? = null,
        val isLoggedIn: Boolean = false,
        val isLoading: Boolean = true,
        val message: String? = null,
        val error: String? = null
    )
}
```

### ProductViewModel

Manages product listing, filtering, searching, and details.

```kotlin
class ProductViewModel @Inject constructor(
    private val fakeDataRepository: FakeDataRepository
) : ViewModel() {
    // StateFlow for UI state
    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()
    
    // Methods for product actions
    fun loadProducts(category: ProductCategory? = null) { ... }
    fun getProductById(productId: String): Product? { ... }
    fun searchProducts(query: String) { ... }
    
    // UI state data class
    data class ProductsUiState(
        val products: List<Product> = emptyList(),
        val selectedProduct: Product? = null,
        val isLoading: Boolean = false,
        val message: String? = null,
        val error: String? = null
    )
}
```

### CartViewModel

Handles shopping cart operations.

```kotlin
class CartViewModel @Inject constructor(
    private val fakeDataRepository: FakeDataRepository
) : ViewModel() {
    // StateFlow for UI state
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()
    
    // Methods for cart actions
    fun addToCart(product: Product, quantity: Int = 1, selectedAttributes: Map<String, String> = emptyMap()) { ... }
    fun removeFromCart(itemId: String) { ... }
    fun updateItemQuantity(itemId: String, newQuantity: Int) { ... }
    fun applyPromoCode(promoCode: String) { ... }
    fun clearCart() { ... }
    
    // UI state data class
    data class CartUiState(
        val cart: Cart? = null,
        val promoCodeApplied: Boolean = false,
        val promoCode: String = "",
        val message: String? = null,
        val error: String? = null
    )
}
```

### OrderViewModel

Manages order creation, history, and details.

```kotlin
class OrderViewModel @Inject constructor(
    private val fakeDataRepository: FakeDataRepository
) : ViewModel() {
    // StateFlow for UI state
    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()
    
    // Methods for order actions
    fun loadUserOrders(userId: String?) { ... }
    fun getOrderById(orderId: String): Order? { ... }
    fun placeOrder(userId: String?, cart: Cart, shippingAddress: Address, billingAddress: Address, paymentMethod: PaymentMethod) { ... }
    fun cancelOrder(orderId: String) { ... }
    
    // UI state data class
    data class OrderUiState(
        val orders: List<Order> = emptyList(),
        val currentOrder: Order? = null,
        val isLoading: Boolean = false,
        val message: String? = null,
        val error: String? = null
    )
}
```

### SubscriptionViewModel

Handles subscription plan selection, management, and renewal.

```kotlin
class SubscriptionViewModel @Inject constructor(
    private val fakeDataRepository: FakeDataRepository
) : ViewModel() {
    // StateFlow for UI state
    private val _uiState = MutableStateFlow(SubscriptionUiState())
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()
    
    // Methods for subscription actions
    fun loadSubscriptionPlans() { ... }
    fun loadUserSubscription(userId: String?) { ... }
    fun subscribeToNewPlan(userId: String?, plan: SubscriptionPlan, paymentMethod: PaymentMethod, billingAddress: Address) { ... }
    fun cancelSubscription(subscriptionId: String) { ... }
    fun toggleAutoRenew(subscriptionId: String) { ... }
    
    // UI state data class
    data class SubscriptionUiState(
        val subscriptionPlans: List<SubscriptionPlan> = emptyList(),
        val userSubscription: Subscription? = null,
        val isLoading: Boolean = false,
        val message: String? = null,
        val error: String? = null
    )
}
```

## UI Screens

### Home Screen

The main screen of the app showing featured products, categories, and promotions.

```kotlin
@Composable
fun HomeScreen(
    navigateToProductList: (String?) -> Unit,
    navigateToCart: () -> Unit,
    navigateToSubscription: () -> Unit,
    navigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel()
) {
    // Screen implementation with:
    // - Featured products grid
    // - Category navigation
    // - Subscription promotion
    // - User welcome section
}
```

### Authentication Screens

Login and registration screens.

```kotlin
@Composable
fun LoginScreen(
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // Login form implementation
}

@Composable
fun RegisterScreen(
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // Registration form implementation
}
```

### Product Screens

Screens for browsing and viewing product details.

```kotlin
@Composable
fun ProductListScreen(
    category: String?,
    navigateToProductDetail: (String) -> Unit,
    navigateUp: () -> Unit,
    productViewModel: ProductViewModel = hiltViewModel()
) {
    // Product grid with filtering and sorting
}

@Composable
fun ProductDetailScreen(
    productId: String,
    navigateToCart: () -> Unit,
    navigateUp: () -> Unit,
    productViewModel: ProductViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    // Product details with images, description, variants
    // Add to cart functionality
}
```

### Cart and Checkout Screens

Screens for managing cart and completing checkout.

```kotlin
@Composable
fun CartScreen(
    navigateToCheckout: () -> Unit,
    navigateToProductDetail: (String) -> Unit,
    navigateUp: () -> Unit,
    cartViewModel: CartViewModel = hiltViewModel()
) {
    // Cart items list
    // Cart summary with totals
    // Promo code application
}

@Composable
fun CheckoutScreen(
    navigateToOrderConfirmation: (String) -> Unit,
    navigateUp: () -> Unit,
    cartViewModel: CartViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // Shipping address selection/input
    // Payment method selection/input
    // Order summary
    // Place order button
}
```

### Subscription Screens

Screens for subscription management.

```kotlin
@Composable
fun SubscriptionPlansScreen(
    navigateToSubscriptionManagement: (String) -> Unit,
    navigateUp: () -> Unit,
    subscriptionViewModel: SubscriptionViewModel = hiltViewModel()
) {
    // Subscription plans comparison
    // Plan details and features
    // Subscribe buttons
}

@Composable
fun SubscriptionManagementScreen(
    subscriptionId: String,
    navigateUp: () -> Unit,
    subscriptionViewModel: SubscriptionViewModel = hiltViewModel()
) {
    // Current subscription details
    // Billing history
    // Renewal options
    // Cancel subscription button
}
```

## Navigation

The navigation system uses Jetpack Navigation Compose to manage screen transitions.

```kotlin
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
        // Auth routes
        composable(NavigationRoute.Login.route) { ... }
        composable(NavigationRoute.Register.route) { ... }
        
        // Main routes
        composable(NavigationRoute.Home.route) { ... }
        composable("${NavigationRoute.ProductList.route}?category={category}") { ... }
        composable("${NavigationRoute.ProductDetail.route}/{productId}") { ... }
        composable(NavigationRoute.Cart.route) { ... }
        composable(NavigationRoute.Checkout.route) { ... }
        
        // Subscription routes
        composable(NavigationRoute.SubscriptionPlans.route) { ... }
        composable("${NavigationRoute.SubscriptionManagement.route}/{subscriptionId}") { ... }
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
```

## Data Generation with Faker API

The app uses the JavaFaker library to generate realistic test data for products, users, orders, and subscriptions.

```kotlin
class FakeDataRepository @Inject constructor() {
    private val faker = Faker()
    
    fun generateProducts(count: Int = 20): List<Product> { ... }
    fun generateUser(): User { ... }
    fun generateCart(): Cart { ... }
    fun generateOrder(): Order { ... }
    fun generateSubscription(): Subscription { ... }
    fun generateSubscriptionPlan(): SubscriptionPlan { ... }
    
    // Helper methods for generating specific attributes
    private fun generateRandomAddress(): Address { ... }
    private fun generateRandomPaymentMethod(): PaymentMethod { ... }
}
```

## Conclusion

The Carbonium app demonstrates a modern Android application using the latest technologies and best practices. It follows the MVVM architecture pattern for clean separation of concerns and utilizes Jetpack Compose for a declarative UI approach. The app provides a complete e-commerce experience including product browsing, cart management, checkout process, and subscription management.