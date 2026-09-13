package com.example.code_reviewer_bot.service

import com.example.code_reviewer_bot.client.OllamaClient
import com.example.code_reviewer_bot.dto.FixRequest
import com.example.code_reviewer_bot.dto.FixResponse
import com.example.code_reviewer_bot.dto.Issue
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class FixService(private val ollamaClient: OllamaClient) {

    private val objectMapper = ObjectMapper()
        .registerModule(com.fasterxml.jackson.module.kotlin.KotlinModule.Builder().build())

    private val schema = """
        {
            "type": "object",
            "properties": {
                "fixedCode": {"type": "string"}
            },
            "required": ["fixedCode"]
        }
    """.trimIndent()

    private fun fixRaw(code: String, language: String, issues: List<Issue>): String {
        val issuesText = issues.joinToString("\n") {
            "- [${it.severity}/${it.category}] ${it.function}: ${it.explanation}"
        }

        val prompt = """
            You will be given a complete $language source file and a list of issues found in it.
        
            Your task: return the ENTIRE corrected file, with ALL functions included,
            not just the parts you changed. The output must be a complete, compilable
            file containing every function from the original, with the listed issues fixed.
        
            Do not return a single expression or a partial snippet.
            Do not omit any function, even if it had no issues.
            Do not add explanations or commentary outside the code itself.
        
            Issues to fix:
            $issuesText
        
            Original complete file:
            $code
        
            Now return the complete corrected file:
        """.trimIndent()

        return ollamaClient.generate(prompt, schema)
    }

    fun fixCode(request: FixRequest): FixResponse {
        val raw = fixRaw(request.code, request.language, request.issues)
        return objectMapper.readValue(raw, FixResponse::class.java)
    }
}