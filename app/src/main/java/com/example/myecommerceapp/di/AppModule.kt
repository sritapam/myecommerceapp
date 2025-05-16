package com.example.myecommerceapp.di

import com.example.myecommerceapp.data.FakeProductRepository
import com.example.myecommerceapp.domain.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//crea una clase singleton
@Module  // Marca esta clase como módulo para Hilt
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFakeProductRepository(fakeProductRepository: FakeProductRepository): ProductRepository {
        return fakeProductRepository
    }

    @Provides
    @Singleton
    fun provideFakeProductRepositoryImpl(): FakeProductRepository {
        return FakeProductRepository()
    }
}