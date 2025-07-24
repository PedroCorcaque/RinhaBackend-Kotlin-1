import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import org.junit.jupiter.api.Test
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.HttpMethod
import kotlinx.coroutines.runBlocking
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.random.Random
import kotlin.test.assertTrue

class PaymentsTest {

    private val client = HttpClient(Java)

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `payments endpoint success`() = runBlocking {
        val response: HttpResponse = client.request("http://localhost:9999/payments") {
            method = HttpMethod.Post
            headers {
                append("correlationId", Uuid.random().toString())
                append("amount", Random.nextDouble().toString())
            }
        }

        assertTrue { response.status.value in 200..299 }
    }

    @Test
    fun `payments endpoint missing correlationId`() = runBlocking {
        val response: HttpResponse = client.request("http://localhost:9999/payments") {
            method = HttpMethod.Post
            headers {
                append("amount", Random.nextDouble().toString())
            }
        }

        assertTrue { response.status.value == 400 }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `payments endpoint missing amount`() = runBlocking {
        val response: HttpResponse = client.request("http://localhost:9999/payments") {
            method = HttpMethod.Post
            headers {
                append("correlationId", Uuid.random().toString())
            }
        }

        assertTrue { response.status.value == 400 }
    }
}