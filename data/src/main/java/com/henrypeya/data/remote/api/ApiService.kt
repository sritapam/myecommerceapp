package com.henrypeya.data.remote.api

import com.henrypeya.data.remote.dto.food.FoodResponseDto
import com.henrypeya.data.remote.dto.order.OrderRequestDto
import com.henrypeya.data.remote.dto.order.OrderResponseDto
import com.henrypeya.data.remote.dto.user.LoginRequestDto
import com.henrypeya.data.remote.dto.user.LoginResponseDto
import com.henrypeya.data.remote.dto.user.RegisterRequestDto
import com.henrypeya.data.remote.dto.user.RegisterResponseDto
import com.henrypeya.data.remote.dto.user.UpdateUserProfileRequestDto
import com.henrypeya.data.remote.dto.user.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("users/login")
    suspend fun loginUser(@Body request: LoginRequestDto): LoginResponseDto

    @POST("users/register")
    suspend fun registerUser(@Body request: RegisterRequestDto): RegisterResponseDto

    @GET("users/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): UserDto

    @PUT("users/update/{email}")
    suspend fun updateUserInfo(
        @Path("email") email: String,
        @Body request: UpdateUserProfileRequestDto
    ): UserDto

    @GET("foods")
    suspend fun getAllFoods(): List<FoodResponseDto>

    @POST("orders")
    suspend fun createOrder(@Body request: OrderRequestDto): OrderResponseDto

    @GET("orders")
    suspend fun getAllOrders(): List<OrderResponseDto>
}