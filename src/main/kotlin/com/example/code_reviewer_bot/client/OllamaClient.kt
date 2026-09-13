package com.example.code_reviewer_bot.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class OllamaClient {

    private val httpClient = HttpClient.newHttpClient()
    private val objectMapper = ObjectMapper()

    fun generate(prompt: String, schema: String): String {
        val body = """
            {
              "model": "llama3.2",
              "prompt": ${toJsonString(prompt)},
              "format": $schema,
              "stream": false,
              "options": {
                "num_predict": 1024
              }
            }
        """.trimIndent()

        val request = HttpRequest.newBuilder().uri(URI.create("http://localhost:11434/api/generate"))
            .header("content-type", "application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        val node = objectMapper.readTree(response.body())
        return node.get("response").asText()
    }

    private fun toJsonString(s: String): String {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\""
    }
}