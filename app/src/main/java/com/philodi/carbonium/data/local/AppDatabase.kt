package com.philodi.carbonium.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.philodi.carbonium.data.local.dao.CartDao
import com.philodi.carbonium.data.local.dao.ProductDao
import com.philodi.carbonium.data.local.dao.UserDao
import com.philodi.carbonium.data.local.entity.CartItemEntity
import com.philodi.carbonium.data.local.entity.ProductEntity
import com.philodi.carbonium.data.local.entity.UserEntity

/**
 * Room database for local storage
 */
@Database(
    entities = [
        ProductEntity::class,
        CartItemEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun userDao(): UserDao
}
