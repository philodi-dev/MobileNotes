package com.philodi.carbonium.data.repository

import com.philodi.carbonium.data.local.dao.ProductDao
import com.philodi.carbonium.data.local.entity.ProductEntity
import com.philodi.carbonium.data.remote.ApiService
import com.philodi.carbonium.data.remote.model.Product
import com.philodi.carbonium.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository interface for product operations
 */
interface ProductRepository {
    fun getProducts(
        category: String? = null,
        search: String? = null,
        sort: String? = null
    ): Flow<Resource<List<Product>>>
    
    fun getFeaturedProducts(): Flow<Resource<List<Product>>>
    
    suspend fun refreshProducts()
    
    suspend fun getProductById(id: String): Resource<Product>
    
    fun searchProducts(query: String): Flow<Resource<List<Product>>>
}

/**
 * Implementation of ProductRepository
 */
class ProductRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val productDao: ProductDao
) : ProductRepository {
    
    override fun getProducts(
        category: String?,
        search: String?,
        sort: String?
    ): Flow<Resource<List<Product>>> {
        return when {
            category != null -> productDao.getProductsByCategory(category)
            search != null -> productDao.searchProducts(search)
            else -> productDao.getAllProducts()
        }.map { productEntities ->
            Resource.Success(productEntities.map { it.toProduct() })
        }
    }
    
    override fun getFeaturedProducts(): Flow<Resource<List<Product>>> {
        return productDao.getFeaturedProducts().map { productEntities ->
            Resource.Success(productEntities.map { it.toProduct() })
        }
    }
    
    override suspend fun refreshProducts() {
        try {
            val products = apiService.getProducts()
            productDao.insertProducts(products.map { ProductEntity.fromProduct(it) })
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    override suspend fun getProductById(id: String): Resource<Product> {
        return try {
            val localProduct = productDao.getProductById(id)
            if (localProduct != null) {
                Resource.Success(localProduct.toProduct())
            } else {
                // Fetch from remote if not in local DB
                val remoteProduct = apiService.getProduct(id)
                productDao.insertProduct(ProductEntity.fromProduct(remoteProduct))
                Resource.Success(remoteProduct)
            }
        } catch (e: Exception) {
            Resource.Error("Failed to get product: ${e.message}", e)
        }
    }
    
    override fun searchProducts(query: String): Flow<Resource<List<Product>>> {
        return productDao.searchProducts(query).map { productEntities ->
            Resource.Success(productEntities.map { it.toProduct() })
        }
    }
}
