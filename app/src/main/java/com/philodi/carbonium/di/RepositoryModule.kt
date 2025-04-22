package com.philodi.carbonium.di

import com.philodi.carbonium.data.repository.AuthRepository
import com.philodi.carbonium.data.repository.CartRepository
import com.philodi.carbonium.data.repository.OrderRepository
import com.philodi.carbonium.data.repository.ProductRepository
import com.philodi.carbonium.data.repository.SubscriptionRepository
import com.philodi.carbonium.data.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing repository implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: com.philodi.carbonium.data.repository.AuthRepositoryImpl
    ): AuthRepository
    
    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: com.philodi.carbonium.data.repository.ProductRepositoryImpl
    ): ProductRepository
    
    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: com.philodi.carbonium.data.repository.CartRepositoryImpl
    ): CartRepository
    
    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: com.philodi.carbonium.data.repository.OrderRepositoryImpl
    ): OrderRepository
    
    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(
        subscriptionRepositoryImpl: com.philodi.carbonium.data.repository.SubscriptionRepositoryImpl
    ): SubscriptionRepository
    
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: com.philodi.carbonium.data.repository.UserRepositoryImpl
    ): UserRepository
}
