package com.langdy.langdy_task.controller

import com.langdy.langdy_task.controller.response.AvailableTeachersResponse
import com.langdy.langdy_task.service.TeacherAvailabilityService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/teachers")
class TeacherController(
    private val teacherAvailabilityService: TeacherAvailabilityService,
) {

    @GetMapping("/available")
    fun getAvailableTeachers(
        @RequestParam courseId: Long,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startAt: LocalDateTime,
    ): ResponseEntity<AvailableTeachersResponse> {
        val res = teacherAvailabilityService.getAvailableTeachers(courseId, startAt)
        return ResponseEntity.ok(res)
    }
}