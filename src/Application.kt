package com.example.t

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.netty.*
import io.ktor.util.*
import java.io.File
import java.io.FileInputStream
import java.util.*
import io.ktor.jackson.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        get("{path}") {
            val filePath = call.parameters["path"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Bad path"))
            val resultObject = File(filePath)
            if (resultObject.isDirectory) {
                call.respond(resultObject.list())
            } else {
                val fileInputStream = FileInputStream(filePath)
                val fileData = fileInputStream.readAllBytes()
                val base64InputStream = Base64.getEncoder().encode(fileData)
                call.respond(mapOf("content" to base64InputStream.toString()))
            }
        }
    }

}


