package com.langdy.langdy_task.repository

import com.langdy.langdy_task.entity.Lesson
import com.langdy.langdy_task.entity.Student
import com.langdy.langdy_task.entity.Teacher
import com.langdy.langdy_task.entity.enums.LessonStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface LessonRepository : JpaRepository<Lesson, Long> {
    fun existsByTeacherAndStartAtAndStatus(
        teacher: Teacher,
        startAt: LocalDateTime,
        status: LessonStatus
    ): Boolean

    fun existsByStudentAndStartAtAndStatus(
        student: Student,
        startAt: LocalDateTime,
        status: LessonStatus
    ): Boolean
}