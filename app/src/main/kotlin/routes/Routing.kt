package br.com.pedrocorcaque.app.controllers

import br.com.pedrocorcaque.app.models.Payments
import br.com.pedrocorcaque.app.models.PaymentsSummaryResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.SerializationException

fun Application.configureRouting() {
    routing {
        staticResources("static", "static")

        post("/payments") {
            try {
                val payment = call.receive<Payments>()
                if (payment.correlationId.isNullOrBlank() || payment.amount.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                call.respond(HttpStatusCode.OK)
            } catch (ex: IllegalStateException) {
                ex.printStackTrace()
                call.respond(HttpStatusCode.BadRequest)
            } catch (ex: SerializationException) {
                ex.printStackTrace()
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/payments-summary") {
            try {
                val response = PaymentsSummaryResponse(
                    default = PaymentsSummaryResponse.DefaultProcessorResponse(
                        totalRequests = 0,
                        totalAmount = 0.0
                    ),
                    fallback = PaymentsSummaryResponse.FallbackProcessorResponse(
                        totalRequests = 0,
                        totalAmount = 0.0
                    )
                )
                call.respond(response)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}