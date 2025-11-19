package com.project.trackfit.repository

import com.project.trackfit.model.server.auth.LoginResponse
import com.project.trackfit.model.client.auth.login.LoginRequestDto
import com.project.trackfit.model.client.auth.register.RegisterRequestDto
import com.project.trackfit.model.server.auth.RegisteredUser
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

class AuthRepository {

    private val users = mutableListOf<RegisteredUser>()

    fun registerUser(request: RegisterRequestDto): Boolean {
        println("Users ---------------> \n$users\n\n")
        if (users.any { it.email == request.email }) throw IllegalArgumentException("User is already registered!")
        val registeredUser = RegisteredUser(
            userId = UUID.randomUUID().toString(),
            email = request.email,
            password = request.password
        )
        return users.add(registeredUser)
    }

    fun loginUser(request: LoginRequestDto): LoginResponse {
        println("Users ---------------> \n$users\n\n")
        val user = users.find { it.email == request.email }
        if (user == null) {
            throw IllegalArgumentException("User is not registered!")
        }
        return LoginResponse(
            accessToken = "${user.email}AccessToken12345",
            refreshToken = "${user.email}RefreshToken12345",
            accessTokenExpirationTimestamp = getExpiryTime(),
            userId = user.userId
        )
    }

    private fun getExpiryTime(): Long {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val threeMonthsLater = now.plusMonths(3)
        return threeMonthsLater.toInstant().toEpochMilli()
    }
}