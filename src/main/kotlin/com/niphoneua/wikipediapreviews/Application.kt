package com.niphoneua.wikipediapreviews

import com.niphoneua.wikipediapreviews.db.initDbConnection
import com.niphoneua.wikipediapreviews.plugins.configureRouting
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import space.jetbrains.api.runtime.ktorClientForSpace

@Suppress("unused")
fun Application.module() {
    initDbConnection()

    configureRouting()
}

val spaceHttpClient = ktorClientForSpace()

val config: Config by lazy { ConfigFactory.load() }
