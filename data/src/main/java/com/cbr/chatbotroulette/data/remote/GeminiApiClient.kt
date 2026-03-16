package com.cbr.chatbotroulette.data.remote

import com.cbr.chatbotroulette.data.BuildConfig
import com.cbr.chatbotroulette.data.remote.model.Content
import com.cbr.chatbotroulette.data.remote.model.GeminiRequest
import com.cbr.chatbotroulette.data.remote.model.GeminiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

open class GeminiApiClient(
    private val httpClient: HttpClient
) {
    private val apiKey = BuildConfig.API_KEY
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

    open suspend fun generateContent(contents: List<Content>): Result<GeminiResponse> {
        return try {
            val response: GeminiResponse = httpClient.post("$baseUrl?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(GeminiRequest(contents = contents))
            }.body()

            if (response.error != null) {
                Result.failure(Exception(response.error.message))
            } else {
                Result.success(response)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
