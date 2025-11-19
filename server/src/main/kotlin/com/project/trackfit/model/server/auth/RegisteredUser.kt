package com.project.trackfit.model.server.auth

data class RegisteredUser(
    val userId: String,
    val email: String,
    val password: String
)
