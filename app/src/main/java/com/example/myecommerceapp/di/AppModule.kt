package com.example.myecommerceapp.di

import com.henrypeya.core.model.AuthRepository
import com.henrypeya.core.model.CartRepository
import com.henrypeya.core.model.ProductRepository
import com.henrypeya.data.AuthRepositoryImpl
import com.henrypeya.data.FakeCartRepositoryImpl
import com.henrypeya.data.FakeProductRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module //gestiona objetos o dependencias que Hilt puede inyectar
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    // Hilt: "Cuando alguien pida AuthRepository, dale una instancia de AuthRepositoryImpl"
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