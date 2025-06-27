package com.henrypeya.data

import com.henrypeya.core.model.domain.repository.auth.AuthRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val _isLoggedIn = MutableStateFlow(false)

    @Volatile
    private var registeredUsers = mutableListOf<Pair<String, String>>().apply {
        add(AuthConstants.TEST_EMAIL to AuthConstants.TEST_PASSWORD) // Agregamos el usuario de prueba al inicio
    }

    override suspend fun login(email: String, password: String): Boolean {
        delay(500)

        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        println("AuthRepositoryImpl: Intentando login para email: '$trimmedEmail', password: '$trimmedPassword'")
        println("AuthRepositoryImpl: Usuarios registrados actualmente: ${registeredUsers.joinToString { "(${it.first}, ${it.second})" }}")

        val userExists = registeredUsers.any { it.first == trimmedEmail && it.second == trimmedPassword }

        if (userExists) {
            _isLoggedIn.value = true
            println("AuthRepositoryImpl: Login exitoso para '$trimmedEmail'")
            return true
        }
        println("AuthRepositoryImpl: Credenciales incorrectas para '$trimmedEmail'")
        return false
    }

    override fun logout() {
        _isLoggedIn.value = false
        println("AuthRepositoryImpl: Usuario ha cerrado sesión.")
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return _isLoggedIn.asStateFlow()
    }

    override suspend fun register(email: String, password: String): Boolean {
        delay(500)

        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        println("AuthRepositoryImpl: Intentando registrar usuario: '$trimmedEmail'")

        if (registeredUsers.any { it.first == trimmedEmail }) {
            println("AuthRepositoryImpl: Registro fallido: Usuario '$trimmedEmail' ya existe.")
            return false
        }

        registeredUsers.add(trimmedEmail to trimmedPassword)
        println("AuthRepositoryImpl: Usuario registrado exitosamente: '$trimmedEmail'. Total de usuarios: ${registeredUsers.size}")
        println("AuthRepositoryImpl: Usuarios después del registro: ${registeredUsers.joinToString { "(${it.first}, ${it.second})" }}")
        return true
    }

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<Boolean>) {
        TODO("Not yet implemented")
    }
}
