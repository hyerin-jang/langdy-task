package com.langdy.langdy_task.controller.response

import java.time.LocalDateTime

data class AvailableTeachersResponse(
    val courseId: Long,
    val startAt: LocalDateTime,
    val teachers: List<TeacherDto>,
) {
    data class TeacherDto(
        val id: Long,
        val name: String,
    )
}