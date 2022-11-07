package com.niphoneua.wikipediapreviews

import io.ktor.http.*
import space.jetbrains.api.runtime.helpers.unfurl
import space.jetbrains.api.runtime.resources.applications
import space.jetbrains.api.runtime.types.*

suspend fun provideUnfurlContent(item: ApplicationUnfurlQueueItem) {
//    val url = Url(item.target)
//    val parts = url.encodedPath.split('/').drop(1)

    val message = "Hello"

    // Build link preview with message constructor DSL
    val content: ApplicationUnfurlContent.Message = unfurl {
        outline(
            MessageOutline(
                ApiIcon("slack"),
                "Text"
            )
        )
        section {
            text(message)
            text("[View message](${item.target})")
        }
    }
    spaceClient.applications.unfurls.queue.postUnfurlsContent(
        listOf(ApplicationUnfurl(item.id, content))
    )
}
