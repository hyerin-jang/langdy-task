package com.langdy.langdy_task.controller.request

import com.langdy.langdy_task.service.command.CreateLessonCommand
import java.time.LocalDateTime

data class CreateLessonRequest(
    val courseId: Long,
    val teacherId: Long,
    val startAt: LocalDateTime
) {
    fun toCommand(studentId: Long): CreateLessonCommand {
        return CreateLessonCommand(
            studentId = studentId,
            courseId = courseId,
            teacherId = teacherId,
            startAt = startAt
        )
    }
}