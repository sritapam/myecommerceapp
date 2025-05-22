package com.example.myecommerceapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Le dice a Hilt que las dependencias de este módulo viven mientras la app esté viva
object AppModuleProviders { // <--- Importante: Usamos 'object' para un módulo de `@Provides`

    // Este método le dice a Hilt cómo obtener un 'Context'
    @Provides
    @Singleton // Asegura que solo se cree una única instancia de Context para toda la app
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        // Hilt inyectará el Context de la aplicación aquí
        // Nosotros solo lo "re-exportamos" para que otras clases que lo necesiten puedan acceder a él
        return context
    }
}