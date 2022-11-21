package com.niphoneua.wikipediapreviews

import space.jetbrains.api.runtime.helpers.ProcessingScope
import space.jetbrains.api.runtime.resources.applications
import space.jetbrains.api.runtime.types.ApplicationIdentifier
import space.jetbrains.api.runtime.types.GlobalPermissionContextIdentifier

suspend fun ProcessingScope.requestPermissions() {
    val spaceClient = clientWithClientCredentials()

    spaceClient.applications.authorizations.authorizedRights.requestRights(
        ApplicationIdentifier.Me,
        GlobalPermissionContextIdentifier,
        listOf("Unfurl.App.ProvideAttachment")
    )
    spaceClient.applications.unfurls.domains.updateUnfurledDomains(listOf(
        "wikipedia.org",
    ))
}
