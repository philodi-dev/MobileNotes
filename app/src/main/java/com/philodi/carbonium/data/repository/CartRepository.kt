package com.philodi.carbonium.data.repository

import com.philodi.carbonium.data.local.dao.CartDao
import com.philodi.carbonium.data.local.dao.ProductDao
import com.philodi.carbonium.data.local.entity.CartItemEntity
import com.philodi.carbonium.data.remote.ApiService
import com.philodi.carbonium.data.remote.AddToCartRequest
import com.philodi.carbonium.data.remote.Cart
import com.philodi.carbonium.data.remote.CartItem
import com.philodi.carbonium.data.remote.UpdateCartItemRequest
import com.philodi.carbonium.data.remote.model.Product
import com.philodi.carbonium.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository interface for cart operations
 */
interface CartRepository {
    fun getCartItems(): Flow<Resource<List<CartItem>>>
    fun getCartItemCount(): Flow<Int>
    fun getCartSubtotal(): Flow<Double>
    suspend fun addToCart(productId: String, quantity: Int): Resource<Unit>
    suspend fun updateCartItemQuantity(itemId: String, quantity: Int): Resource<Unit>
    suspend fun removeCartItem(productId: String): Resource<Unit>
    suspend fun clearCart(): Resource<Unit>
    suspend fun syncCart(): Resource<Cart>
}

/**
 * Implementation of CartRepository
 */
class CartRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val cartDao: CartDao,
    private val productDao: ProductDao
) : CartRepository {
    
    override fun getCartItems(): Flow<Resource<List<CartItem>>> {
        return cartDao.getAllCartItems().map { cartItems ->
            try {
                val items = cartItems.mapNotNull { cartItem ->
                    val product = productDao.getProductById(cartItem.productId)
                    product?.let {
                        CartItem(
                            id = cartItem.id.toString(),
                            product = it.toProduct(),
                            quantity = cartItem.quantity,
                            price = it.price * cartItem.quantity
                        )
                    }
                }
                Resource.Success(items)
            } catch (e: Exception) {
                Resource.Error("Failed to load cart items: ${e.message}", e)
            }
        }
    }
    
    override fun getCartItemCount(): Flow<Int> {
        return cartDao.getCartItemCount()
    }
    
    override fun getCartSubtotal(): Flow<Double> {
        return cartDao.getCartSubtotal().map { it ?: 0.0 }
    }
    
    override suspend fun addToCart(productId: String, quantity: Int): Resource<Unit> {
        return try {
            cartDao.addOrUpdateCartItem(productId, quantity)
            
            // Also sync with remote
            try {
                apiService.addToCart(AddToCartRequest(productId, quantity))
            } catch (e: Exception) {
                // Handle remote sync failure (could retry later)
            }
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to add to cart: ${e.message}", e)
        }
    }
    
    override suspend fun updateCartItemQuantity(itemId: String, quantity: Int): Resource<Unit> {
        return try {
            // Convert itemId to productId in real implementation
            val productId = itemId // This is a simplification
            cartDao.updateCartItemQuantity(productId, quantity)
            
            // Sync with remote
            try {
                apiService.updateCartItem(UpdateCartItemRequest(itemId, quantity))
            } catch (e: Exception) {
                // Handle remote sync failure
            }
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to update cart: ${e.message}", e)
        }
    }
    
    override suspend fun removeCartItem(productId: String): Resource<Unit> {
        return try {
            cartDao.deleteCartItemByProductId(productId)
            
            // Sync with remote
            try {
                apiService.removeCartItem(productId)
            } catch (e: Exception) {
                // Handle remote sync failure
            }
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to remove from cart: ${e.message}", e)
        }
    }
    
    override suspend fun clearCart(): Resource<Unit> {
        return try {
            cartDao.clearCart()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to clear cart: ${e.message}", e)
        }
    }
    
    override suspend fun syncCart(): Resource<Cart> {
        return try {
            val remoteCart = apiService.getCart()
            
            // Update local cart
            cartDao.clearCart()
            remoteCart.items.forEach { item ->
                cartDao.insertCartItem(
                    CartItemEntity(
                        productId = item.product.id,
                        quantity = item.quantity
                    )
                )
                
                // Ensure we have product in local DB
                productDao.insertProduct(ProductEntity.fromProduct(item.product))
            }
            
            Resource.Success(remoteCart)
        } catch (e: Exception) {
            Resource.Error("Failed to sync cart: ${e.message}", e)
        }
    }
}
