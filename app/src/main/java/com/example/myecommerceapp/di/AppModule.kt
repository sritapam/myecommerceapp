package com.example.myecommerceapp.di

import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import com.henrypeya.core.model.domain.repository.cart.CartRepository
import com.henrypeya.core.model.domain.repository.order.OrderRepository
import com.henrypeya.core.model.domain.repository.product.ProductRepository
import com.henrypeya.core.model.domain.repository.user.UserRepository
import com.henrypeya.data.repository.auth.AuthRepositoryImpl
import com.henrypeya.data.repository.cart.CartRepositoryImpl
import com.henrypeya.data.repository.order.OrderRepositoryImpl
import com.henrypeya.data.repository.product.ProductRepositoryImpl
import com.henrypeya.data.repository.user.UserRepositoryImpl
import com.henrypeya.data.service.imageupload.CloudinaryService
import com.henrypeya.data.service.imageupload.CloudinaryServiceImpl
import com.henrypeya.feature_auth.ui.components.DefaultEmailValidator
import com.henrypeya.feature_auth.ui.components.EmailValidator
import com.henrypeya.library.utils.ResourceProvider
import com.henrypeya.library.utils.ResourceProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        fakeProductRepository: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindCloudinaryService(
        cloudinaryService: CloudinaryServiceImpl
    ): CloudinaryService

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

    @Binds
    @Singleton
    abstract fun bindResourceProvider(
        resourceProviderImpl: ResourceProviderImpl
    ): ResourceProvider

    @Binds
    @Singleton
    abstract fun bindEmailValidator(
        defaultEmailValidator: DefaultEmailValidator
    ): EmailValidator
}