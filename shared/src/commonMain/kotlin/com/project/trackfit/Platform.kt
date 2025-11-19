package com.project.trackfit

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform