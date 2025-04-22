package com.philodi.carbonium.data.repository

import com.philodi.carbonium.data.local.dao.UserDao
import com.philodi.carbonium.data.local.entity.UserEntity
import com.philodi.carbonium.data.remote.ApiService
import com.philodi.carbonium.data.remote.model.Child
import com.philodi.carbonium.data.remote.model.User
import com.philodi.carbonium.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository interface for user operations
 */
interface UserRepository {
    fun getCurrentUser(): Flow<Resource<User?>>
    suspend fun updateUser(user: User): Resource<User>
    suspend fun syncUser(): Resource<User>
    
    suspend fun getChildren(): Resource<List<Child>>
    suspend fun getChild(id: String): Resource<Child>
    suspend fun addChild(child: Child): Resource<Child>
    suspend fun updateChild(id: String, child: Child): Resource<Child>
    suspend fun deleteChild(id: String): Resource<Unit>
}

/**
 * Implementation of UserRepository
 */
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao
) : UserRepository {
    
    override fun getCurrentUser(): Flow<Resource<User?>> {
        return userDao.getCurrentUser().map { userEntity ->
            userEntity?.let {
                Resource.Success(it.toUser())
            } ?: Resource.Success(null)
        }
    }
    
    override suspend fun updateUser(user: User): Resource<User> {
        return try {
            val updatedUser = apiService.updateUser(user)
            userDao.updateUser(UserEntity.fromUser(updatedUser))
            Resource.Success(updatedUser)
        } catch (e: Exception) {
            Resource.Error("Failed to update user: ${e.message}", e)
        }
    }
    
    override suspend fun syncUser(): Resource<User> {
        return try {
            val user = apiService.getCurrentUser()
            userDao.insertUser(UserEntity.fromUser(user))
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error("Failed to sync user data: ${e.message}", e)
        }
    }
    
    override suspend fun getChildren(): Resource<List<Child>> {
        return try {
            val children = apiService.getChildren()
            Resource.Success(children)
        } catch (e: Exception) {
            Resource.Error("Failed to get children: ${e.message}", e)
        }
    }
    
    override suspend fun getChild(id: String): Resource<Child> {
        return try {
            val child = apiService.getChild(id)
            Resource.Success(child)
        } catch (e: Exception) {
            Resource.Error("Failed to get child details: ${e.message}", e)
        }
    }
    
    override suspend fun addChild(child: Child): Resource<Child> {
        return try {
            val newChild = apiService.addChild(child)
            Resource.Success(newChild)
        } catch (e: Exception) {
            Resource.Error("Failed to add child: ${e.message}", e)
        }
    }
    
    override suspend fun updateChild(id: String, child: Child): Resource<Child> {
        return try {
            val updatedChild = apiService.updateChild(id, child)
            Resource.Success(updatedChild)
        } catch (e: Exception) {
            Resource.Error("Failed to update child: ${e.message}", e)
        }
    }
    
    override suspend fun deleteChild(id: String): Resource<Unit> {
        return try {
            apiService.deleteChild(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to delete child: ${e.message}", e)
        }
    }
}
