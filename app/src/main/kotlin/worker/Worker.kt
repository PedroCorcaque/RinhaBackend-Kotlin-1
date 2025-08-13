package br.com.pedrocorcaque.app.worker

import br.com.pedrocorcaque.app.models.Payments
import io.lettuce.core.RedisClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

suspend fun processPayment(
    payment: Payments,
    client: HttpClient,
    defaultUrl: String,
    fallbackUrl: String,
    redis: io.lettuce.core.api.sync.RedisCommands<String, String>
) {
    try {
        val defaultResponse = client.post(defaultUrl) {
            contentType(ContentType.Application.Json)
            setBody(payment)
        }

        if (defaultResponse.status.value in 500..599) {
            println("Default processor is down (${defaultResponse.status}). Trying fallback...")
            val fallbackResponse = client.post(fallbackUrl) {
                contentType(ContentType.Application.Json)
                setBody(payment)
            }

            if (fallbackResponse.status.isSuccess()) {
                println("Payment processed successfully in fallback: $payment")
            } else {
                println("Failed in fallback: ${fallbackResponse.status}")
                redis.lpush("queue:payments", Json.encodeToString(payment))
            }
        } else if (defaultResponse.status.isSuccess()) {
            println("Payment processed successfully in default: $payment")
        } else {
            println("Fail in default: ${defaultResponse.status}")
            redis.lpush("queue:payments", Json.encodeToString(payment))
        }
    } catch (e: Exception) {
        println("Error processing payment: ${e.message}")
        redis.lpush("queue:payments", Json.encodeToString(payment))
    }
}

fun main() {
    val redisHost = System.getenv("REDIS_HOST") ?: "localhost"
    val redisPort = System.getenv("REDIS_PORT") ?: "6379"
    val defaultUrl = System.getenv("PROCESSOR_DEFAULT_URL") ?: "http://payment-processor-default:8080"
    val fallbackUrl = System.getenv("PROCESSOR_FALLBACK_URL") ?: "http://payment-processor-fallback:8080"

    val redisClient = RedisClient.create("redis://$redisHost:$redisPort")
    val connection = redisClient.connect()
    val redis = connection.sync()

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    while (true) {
        val result = redis.brpop(0, "queue:payments")
        val jsonPayments = result?.getValueOrElse(null)

        if (jsonPayments != null) {
            try {
                val payment = Json.decodeFromString<Payments>(jsonPayments)
                println("Processing payment: $payment")
                kotlinx.coroutines.runBlocking {
                    processPayment(payment, client, defaultUrl, fallbackUrl, redis)
                }
            } catch (e: Exception) {
                println("Error processing payment: ${e.message}")
                redis.lpush("queue:payments", jsonPayments)
            }
        }
    }
}