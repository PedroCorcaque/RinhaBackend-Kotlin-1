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
                // TODO: works with the payment
                call.respond(HttpStatusCode.OK)
            } catch (ex: IllegalStateException) {
                call.respond(HttpStatusCode.BadRequest)
            } catch (ex: SerializationException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/payments-summary") {
            val startTimestamp: String? = call.request.queryParameters["from"]
            val endTimestamp: String? = call.request.queryParameters["to"]

            if (startTimestamp == null && endTimestamp == null)
            {
                // TODO: return all time data and fill paymentsSummaryResponse
            }
            else
            {
                // TODO: search the data between start and end timestamp and fill paymentsSummaryResponse
            }
            var paymentsSummaryResponse = PaymentsSummaryResponse(null, null)
            call.respond(paymentsSummaryResponse)
        }
    }
}