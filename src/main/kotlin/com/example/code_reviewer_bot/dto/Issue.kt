package com.example.code_reviewer_bot.dto

data class Issue(
    val function: String,
    val severity: Severity,
    val category: IssueCategory,
    val explanation: String
)