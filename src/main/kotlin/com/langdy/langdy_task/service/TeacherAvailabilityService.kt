package com.langdy.langdy_task.service

import com.langdy.langdy_task.global.policy.TimePolicy
import com.langdy.langdy_task.controller.response.AvailableTeachersResponse
import com.langdy.langdy_task.repository.LessonQueryRepository
import com.langdy.langdy_task.repository.TeacherRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TeacherAvailabilityService(
    private val lessonQueryRepository: LessonQueryRepository,
    private val teacherRepository: TeacherRepository,
) {
    fun getAvailableTeachers(courseId: Long, startAt: LocalDateTime): AvailableTeachersResponse {
        TimePolicy.validateStartAt(startAt)

        val availableTeacherIds = lessonQueryRepository.findAvailableTeachers(courseId, startAt)
        val teachers = teacherRepository.findAllById(availableTeacherIds)
            .sortedBy { it.id }
            .map { AvailableTeachersResponse.TeacherDto(id = it.id, name = it.name) }

        return AvailableTeachersResponse(
            courseId = courseId,
            startAt = startAt,
            teachers = teachers
        )
    }
}