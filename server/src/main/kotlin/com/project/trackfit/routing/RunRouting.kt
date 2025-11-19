package com.project.trackfit.routing

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.serialization.json.Json
import com.project.trackfit.model.client.run.RunRequestDto
import com.project.trackfit.model.mapper.toRunResponse
import model.server.run.RunResponse
import com.project.trackfit.repository.RunRepository
import io.ktor.server.engine.logError
import java.io.File

fun Application.configureRunRouting(
    repository: RunRepository
) {
    routing {
        // Serve uploaded files under /uploads
        staticFiles("/uploads", File("uploads"))

        //Applications Running
        get(path = "/runs") {
            try {
                val allRuns = repository.allRuns()

                println(allRuns)

                call.respond(
                    status = HttpStatusCode.OK,
                    message = allRuns
                )
            } catch (t: Throwable) {
                logError(call = call, error = t)
            }
        }

        post("/run") {
            try {
                val runResponse = extractRunResponse(
                    call = call,
                    repository = repository
                )

                if (runResponse == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing RUN_DATA")
                } else {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = runResponse
                    )
                }
            } catch (t: Throwable) {
                logError(call = call, error = t)
            }
        }

        delete(path = "/run") {
            try {
                val userId = call.request.queryParameters["id"]
                if (!userId.isNullOrBlank()) {
                    repository.removeRun(id = userId)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            } catch (t: Throwable) {
                logError(call = call, error = t)
            }
        }
    }
}

private suspend fun extractRunResponse(
    call: RoutingCall,
    repository: RunRepository
): RunResponse? {
    var savedImageUrl: String? = null
    var runRequest: RunRequestDto? = null

    // Increase formFieldLimit if you expect large multipart bodies (in bytes)
    // 100 MB
    val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 100)
    println("MultipartData -----------------> $multipartData")
    multipartData.forEachPart { part ->
        println("Part -----------------> $part")
        when (part) {
            is PartData.FormItem -> {
                // Expect RUN_DATA as JSON string
                if (part.name == "RUN_DATA") {
                    println("MultipartDataResponse -----------------> ${part.value}")
                    val json = Json {
                        ignoreUnknownKeys = true
                    }
                    runRequest = json.decodeFromString<RunRequestDto>(part.value)
                    println("ClientData -----------> $runRequest")
                }
            }

            is PartData.FileItem -> {
                if (part.name == "MAP_PICTURE") {
                    println("MultipartFileResponse -----------------> $part")
                    // Use original file name or build one
                    val original = part.originalFileName ?: "run_${System.currentTimeMillis()}.jpg"
                    val uploadDir = File("uploads")
                    uploadDir.mkdirs()
                    val file = File(uploadDir, original)

                    // Recommended Ktor pattern: stream provider -> writeChannel -> copyAndClose
                    // This avoids loading whole file into memory.
                    part.provider().copyAndClose(file.writeChannel())

                    // Build URL that client can use. If testing on emulator, use 10.0.2.2 as host.
                    // Here I build from the request host/port/scheme.
                    val scheme = call.request.origin.scheme
                    val host = call.request.host()
                    val port = call.request.port()
                    savedImageUrl = "$scheme://$host:$port/uploads/${file.name}"
                }
            }

            else -> {}
        }

        part.dispose()
    }

    if (runRequest == null) {
        return null
    }

    // Save the URL into the RunResponse and persist
    val finalData = runRequest.toRunResponse(mapPictureUrl = savedImageUrl)
    println("FinalRunData ------------------> \n${Json.encodeToString<RunResponse>(finalData)}")
    repository.addRun(finalData)

    return finalData
}
