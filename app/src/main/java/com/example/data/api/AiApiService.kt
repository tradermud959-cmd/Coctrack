package com.example.data.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiContent(
    val role: String? = null,
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

data class GeminiCandidate(
    val content: GeminiContent? = null
)

data class GroqRequest(
    val model: String = "llama3-8b-8192",
    val messages: List<GroqMessage>
)

data class GroqMessage(
    val role: String = "user",
    val content: String
)

data class GroqResponse(
    val choices: List<GroqChoice>? = null
)

data class GroqChoice(
    val message: GroqMessage? = null
)

interface AiApiService {
    @POST
    suspend fun generateWithGemini(
        @Url url: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse

    @POST("https://api.groq.com/openai/v1/chat/completions")
    suspend fun generateWithGroq(
        @Header("Authorization") authHeader: String,
        @Body request: GroqRequest
    ): GroqResponse
}
