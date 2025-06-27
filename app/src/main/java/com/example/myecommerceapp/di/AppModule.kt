package com.example.myecommerceapp.di

import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import com.henrypeya.core.model.domain.repository.cart.CartRepository
import com.henrypeya.core.model.domain.repository.product.ProductRepository
import com.henrypeya.data.AuthRepositoryImpl
import com.henrypeya.data.FakeCartRepositoryImpl
import com.henrypeya.data.FakeProductRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        fakeProductRepository: FakeProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        fakeCartRepositoryImpl: FakeCartRepositoryImpl
    ): CartRepository
}