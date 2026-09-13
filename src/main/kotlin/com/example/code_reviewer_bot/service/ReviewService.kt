package com.example.code_reviewer_bot.service

import com.example.code_reviewer_bot.client.OllamaClient
import com.example.code_reviewer_bot.dto.IssueCategory
import com.example.code_reviewer_bot.dto.ReviewResponse
import com.example.code_reviewer_bot.dto.Severity
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class ReviewService(private val ollamaClient: OllamaClient) {

    private val objectMapper = ObjectMapper()
        .registerModule(com.fasterxml.jackson.module.kotlin.KotlinModule.Builder().build())

    private val schema = """
        {
            "type": "object",
            "properties": {
                "issues": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "function": {"type": "string"},
                            "severity": {"type": "string", "enum": ["LOW", "MEDIUM", "HIGH"]},
                            "category": {"type": "string", "enum": ["BUG", "SECURITY", "STYLE"]},
                            "explanation": {"type": "string"}
                        },
                        "required": ["function", "severity", "category", "explanation"]
                    }
                }
            },
            "required": ["issues"]
        }
    """.trimIndent()

    private fun reviewRaw(code: String, language: String): String {
        val prompt = """
            Review this $language code for bugs, security issues, and bad practices.
            For each issue, identify the function it's in, its severity, and a short explanation.

            Severity rules:
            - HIGH: anything that could leak secrets/credentials, cause data loss, or crash in production
            - MEDIUM: logic bugs that produce wrong results but don't crash or leak data
            - LOW: style, readability, or minor robustness issues

            Code:
            $code
        """.trimIndent()

        return ollamaClient.generate(prompt, schema)
    }

    fun review(code: String, language: String): ReviewResponse {
        val raw = reviewRaw(code, language)
        val res = objectMapper.readValue(raw, ReviewResponse::class.java)

        val updatedIssues = res.issues.map {
            if (it.category == IssueCategory.SECURITY) it.copy(severity = Severity.HIGH) else it
        }
        val dedupedIssues = updatedIssues.distinctBy { it.function to it.category }

        return ReviewResponse(dedupedIssues)
    }
}