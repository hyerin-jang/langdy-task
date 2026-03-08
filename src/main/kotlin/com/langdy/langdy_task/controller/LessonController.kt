package com.langdy.langdy_task.controller

import com.langdy.langdy_task.controller.request.CreateLessonRequest
import com.langdy.langdy_task.controller.response.CreateLessonResponse
import com.langdy.langdy_task.service.LessonService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/lessons")
class LessonController(
    private val lessonService: LessonService
) {
    @PostMapping
    fun createLesson(
        @RequestHeader("X-Student-Id") studentId: Long,
        @RequestBody request: CreateLessonRequest
    ): ResponseEntity<CreateLessonResponse> {
        val command = request.toCommand(studentId)
        val response = lessonService.createLesson(command)

        return ResponseEntity.ok(response)
    }
}