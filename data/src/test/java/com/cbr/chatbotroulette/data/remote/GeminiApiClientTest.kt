package com.cbr.chatbotroulette.data.remote

import com.cbr.chatbotroulette.data.remote.model.Content
import com.cbr.chatbotroulette.data.remote.model.Part
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeminiApiClientTest {

    private fun createMockClient(responseBody: String, statusCode: HttpStatusCode = HttpStatusCode.OK): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        content = responseBody,
                        status = statusCode,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    @Test
    fun `generateContent returns success with valid response`() = runTest {
        val responseJson = """
            {
                "candidates": [
                    {
                        "content": {
                            "role": "model",
                            "parts": [{"text": "Hello from Gemini!"}]
                        }
                    }
                ]
            }
        """.trimIndent()

        val client = GeminiApiClient(createMockClient(responseJson))
        val contents = listOf(Content("user", listOf(Part("Hello"))))

        val result = client.generateContent(contents)

        assertTrue(result.isSuccess)
        val response = result.getOrNull()!!
        assertEquals("Hello from Gemini!", response.candidates?.first()?.content?.parts?.first()?.text)
    }

    @Test
    fun `generateContent returns failure when API returns error`() = runTest {
        val responseJson = """
            {
                "error": {
                    "code": 400,
                    "message": "Invalid request",
                    "status": "INVALID_ARGUMENT"
                }
            }
        """.trimIndent()

        val client = GeminiApiClient(createMockClient(responseJson))
        val contents = listOf(Content("user", listOf(Part("Hello"))))

        val result = client.generateContent(contents)

        assertTrue(result.isFailure)
        assertEquals("Invalid request", result.exceptionOrNull()?.message)
    }

    @Test
    fun `generateContent returns failure on network exception`() = runTest {
        val mockEngine = MockEngine {
            throw RuntimeException("Network error")
        }
        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val client = GeminiApiClient(httpClient)
        val contents = listOf(Content("user", listOf(Part("Hello"))))

        val result = client.generateContent(contents)

        assertTrue(result.isFailure)
    }

    @Test
    fun `generateContent returns success with empty candidates`() = runTest {
        val responseJson = """
            {
                "candidates": []
            }
        """.trimIndent()

        val client = GeminiApiClient(createMockClient(responseJson))
        val contents = listOf(Content("user", listOf(Part("Hello"))))

        val result = client.generateContent(contents)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.candidates?.isEmpty() == true)
    }
}
