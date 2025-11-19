package com.project.trackfit.routing

import com.project.trackfit.model.client.auth.login.LoginRequestDto
import com.project.trackfit.model.client.auth.register.RegisterRequestDto
import com.project.trackfit.repository.AuthRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.engine.logError
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureAuthRouting(
    repository: AuthRepository
) {
    routing {
        //Authentications
        post(path = "/register") {
            try {
                val request = call.receive<RegisterRequestDto>()
                val isUserRegistered = repository.registerUser(request = request)

                if (isUserRegistered) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            } catch (t: Throwable) {
                logError(call = call, error = t)
            }
        }

        post(path = "/login") {
            try {
                val request = call.receive<LoginRequestDto>()
                val loginResponse = repository.loginUser(request = request)

                println(loginResponse)

                call.respond(
                    status = HttpStatusCode.OK,
                    message = loginResponse
                )
            } catch (t: Throwable) {
                logError(call = call, error = t)
            }
        }
    }
}