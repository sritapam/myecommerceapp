package com.example.myecommerceapp.di

import com.example.myecommerceapp.data.AuthRepositoryImpl
import com.example.myecommerceapp.data.FakeProductRepository
import com.example.myecommerceapp.domain.AuthRepository
import com.example.myecommerceapp.domain.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    // Hilt: "Cuando alguien pida AuthRepository, dale una instancia de AuthRepositoryImpl"
    @Binds
    @Singleton // Asegura que solo haya una instancia de AuthRepository durante la vida de la app
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton // Asegura que solo haya una instancia de ProductRepository durante la vida de la app
    abstract fun bindProductRepository(
        fakeProductRepository: FakeProductRepository
    ): ProductRepository
}