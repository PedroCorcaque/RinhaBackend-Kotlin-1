package br.com.pedrocorcaque.app.models

import kotlinx.serialization.Serializable

@Serializable
data class PaymentsSummaryResponse (
    val default: DefaultProcessorResponse?,
    val fallback: FallbackProcessorResponse?
) {
    @Serializable
    data class DefaultProcessorResponse(
        val totalRequests: Int,
        val totalAmount: Double
    )

    @Serializable
    data class FallbackProcessorResponse(
        val totalRequests: Int,
        val totalAmount: Double
    )
}