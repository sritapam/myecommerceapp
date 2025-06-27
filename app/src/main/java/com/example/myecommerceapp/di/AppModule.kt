package com.example.myecommerceapp.di

import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import com.henrypeya.core.model.domain.repository.cart.CartRepository
import com.henrypeya.core.model.domain.repository.product.ProductRepository
import com.henrypeya.core.model.domain.repository.user.UserRepository
import com.henrypeya.data.repository.auth.AuthRepositoryImpl
import com.henrypeya.data.repository.cart.FakeCartRepositoryImpl
import com.henrypeya.data.repository.product.FakeProductRepositoryImpl
import com.henrypeya.data.repository.user.FakeUserRepositoryImpl
import com.henrypeya.data.service.imageupload.CloudinaryService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.henrypeya.data.service.imageupload.FakeCloudinaryService

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

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        fakeUserRepositoryImpl: FakeUserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindCloudinaryService(
        fakeCloudinaryService: FakeCloudinaryService
    ): CloudinaryService
}