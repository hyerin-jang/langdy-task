package com.langdy.langdy_task.service.command

import java.time.LocalDateTime

data class CreateLessonCommand(
    val studentId: Long,
    val courseId: Long,
    val teacherId: Long,
    val startAt: LocalDateTime
)