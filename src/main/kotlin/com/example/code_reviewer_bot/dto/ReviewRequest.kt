package com.example.code_reviewer_bot.dto

data class ReviewRequest(
    val code: String,
    val language: String = "Kotlin"
)
