package com.niphoneua.wikipediapreviews.db

import org.jetbrains.exposed.sql.AndOp
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import space.jetbrains.api.runtime.types.RefreshTokenPayload

data class RefreshTokenAndScope(
    val refreshToken: String,
    val scope: String
)

@Suppress("unused")
fun findRefreshTokenData(clientIdParam: String, userIdParam: String) = transaction {
    with(RefreshToken) {
        slice(refreshToken, scope).select {
            clientIdAndUserMatch(clientIdParam, userIdParam)
        }
            .map { RefreshTokenAndScope(it[refreshToken], it[scope]) }
            .firstOrNull()
    }
}

private fun SqlExpressionBuilder.clientIdAndUserMatch(
    clientIdParam: String,
    userIdParam: String
) = AndOp(
    listOf(
        RefreshToken.clientId eq clientIdParam,
        RefreshToken.userId eq userIdParam,
    )
)

@Suppress("unused")
fun saveRefreshTokenData(payload: RefreshTokenPayload) = transaction {
    with(RefreshToken) {
        replace {
            it[clientId] = payload.clientId
            it[userId] = payload.userId
            it[refreshToken] = payload.refreshToken
            it[scope] = payload.scope
        }
    }
}

fun getLastEtag(clientId: String): Number? = transaction {
    return@transaction AppInstallation.select { AppInstallation.clientId eq clientId }.firstOrNull()?.get(AppInstallation.lastEtag)
}

fun setLastEtag() {}
