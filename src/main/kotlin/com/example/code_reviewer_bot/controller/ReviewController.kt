package com.example.code_reviewer_bot.controller

import com.example.code_reviewer_bot.dto.FixRequest
import com.example.code_reviewer_bot.dto.FixResponse
import com.example.code_reviewer_bot.dto.ReviewRequest
import com.example.code_reviewer_bot.dto.ReviewResponse
import com.example.code_reviewer_bot.service.FixService
import com.example.code_reviewer_bot.service.ReviewService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ReviewController(
    private val reviewService: ReviewService,
    private val fixService: FixService
) {

    @PostMapping("/api/review")
    fun review(@RequestBody request: ReviewRequest): ReviewResponse {
        return reviewService.review(request.code, request.language)
    }

    @PostMapping("/api/fix")
    fun fix(@RequestBody request: FixRequest): FixResponse {
        return fixService.fixCode(request)
    }
}