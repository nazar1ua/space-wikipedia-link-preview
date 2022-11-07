package com.niphoneua.wikipediapreviews

import space.jetbrains.api.runtime.SpaceAppInstance
import space.jetbrains.api.runtime.SpaceAuth
import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.ktorClientForSpace

val spaceAppInstance = SpaceAppInstance(
    clientId = "<Space app client id>",
    clientSecret = "<Space app client secret>",
    spaceServerUrl = "https://<your-Space-org>.jetbrains.space"
)

private val spaceHttpClient = ktorClientForSpace()

val spaceClient by lazy {
    SpaceClient(spaceHttpClient, spaceAppInstance, SpaceAuth.ClientCredentials())
}
