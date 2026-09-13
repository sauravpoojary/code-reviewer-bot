package com.example.code_reviewer_bot.dto

data class FixRequest(
    val code: String,
    val language: String,
    val issues: List<Issue>
)
