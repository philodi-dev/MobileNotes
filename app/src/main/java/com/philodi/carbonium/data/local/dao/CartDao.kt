package com.philodi.carbonium.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.philodi.carbonium.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Cart item entities
 */
@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItemEntity>>
    
    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun getCartItemByProductId(productId: String): CartItemEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItemEntity): Long
    
    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateCartItemQuantity(productId: String, quantity: Int)
    
    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteCartItemByProductId(productId: String)
    
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
    
    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartItemCount(): Flow<Int>
    
    @Query("SELECT SUM(p.price * c.quantity) FROM cart_items c JOIN products p ON c.productId = p.id")
    fun getCartSubtotal(): Flow<Double?>
    
    @Transaction
    suspend fun addOrUpdateCartItem(productId: String, quantity: Int) {
        val existingItem = getCartItemByProductId(productId)
        if (existingItem == null) {
            insertCartItem(CartItemEntity(productId = productId, quantity = quantity))
        } else {
            updateCartItemQuantity(productId, existingItem.quantity + quantity)
        }
    }
}
