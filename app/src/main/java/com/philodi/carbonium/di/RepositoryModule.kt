package com.philodi.carbonium.di

import com.philodi.carbonium.data.repository.FakeDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideFakeDataRepository(): FakeDataRepository {
        return FakeDataRepository()
    }
}