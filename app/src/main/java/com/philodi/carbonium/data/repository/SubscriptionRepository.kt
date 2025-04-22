package com.philodi.carbonium.data.repository

import com.philodi.carbonium.data.remote.ApiService
import com.philodi.carbonium.data.remote.CreateSubscriptionRequest
import com.philodi.carbonium.data.remote.model.BillingCycle
import com.philodi.carbonium.data.remote.model.Service
import com.philodi.carbonium.data.remote.model.Subscription
import com.philodi.carbonium.util.Resource
import javax.inject.Inject

/**
 * Repository interface for subscription operations
 */
interface SubscriptionRepository {
    suspend fun getSubscriptions(): Resource<List<Subscription>>
    suspend fun getSubscription(id: String): Resource<Subscription>>
    suspend fun createSubscription(
        serviceId: String,
        planId: String,
        billingCycle: BillingCycle,
        autoRenew: Boolean,
        paymentMethod: String
    ): Resource<Subscription>
    suspend fun renewSubscription(id: String): Resource<Subscription>
    suspend fun cancelSubscription(id: String): Resource<Subscription>
    suspend fun getServices(): Resource<List<Service>>
    suspend fun getService(id: String): Resource<Service>
}

/**
 * Implementation of SubscriptionRepository
 */
class SubscriptionRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SubscriptionRepository {
    
    override suspend fun getSubscriptions(): Resource<List<Subscription>> {
        return try {
            val subscriptions = apiService.getSubscriptions()
            Resource.Success(subscriptions)
        } catch (e: Exception) {
            Resource.Error("Failed to get subscriptions: ${e.message}", e)
        }
    }
    
    override suspend fun getSubscription(id: String): Resource<Subscription> {
        return try {
            val subscription = apiService.getSubscription(id)
            Resource.Success(subscription)
        } catch (e: Exception) {
            Resource.Error("Failed to get subscription details: ${e.message}", e)
        }
    }
    
    override suspend fun createSubscription(
        serviceId: String,
        planId: String,
        billingCycle: BillingCycle,
        autoRenew: Boolean,
        paymentMethod: String
    ): Resource<Subscription> {
        return try {
            val subscription = apiService.createSubscription(
                CreateSubscriptionRequest(
                    serviceId = serviceId,
                    planId = planId,
                    billingCycle = billingCycle,
                    autoRenew = autoRenew,
                    paymentMethod = paymentMethod
                )
            )
            Resource.Success(subscription)
        } catch (e: Exception) {
            Resource.Error("Failed to create subscription: ${e.message}", e)
        }
    }
    
    override suspend fun renewSubscription(id: String): Resource<Subscription> {
        return try {
            val subscription = apiService.renewSubscription(id)
            Resource.Success(subscription)
        } catch (e: Exception) {
            Resource.Error("Failed to renew subscription: ${e.message}", e)
        }
    }
    
    override suspend fun cancelSubscription(id: String): Resource<Subscription> {
        return try {
            val subscription = apiService.cancelSubscription(id)
            Resource.Success(subscription)
        } catch (e: Exception) {
            Resource.Error("Failed to cancel subscription: ${e.message}", e)
        }
    }
    
    override suspend fun getServices(): Resource<List<Service>> {
        return try {
            val services = apiService.getServices()
            Resource.Success(services)
        } catch (e: Exception) {
            Resource.Error("Failed to get services: ${e.message}", e)
        }
    }
    
    override suspend fun getService(id: String): Resource<Service> {
        return try {
            val service = apiService.getService(id)
            Resource.Success(service)
        } catch (e: Exception) {
            Resource.Error("Failed to get service details: ${e.message}", e)
        }
    }
}
