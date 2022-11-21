package com.niphoneua.wikipediapreviews

import com.niphoneua.wikipediapreviews.db.getLastEtag
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import space.jetbrains.api.ExperimentalSpaceSdkApi
import space.jetbrains.api.runtime.Space
import space.jetbrains.api.runtime.helpers.RequestAdapter
import space.jetbrains.api.runtime.helpers.SpaceHttpResponse
import space.jetbrains.api.runtime.helpers.processPayload
import space.jetbrains.api.runtime.resources.applications
import space.jetbrains.api.runtime.types.InitPayload
import space.jetbrains.api.runtime.types.NewUnfurlQueueItemsPayload

private var lastEtag: Long? = null

@OptIn(ExperimentalSpaceSdkApi::class)
fun Routing.api() {
    post("api/space") {
        val ktorRequestAdapter = object : RequestAdapter {
            override suspend fun receiveText() =
                call.receiveText()

            override fun getHeader(headerName: String) =
                call.request.header(headerName)

            override suspend fun respond(httpStatusCode: Int, body: String) =
                call.respond(HttpStatusCode.fromValue(httpStatusCode), body)
        }

        // read and process the message payload
        Space.processPayload(ktorRequestAdapter, spaceHttpClient, AppInstanceStorage) {payload ->
            when (payload) {
                is InitPayload -> {
                    requestPermissions()
                    SpaceHttpResponse.RespondWithOk
                }
                is NewUnfurlQueueItemsPayload -> {
                    val spaceClient = clientWithClientCredentials()
                    call.application.environment.log.info(getLastEtag(spaceClient.appInstance.clientId).toString())

                    val queueApi = spaceClient.applications.unfurls.queue
                    var queueItems = queueApi.getUnfurlQueueItems(lastEtag, batchSize = 100)
                    while (queueItems.isNotEmpty()) {
                        queueItems.forEach { item ->
                            call.application.environment.log.info(provideUnfurlContent(item, spaceClient))
                        }
                        lastEtag = queueItems.last().etag
                        queueItems = queueApi.getUnfurlQueueItems(lastEtag, batchSize = 100)
                    }
                    SpaceHttpResponse.RespondWithOk
                }
                else -> {
                    call.respond(HttpStatusCode.OK)
                    SpaceHttpResponse.RespondWithOk
                }
            }
        }
    }
}
