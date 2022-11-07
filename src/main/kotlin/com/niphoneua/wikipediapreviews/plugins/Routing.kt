package com.niphoneua.wikipediapreviews.plugins

import com.niphoneua.wikipediapreviews.api
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        api()
    }
}
