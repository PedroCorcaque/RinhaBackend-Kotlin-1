package br.com.pedrocorcaque.app.models

import kotlinx.serialization.Serializable

@Serializable
data class Payments (
    val properties: Properties
) {
    @Serializable
    data class Properties(
        val correlationId: String,
        val amount: String
    )
}