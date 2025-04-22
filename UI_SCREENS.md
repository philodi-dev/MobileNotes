# Carbonium - UI Screens and User Flows

This document outlines the different screens in the Carbonium app and the user flows between them.

## Main User Flows

### Authentication Flow

1. **Launch App**
   - If user is not logged in, redirect to Login screen
   - If user is logged in, go to Home screen

2. **Login Flow**
   - User enters email and password
   - On successful login, navigate to Home screen
   - On failure, show error message
   - "Register" button navigates to Register screen
   - "Forgot Password" button shows password reset options

3. **Registration Flow**
   - User enters personal information (name, email, password)
   - On successful registration, navigate to Home screen
   - On failure, show error message
   - "Login" button navigates back to Login screen

### Shopping Flow

1. **Browse Products**
   - User can browse products by category or search
   - Clicking on a product navigates to Product Detail screen

2. **Product Detail**
   - User can view product details, images, reviews
   - "Add to Cart" button adds the product to cart
   - User can select variants and quantity

3. **Cart Management**
   - User can view all items in cart
   - User can change quantities or remove items
   - User can apply promo codes
   - "Checkout" button navigates to Checkout screen

4. **Checkout Process**
   - User selects/enters shipping address
   - User selects/enters payment method
   - User reviews order summary
   - "Place Order" button creates the order and navigates to Order Confirmation

5. **Order Confirmation**
   - User sees confirmation with order details
   - User can navigate to Home or Order Details

### Subscription Flow

1. **Browse Subscription Plans**
   - User can compare different subscription plans
   - "Subscribe" button navigates to subscription checkout

2. **Subscription Checkout**
   - User enters/selects billing information
   - User reviews subscription details
   - "Confirm Subscription" button activates the subscription

3. **Subscription Management**
   - User can view current subscription details
   - User can cancel subscription or toggle auto-renew
   - User can upgrade/downgrade subscription plan

## Screen Implementations

### Home Screen

