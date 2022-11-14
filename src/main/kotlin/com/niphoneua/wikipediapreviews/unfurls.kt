package com.niphoneua.wikipediapreviews

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.helpers.unfurl
import space.jetbrains.api.runtime.resources.applications
import space.jetbrains.api.runtime.types.*

object NumberSerializer : KSerializer<Number> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Number", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Number) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Number {
        return decoder.decodeInt()
    }
}

suspend fun provideUnfurlContent(item: ApplicationUnfurlQueueItem, spaceClient: SpaceClient): String {
    val url = Url(item.target)
    val parts = url.encodedPath.split('/').drop(2)

    if (parts.isEmpty())
        return Exception("").toString()

    @Serializable
    data class WikiImage(
        val source: String,
        @Serializable(with = NumberSerializer::class)
        val width: Number,
        @Serializable(with = NumberSerializer::class)
        val height: Number
    )

    @Serializable
    data class WikiPage(
        @Serializable(with = NumberSerializer::class)
        val pageid: Number = 0,
        @Serializable(with = NumberSerializer::class)
        val ns: Number = 0,
        val title: String,
        val extract: String = "",
        val thumbnail: WikiImage = WikiImage(
            source = "",
            width = 0,
            height = 0
        ),
        val missing: String = ""
    )

    @Serializable
    data class WikiQuery(
        val pages: Map<String, WikiPage>
    )

    @Serializable
    data class WikiResponse(
        val batchcomplete: String,
        val query: WikiQuery
    )

    var response: WikiResponse

    try {
        response =
            spaceHttpClient.get("https://${url.host}/w/api.php") {
                url {
                    parameters.append("format", "json")
                    parameters.append("action", "query")
                    parameters.append("prop", "extracts|pageimages")
                    parameters.append("exintro", "")
                    parameters.append("explaintext", "")
                    parameters.append("redirects", "1")
                    parameters.append("titles", parts.first())
                    parameters.append("piprop", "thumbnail")
                    parameters.append("pithumbsize", "1000")
                }
            }.body()
    } catch (e: NoTransformationFoundException) {
        val responseToString: String =
            spaceHttpClient.get("https://${url.host}/w/api.php") {
                url {
                    parameters.append("format", "json")
                    parameters.append("action", "query")
                    parameters.append("prop", "extracts|pageimages")
                    parameters.append("exintro", "")
                    parameters.append("explaintext", "")
                    parameters.append("redirects", "1")
                    parameters.append("titles", parts.first())
                    parameters.append("piprop", "thumbnail")
                    parameters.append("pithumbsize", "1000")
                }
            }.body()
        val json = Json {
            ignoreUnknownKeys = true
        }
        response = json.decodeFromString(responseToString.trimIndent())
    }

    val pageMeta: WikiPage = response.query.pages.values.first()

    // Build link preview with message constructor DSL
    val content: ApplicationUnfurlContent.Message = unfurl {
        outline(
            MessageOutline(
                ApiIcon("book"),
                url.host
            )
        )
        section {
            text(pageMeta.title)
            text("${pageMeta.extract}${if (pageMeta.thumbnail.source.isEmpty()) "\n\n![Image is loading...]${pageMeta.thumbnail.source}" else ""}")
        }
    }

    spaceClient.applications.unfurls.queue.postUnfurlsContent(
        listOf(ApplicationUnfurl(item.id, content))
    )

    return "Success request"
}
