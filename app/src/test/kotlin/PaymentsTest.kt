import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import org.junit.jupiter.api.Test
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.random.Random
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlin.test.assertTrue
import br.com.pedrocorcaque.app.models.Payments
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

class PaymentsTest {

    private val client = HttpClient(Java) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `payments endpoint success`() = runBlocking {
        val response = client.post("http://localhost:9999/payments") {
            contentType(io.ktor.http.ContentType.Application.Json)
            setBody(Payments(Uuid.random().toString(), Random.nextDouble().toString()))
        }

        assertTrue { response.status.value in 200..299 }
    }

    @Test
    fun `payments endpoint missing correlationId`() = runBlocking {
        val response: HttpResponse = client.request("http://localhost:9999/payments") {
            method = HttpMethod.Post
            contentType(io.ktor.http.ContentType.Application.Json)
            setBody("""{ "amount": "12.34" }""")
        }

        assertTrue { response.status.value == 400 }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `payments endpoint missing amount`() = runBlocking {
        val response: HttpResponse = client.request("http://localhost:9999/payments") {
            method = HttpMethod.Post
            contentType(io.ktor.http.ContentType.Application.Json)
            setBody("""{ "correlationId": "${Uuid.random()}" }""")
        }

        assertTrue { response.status.value == 400 }
    }
}