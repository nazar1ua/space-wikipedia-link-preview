package com.niphoneua.wikipediapreviews

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import space.jetbrains.api.runtime.helpers.readPayload
import space.jetbrains.api.runtime.helpers.verifyWithPublicKey
import space.jetbrains.api.runtime.resources.applications
import space.jetbrains.api.runtime.types.ChatMessage
import space.jetbrains.api.runtime.types.MessagePayload
import space.jetbrains.api.runtime.types.NewUnfurlQueueItemsPayload
import space.jetbrains.api.runtime.types.ProfileIdentifier

private var lastEtag: Long? = null

fun Routing.api() {
    post("api/space") {
        // read request body
        val body = call.receiveText()

        // verify the request
        val signature = call.request.header("X-Space-Public-Key-Signature")
        val timestamp = call.request.header("X-Space-Timestamp")?.toLongOrNull()
        if (signature.isNullOrBlank() || timestamp == null || !spaceClient.verifyWithPublicKey(
                body,
                timestamp,
                signature
            )
        ) {
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }

        // read and process the message payload
        when (val payload = readPayload(body)) {
            is MessagePayload -> {
                if ((payload.message.body as? ChatMessage.Text)?.text == "init") {
                    commandInit(payload.userId)
                }
                call.respond(HttpStatusCode.OK, "")
            }
            is NewUnfurlQueueItemsPayload -> {
                val queueApi = spaceClient.applications.unfurls.queue
                var queueItems = queueApi.getUnfurlQueueItems(lastEtag, batchSize = 100)
//                val regex = """https?:\/\/[a-z]{0,2}\.wikipedia.org\/wiki\/[a-z]{1,50}""".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))
                while (queueItems.isNotEmpty()) {
                    queueItems.forEach { item ->
                        provideUnfurlContent(item)
                        /*if (regex.containsMatchIn(item.target)) {
                        }*/
                    }
                    lastEtag = queueItems.last().etag
                    queueItems = queueApi.getUnfurlQueueItems(lastEtag, batchSize = 100)
                }
            }
        }
    }
}
