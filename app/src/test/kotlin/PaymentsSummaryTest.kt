import br.com.pedrocorcaque.app.models.PaymentsSummaryResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class PaymentsSummaryTest {

    private val client = HttpClient(Java) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    @Test
    fun `payments-summary endpoint success`() = runBlocking {
        val response: HttpResponse = client.request("http://localhost:9999/payments-summary") {
            method = HttpMethod.Get
            url {
                parameters.append("from", "2025-07-24T20:29:12.000Z")
                parameters.append("to", "2025-07-24T20:30:12.000Z")
            }
        }

        val paymentsSummaryResponse: PaymentsSummaryResponse = response.body()

        assertTrue { response.status.value == 200 }
        assertTrue { paymentsSummaryResponse.default != null }
        assertTrue { paymentsSummaryResponse.fallback != null }
    }
}