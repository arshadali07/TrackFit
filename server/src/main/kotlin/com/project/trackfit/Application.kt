package com.project.trackfit

import com.project.trackfit.repository.AuthRepository
import com.project.trackfit.repository.RunRepository
import com.project.trackfit.routing.configureAuthRouting
import com.project.trackfit.routing.configureRunRouting
import com.project.trackfit.routing.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(
        factory = Netty,
        port = SERVER_PORT,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureAuthRouting(repository = AuthRepository())
    configureRunRouting(repository = RunRepository())
}