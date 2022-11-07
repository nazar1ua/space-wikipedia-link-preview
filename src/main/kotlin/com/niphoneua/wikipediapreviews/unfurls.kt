package com.niphoneua.wikipediapreviews

import io.ktor.http.*
import space.jetbrains.api.runtime.types.ApplicationUnfurlQueueItem

/*suspend*/ fun provideUnfurlContent(item: ApplicationUnfurlQueueItem, spaceUserId: String) {
    val url = Url(item.target)
    val parts = url.encodedPath.split('/').dropWhile { it != "archives" }.drop(1)



    /*val message = try {
        fetchMessage(channelId, messageId, threadTs, tokens.accessToken)
    } ?: return

    val authorName = fetchAuthorName(tokens.accessToken, message.user)
    // Build link preview with message constructor DSL
    val content: ApplicationUnfurlContent.Message = unfurl {
        outline(
            MessageOutline(
                ApiIcon("slack"),
                "*$authorName* in $channelLink"
            )
        )
        section {
            text(message.text)
            text("[View message](${item.target})")
        }
    }
    spaceClient.applications.unfurls.queue.postUnfurlsContent(
        listOf(ApplicationUnfurl(item.id, content))
    )*/
}
