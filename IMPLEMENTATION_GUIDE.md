# Carbonium - Android E-commerce & Subscription App Implementation Guide

## Project Overview

Carbonium is a full-featured Android e-commerce application built with Kotlin and Jetpack Compose using MVVM architecture. The app includes sophisticated e-commerce functionality including product browsing, shopping cart management, checkout process, order tracking, and subscription management.

## Environment Setup Requirements

To build and run this project, you need:

1. **Java Development Kit (JDK) 17**
2. **Android SDK** with:
   - Android SDK Platform 34
   - Android SDK Build-Tools 33.0.1
   - Android SDK Command-line Tools
   - Android SDK Platform-Tools
   - Android Emulator (for testing)

3. **License Acceptance**:
   - All Android SDK licenses must be accepted (using `sdkmanager --licenses`)

## Project Structure

The app follows MVVM (Model-View-ViewModel) architecture and is organized as follows:

```
com.philodi.carbonium/
├── data/                   # Data layer
│   ├── model/              # Data models
│   ├── repository/         # Repositories for data operations
│   ├── local/              # Local data sources (Room)
│   └── remote/             # Remote data sources (APIs)
├── di/                     # Dependency injection 
├── domain/                 # Domain layer with business logic
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Use cases and business logic
├── presentation/           # UI layer
│   ├── navigation/         # Navigation components
│   ├── screens/            # UI screens organized by feature
│   └── viewmodel/          # ViewModels
├── ui/                     # UI components and theme
└── util/                   # Utility classes
```

## Key Components

### Data Models

1. **Product**: Represents product information with attributes, pricing, and inventory status.
2. **User**: User account data with personal information and preferences.
3. **Cart**: Shopping cart with items, quantities, and pricing calculations.
4. **Order**: Order information including items, shipping details, and status.
5. **Subscription**: Subscription plans and user subscriptions with billing details.

### ViewModels

1. **AuthViewModel**: Handles authentication, registration, and user session management.
2. **ProductViewModel**: Manages product listing, filtering, and detail display.
3. **CartViewModel**: Handles shopping cart operations and calculations.
4. **OrderViewModel**: Manages order creation, history, and status tracking.
5. **SubscriptionViewModel**: Handles subscription plan options and subscription management.

### UI Screens

1. **Authentication**: Login and registration screens.
2. **Home**: Main screen with featured products and categories.
3. **Product**: Product listing and detail screens.
4. **Cart**: Shopping cart management screen.
5. **Checkout**: Order completion process screens.
6. **Subscription**: Subscription plan and management screens.

## Features

### User Authentication
- User registration and login
- Profile management
- Address and payment method management

### Product Browsing
- Category-based browsing
- Product search and filtering
- Detailed product information display

### Shopping Cart
- Add/remove items
- Quantity adjustments
- Price calculations with tax and shipping
- Promo code application

### Checkout Process
- Shipping address selection
- Payment method selection
- Order review and confirmation
- Order tracking

### Subscription Management
- Subscription plan browsing
- Plan comparison
- Subscription activation and management
- Auto-renewal settings
- Cancellation options

## Implementation Notes

### Data Generation

The app uses JavaFaker for generating realistic test data across all data models:

```kotlin
class FakeDataRepository @Inject constructor() {
    private val faker = Faker()
    
    fun generateProducts(count: Int = 20): List<Product> { ... }
    fun generateUser(): User { ... }
    fun generateCart(): Cart { ... }
    fun generateOrder(): Order { ... }
    fun generateSubscription(): Subscription { ... }
}
```

### Dependency Injection

Hilt is used for dependency injection:

```kotlin
@HiltAndroidApp
class CarboniumApplication : Application() {
    // Application setup
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFakeDataRepository(): FakeDataRepository {
        return FakeDataRepository()
    }
    
    // Other dependencies
}
```

### UI with Jetpack Compose

All UI is built with Jetpack Compose using Material 3 design components:

```kotlin
@Composable
fun HomeScreen(
    navigateToProductList: (String?) -> Unit,
    navigateToCart: () -> Unit,
    navigateToSubscription: () -> Unit,
    navigateToLogin: () -> Unit,
    // ViewModels
) {
    // UI implementation
}
```

### Navigation

Jetpack Navigation Compose is used for navigation between screens:

```kotlin
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Home.route
    ) {
        // Route definitions
    }
}
```

## Build Instructions

1. Clone the repository
2. Ensure Android SDK is properly configured
3. Accept all SDK licenses
4. Build the project with Gradle:
   ```
   ./gradlew build
   ```
5. Run the app on an emulator or device:
   ```
   ./gradlew installDebug
   ```

## Next Steps for Implementation

1. **Complete the Room Database Implementation**:
   - Create database entities
   - Define DAOs for data access
   - Set up the Room database

2. **Add Real Networking**:
   - Implement API interfaces with Retrofit
   - Create real data repositories
   - Add authentication token handling

3. **Enhance UI/UX**:
   - Implement animations and transitions
   - Add loading states
   - Improve error handling

4. **Implement Testing**:
   - Unit tests for ViewModels
   - Integration tests for repositories
   - UI tests with Compose testing

5. **Add Advanced Features**:
   - Push notifications
   - Offline support
   - Analytics tracking

## Common Build Issues and Solutions

### SDK License Acceptance

If you encounter license acceptance issues:

1. Use the SDK Manager to accept licenses:
   ```
   sdkmanager --licenses
   ```

2. Or manually create license files:
   ```
   mkdir -p $ANDROID_HOME/licenses
   echo "8933bad161af4178b1185d1a37fbf41ea5269c55" > $ANDROID_HOME/licenses/android-sdk-license
   echo "d56f5187479451eabf01fb78af6dfcb131a6481e" >> $ANDROID_HOME/licenses/android-sdk-license
   ```

### Adaptive Icons

The app uses adaptive icons which require minSdk 26:

```gradle
android {
    defaultConfig {
        minSdk 26
        // ...
    }
}
```

### Java Version Compatibility

The app requires Java 17:

```gradle
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = '17'
    }
}
```