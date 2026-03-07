package com.langdy.langdy_task.global.response

import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val message: String,
    val path: String?
)