The main entry point of the app showing featured products, categories, and promotions.

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
    val authState by authViewModel.uiState.collectAsState()
    val productsState by productViewModel.uiState.collectAsState()
    
    // Top app bar with profile and cart icons
    
    // Content
    LazyColumn {
        // Welcome section (personalized if logged in)
        item {
            Card {
                if (authState.isLoggedIn && authState.user != null) {
                    Text("Welcome back, ${authState.user?.firstName}!")
                } else {
                    Text("Welcome to Carbonium")
                    Button(onClick = navigateToLogin) {
                        Text("Login / Register")
                    }
                }
            }
        }
        
        // Categories section
        item {
            Text("Shop by Category")
            LazyRow {
                items(ProductCategory.values()) { category ->
                    CategoryItem(
                        category = category,
                        onClick = { navigateToProductList(category.name) }
                    )
                }
            }
        }
        
        // Featured products section
        item {
            Text("Featured Products")
        }
        
        // Display products in a grid
        items(productsState.products.chunked(2)) { rowProducts ->
            Row {
                rowProducts.forEach { product ->
                    ProductItem(
                        product = product,
                        onClick = { navigateToProductList(null) }
                    )
                }
            }
        }
        
        // Subscription promotion
        item {
            Card {
                Text("Join our Premium Membership")
                Text("Subscribe to get exclusive deals, free shipping, and more!")
                Button(onClick = navigateToSubscription) {
                    Text("View Plans")
                }
            }
        }
    }
}
```

### Authentication Screens

#### Login Screen

```kotlin
@Composable
fun LoginScreen(
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    Column {
        Text("Welcome to Carbonium")
        Text("Login to your account")
        
        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        
        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        
        // Forgot password link
        TextButton(onClick = { /* Forgot password */ }) {
            Text("Forgot Password?")
        }
        
        // Error message
        if (authState.error != null) {
            Text(
                text = authState.error ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }
        
        // Login button
        Button(
            onClick = { authViewModel.login(email, password) },
            enabled = !authState.isLoading
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Login")
            }
        }
        
        // Register link
        Row {
            Text("Don't have an account?")
            TextButton(onClick = navigateToRegister) {
                Text("Register")
            }
        }
        
        // Demo options
        Card {
            Text("Demo Options")
            Button(onClick = { authViewModel.loadSampleUser() }) {
                Text("Login as Demo User")
            }
        }
    }
}
```

#### Register Screen

```kotlin
@Composable
fun RegisterScreen(
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    Column {
        Text("Create Account")
        
        // Name fields
        Row {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") }
            )
            
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") }
            )
        }
        
        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        
        // Password fields
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = password != confirmPassword && confirmPassword.isNotEmpty()
        )
        
        // Error message
        if (authState.error != null) {
            Text(
                text = authState.error ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }
        
        // Register button
        Button(
            onClick = {
                if (password == confirmPassword) {
                    authViewModel.register(email, password, firstName, lastName)
                }
            },
            enabled = !authState.isLoading && password == confirmPassword
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Create Account")
            }
        }
        
        // Login link
        Row {
            Text("Already have an account?")
            TextButton(onClick = navigateToLogin) {
                Text("Login")
            }
        }
    }
}
```

### Product Screens

#### Product List Screen

```kotlin
@Composable
fun ProductListScreen(
    category: String?,
    navigateToProductDetail: (String) -> Unit,
    navigateUp: () -> Unit,
    productViewModel: ProductViewModel = hiltViewModel()
) {
    val productsState by productViewModel.uiState.collectAsState()
    
    // Load products when screen is created
    LaunchedEffect(category) {
        val categoryEnum = category?.let { ProductCategory.valueOf(it) }
        productViewModel.loadProducts(categoryEnum)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category ?: "All Products") },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Filter and sort controls
            Row {
                // Filters
                // Sort options
            }
            
            // Product grid
            if (productsState.isLoading) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (productsState.error != null) {
                Text(
                    text = productsState.error ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(productsState.products) { product ->
                        ProductGridItem(
                            product = product,
                            onClick = { navigateToProductDetail(product.id) }
                        )
                    }
                }
            }
        }
    }
}
```

#### Product Detail Screen

```kotlin
@Composable
fun ProductDetailScreen(
    productId: String,
    navigateToCart: () -> Unit,
    navigateUp: () -> Unit,
    productViewModel: ProductViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val productsState by productViewModel.uiState.collectAsState()
    val cartState by cartViewModel.uiState.collectAsState()
    
    var quantity by remember { mutableStateOf(1) }
    var selectedAttributes by remember { mutableStateOf(mapOf<String, String>()) }
    
    // Load product details
    LaunchedEffect(productId) {
        productViewModel.getProductById(productId)
    }
    
    val product = productsState.selectedProduct
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "Product Details") },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = navigateToCart) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        }
    ) { padding ->
        if (product == null) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                // Product image
                item {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                }
                
                // Product info
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        
                        Text(
                            text = NumberFormat.getCurrencyInstance().format(product.price),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        // Rating
                        Row {
                            Text("${product.rating}/5.0")
                            Text("(${product.reviewCount} reviews)")
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            if (product.inStock) {
                                Text(
                                    text = "In Stock",
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            } else {
                                Text(
                                    text = "Out of Stock",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
                
                // Product description
                item {
                    Card(modifier = Modifier.padding(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(product.description)
                        }
                    }
                }
                
                // Product attributes
                if (product.attributes.isNotEmpty()) {
                    item {
                        Card(modifier = Modifier.padding(16.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Options",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                product.attributes.forEach { (key, value) ->
                                    Row {
                                        Text("$key: $value")
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Quantity selector
                item {
                    Card(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Quantity:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                enabled = quantity > 1
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }
                            
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            IconButton(onClick = { quantity++ }) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                        }
                    }
                }
                
                // Add to cart button
                item {
                    Button(
                        onClick = {
                            cartViewModel.addToCart(
                                product = product,
                                quantity = quantity,
                                selectedAttributes = selectedAttributes
                            )
                        },
                        enabled = product.inStock,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Add to Cart")
                    }
                }
                
                // Success message
                if (cartState.message != null) {
                    item {
                        Snackbar {
                            Text(cartState.message ?: "")
                        }
                    }
                }
            }
        }
    }
}
```

### Cart and Checkout Screens

#### Cart Screen

```kotlin
@Composable
fun CartScreen(
    navigateToCheckout: () -> Unit,
    navigateToProductDetail: (String) -> Unit,
    navigateUp: () -> Unit,
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val cartState by cartViewModel.uiState.collectAsState()
    val currencyFormatter = NumberFormat.getCurrencyInstance()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping Cart") },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (cartState.cart?.items.isNullOrEmpty()) {
                // Empty cart view
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Your cart is empty")
                    Button(onClick = navigateUp) {
                        Text("Continue Shopping")
                    }
                }
            } else {
                // Cart with items
                Column {
                    // Cart items list
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(cartState.cart?.items ?: emptyList()) { item ->
                            CartItem(
                                item = item,
                                onRemove = {
                                    cartViewModel.removeFromCart(item.id)
                                },
                                onQuantityChange = { newQuantity ->
                                    cartViewModel.updateItemQuantity(item.id, newQuantity)
                                },
                                onProductClick = {
                                    navigateToProductDetail(item.productId)
                                }
                            )
                            
                            Divider()
                        }
                    }
                    
                    // Cart summary
                    Card(modifier = Modifier.padding(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Order Summary")
                            
                            Row {
                                Text("Subtotal")
                                Spacer(modifier = Modifier.weight(1f))
                                Text(currencyFormatter.format(cartState.cart?.subtotal))
                            }
                            
                            Row {
                                Text("Shipping")
                                Spacer(modifier = Modifier.weight(1f))
                                Text(currencyFormatter.format(cartState.cart?.shipping))
                            }
                            
                            Row {
                                Text("Tax")
                                Spacer(modifier = Modifier.weight(1f))
                                Text(currencyFormatter.format(cartState.cart?.tax))
                            }
                            
                            if ((cartState.cart?.discount ?: 0.0) > 0.0) {
                                Row {
                                    Text("Discount")
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text("-${currencyFormatter.format(cartState.cart?.discount)}")
                                }
                            }
                            
                            Divider()
                            
                            Row {
                                Text("Total")
                                Spacer(modifier = Modifier.weight(1f))
                                Text(currencyFormatter.format(cartState.cart?.total))
                            }
                            
                            Button(
                                onClick = navigateToCheckout,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Proceed to Checkout")
                            }
                        }
                    }
                }
            }
        }
    }
}
```

#### Checkout Screen

```kotlin
@Composable
fun CheckoutScreen(
    navigateToOrderConfirmation: (String) -> Unit,
    navigateUp: () -> Unit,
    cartViewModel: CartViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val cartState by cartViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val orderState by orderViewModel.uiState.collectAsState()
    
    var selectedShippingAddress by remember { mutableStateOf<Address?>(null) }
    var selectedBillingAddress by remember { mutableStateOf<Address?>(null) }
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var useShippingAsBilling by remember { mutableStateOf(true) }
    
    // Observe order creation and navigate to confirmation
    LaunchedEffect(orderState.currentOrder) {
        orderState.currentOrder?.let { order ->
            navigateToOrderConfirmation(order.id)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            // Shipping address section
            item {
                Card(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Shipping Address")
                        
                        // Address selection UI
                        // User can select from saved addresses or add a new one
                    }
                }
            }
            
            // Billing address section
            item {
                Card(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row {
                            Text("Billing Address")
                            Spacer(modifier = Modifier.weight(1f))
                            Checkbox(
                                checked = useShippingAsBilling,
                                onCheckedChange = { useShippingAsBilling = it }
                            )
                            Text("Same as shipping")
                        }
                        
                        if (!useShippingAsBilling) {
                            // Billing address selection UI
                        }
                    }
                }
            }
            
            // Payment method section
            item {
                Card(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Payment Method")
                        
                        // Payment method selection UI
                        // User can select from saved methods or add a new one
                    }
                }
            }
            
            // Order summary section
            item {
                Card(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Order Summary")
                        
                        // Cart items summary
                        cartState.cart?.items?.forEach { item ->
                            Row {
                                Text("${item.quantity}x ${item.name}")
                                Spacer(modifier = Modifier.weight(1f))
                                Text(NumberFormat.getCurrencyInstance().format(
                                    item.price.multiply(BigDecimal(item.quantity))
                                ))
                            }
                        }
                        
                        Divider()
                        
                        // Totals
                        Row {
                            Text("Subtotal")
                            Spacer(modifier = Modifier.weight(1f))
                            Text(NumberFormat.getCurrencyInstance().format(cartState.cart?.subtotal))
                        }
                        
                        Row {
                            Text("Shipping")
                            Spacer(modifier = Modifier.weight(1f))
                            Text(NumberFormat.getCurrencyInstance().format(cartState.cart?.shipping))
                        }
                        
                        Row {
                            Text("Tax")
                            Spacer(modifier = Modifier.weight(1f))
                            Text(NumberFormat.getCurrencyInstance().format(cartState.cart?.tax))
                        }
                        
                        if ((cartState.cart?.discount ?: 0.0) > 0.0) {
                            Row {
                                Text("Discount")
                                Spacer(modifier = Modifier.weight(1f))
                                Text("-${NumberFormat.getCurrencyInstance().format(cartState.cart?.discount)}")
                            }
                        }
                        
                        Divider()
                        
                        Row {
                            Text("Total")
                            Spacer(modifier = Modifier.weight(1f))
                            Text(NumberFormat.getCurrencyInstance().format(cartState.cart?.total))
                        }
                    }
                }
            }
            
            // Place order button
            item {
                Button(
                    onClick = {
                        val userId = authState.user?.id
                        val cart = cartState.cart
                        val shippingAddress = selectedShippingAddress
                        val billingAddress = if (useShippingAsBilling) selectedShippingAddress else selectedBillingAddress
                        val paymentMethod = selectedPaymentMethod
                        
                        if (userId != null && cart != null && shippingAddress != null && billingAddress != null && paymentMethod != null) {
                            orderViewModel.placeOrder(
                                userId = userId,
                                cart = cart,
                                shippingAddress = shippingAddress,
                                billingAddress = billingAddress,
                                paymentMethod = paymentMethod
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = selectedShippingAddress != null && 
                             (useShippingAsBilling || selectedBillingAddress != null) &&
                             selectedPaymentMethod != null
                ) {
                    Text("Place Order")
                }
            }
        }
    }
}
```

### Subscription Screens

#### Subscription Plans Screen

```kotlin
@Composable
fun SubscriptionPlansScreen(
    navigateToSubscriptionManagement: (String) -> Unit,
    navigateUp: () -> Unit,
    subscriptionViewModel: SubscriptionViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val subscriptionState by subscriptionViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    
    // Load subscription plans when screen is created
    LaunchedEffect(Unit) {
        subscriptionViewModel.loadSubscriptionPlans()
        
        if (authState.isLoggedIn && authState.user != null) {
            subscriptionViewModel.loadUserSubscription(authState.user?.id)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscription Plans") },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (subscriptionState.isLoading) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (subscriptionState.userSubscription != null) {
            // User already has a subscription
            Column(
                modifier = Modifier.padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("You are currently subscribed to:")
                        Text(
                            text = subscriptionState.userSubscription?.plan?.name ?: "",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        
                        Button(
                            onClick = {
                                subscriptionState.userSubscription?.id?.let {
                                    navigateToSubscriptionManagement(it)
                                }
                            }
                        ) {
                            Text("Manage Subscription")
                        }
                    }
                }
            }
        } else {
            // Show subscription plans
            LazyColumn(modifier = Modifier.padding(padding)) {
                item {
                    Text(
                        text = "Choose a Subscription Plan",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                items(subscriptionState.subscriptionPlans) { plan ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (plan.isPopular) {
                                Text(
                                    text = "MOST POPULAR",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            
                            Text(
                                text = plan.name,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            
                            Text(
                                text = "${NumberFormat.getCurrencyInstance().format(plan.price)}/${plan.billingCycle.name.lowercase()}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Text(plan.description)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text("Features:")
                            plan.features.forEach { feature ->
                                Row {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                    Text(feature)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    // Subscribe to this plan
                                    // In a real app, would navigate to subscription checkout
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Subscribe")
                            }
                        }
                    }
                }
            }
        }
    }
}
```

#### Subscription Management Screen

```kotlin
@Composable
fun SubscriptionManagementScreen(
    subscriptionId: String,
    navigateUp: () -> Unit,
    subscriptionViewModel: SubscriptionViewModel = hiltViewModel()
) {
    val subscriptionState by subscriptionViewModel.uiState.collectAsState()
    
    // Load subscription details
    LaunchedEffect(subscriptionId) {
        // In a real app, would load the specific subscription by ID
        // For demo purposes, we're just loading the user's subscription
        subscriptionViewModel.loadUserSubscription(null)
    }
    
    val subscription = subscriptionState.userSubscription
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Subscription") },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (subscriptionState.isLoading) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (subscription == null) {
            Text("Subscription not found")
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                // Subscription details
                item {
                    Card(modifier = Modifier.padding(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = subscription.plan.name,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            
                            Text(
                                text = "${NumberFormat.getCurrencyInstance().format(subscription.plan.price)}/${subscription.plan.billingCycle.name.lowercase()}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Text("Status: ${subscription.status.name}")
                            
                            Text("Started on: ${DateFormat.getDateInstance().format(subscription.startDate)}")
                            
                            if (subscription.status == SubscriptionStatus.ACTIVE) {
                                Text("Next billing date: ${DateFormat.getDateInstance().format(subscription.nextBillingDate)}")
                            }
                        }
                    }
                }
                
                // Auto-renew toggle
                item {
                    Card(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Auto-renew subscription")
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            Switch(
                                checked = subscription.autoRenew,
                                onCheckedChange = {
                                    subscriptionViewModel.toggleAutoRenew(subscription.id)
                                }
                            )
                        }
                    }
                }
                
                // Cancel subscription button
                item {
                    Button(
                        onClick = { subscriptionViewModel.cancelSubscription(subscription.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        enabled = subscription.status == SubscriptionStatus.ACTIVE
                    ) {
                        Text("Cancel Subscription")
                    }
                }
            }
        }
    }
}
```

## UI Components

### Home Screen Components

```kotlin
@Composable
fun CategoryItem(
    category: ProductCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category.name.replace("_", " "),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = NumberFormat.getCurrencyInstance().format(product.price),
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(product.rating.toString())
                    
                    Text(" (${product.reviewCount})")
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    if (product.inStock) {
                        Text(
                            text = "In Stock",
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    } else {
                        Text(
                            text = "Out of Stock",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
```

### Cart Components

```kotlin
@Composable
fun CartItem(
    item: CartItem,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onProductClick: () -> Unit
) {
    Card(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onProductClick)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Product details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(onClick = onProductClick)
                )
                
                Text(
                    text = NumberFormat.getCurrencyInstance().format(item.price),
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Display attributes if any
                item.attributes.forEach { (key, value) ->
                    Text("$key: $value")
                }
                
                Text(
                    text = "Total: ${NumberFormat.getCurrencyInstance().format(
                        item.price.multiply(BigDecimal(item.quantity))
                    )}"
                )
            }
            
            // Quantity control
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onQuantityChange(item.quantity - 1) },
                        enabled = item.quantity > 1
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }
                    
                    Text(item.quantity.toString())
                    
                    IconButton(onClick = { onQuantityChange(item.quantity + 1) }) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }
                
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                }
            }
        }
    }
}
```

## Navigation Integration

The app uses Jetpack Navigation Compose to manage the navigation between screens:

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
                    navController.navigate(NavigationRoute.Login.route)
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
        
        // Product screens
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
        
        // ... additional route definitions for other screens
    }
}
```