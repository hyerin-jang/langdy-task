package com.langdy.langdy_task.global.exception

import com.langdy.langdy_task.global.response.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        e: BusinessException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            message = e.message ?: "Business error",
            path = request.requestURI
        )

        return ResponseEntity(response, HttpStatus.CONFLICT)
    }

    /**
     * DB unique constraint 충돌 (동시성 이슈)
     */
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrity(
        e: DataIntegrityViolationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            message = "Lesson already booked",
            path = request.requestURI
        )

        return ResponseEntity(response, HttpStatus.CONFLICT)
    }

    /**
     * 기타 예외
     */
    @ExceptionHandler(Exception::class)
    fun handleException(
        e: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            message = e.message ?: "Internal server error",
            path = request.requestURI
        )

        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}