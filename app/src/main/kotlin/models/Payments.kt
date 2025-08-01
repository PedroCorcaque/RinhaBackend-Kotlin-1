package br.com.pedrocorcaque.app.models

import kotlinx.serialization.Serializable

@Serializable
data class Payments (
    val correlationId: String,
    val amount: String
) { }