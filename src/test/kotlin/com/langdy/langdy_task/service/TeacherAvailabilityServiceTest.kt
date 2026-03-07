package com.langdy.langdy_task.service

import com.langdy.langdy_task.entity.Teacher
import com.langdy.langdy_task.repository.LessonQueryRepository
import com.langdy.langdy_task.repository.TeacherRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.Test

class TeacherAvailabilityServiceTest {
    private val lessonQueryRepository: LessonQueryRepository = mockk()
    private val teacherRepository: TeacherRepository = mockk()
    private val service = TeacherAvailabilityService(
        lessonQueryRepository,
        teacherRepository
    )

    @Test
    @DisplayName("특정 시간에 수업 가능한 선생님 목록을 조회한다")
    fun getAvailableTeachers() {
        // given
        val courseId = 1L
        val startAt = LocalDateTime.of(2025, 3, 6, 10, 0)

        val teacher1 = Teacher(id = 1, name = "teacher1")
        val teacher2 = Teacher(id = 2, name = "teacher2")

        every {
            lessonQueryRepository.findAvailableTeachers(courseId, startAt)
        } returns listOf(1L, 2L)

        every {
            teacherRepository.findAllById(listOf(1L, 2L))
        } returns listOf(teacher1, teacher2)

        // when
        val result = service.getAvailableTeachers(courseId, startAt)

        // then
        assertThat(result.courseId).isEqualTo(courseId)
        assertThat(result.startAt).isEqualTo(startAt)

        assertThat(result.teachers).hasSize(2)

        assertThat(result.teachers[0].id).isEqualTo(1)
        assertThat(result.teachers[0].name).isEqualTo("teacher1")

        assertThat(result.teachers[1].id).isEqualTo(2)
        assertThat(result.teachers[1].name).isEqualTo("teacher2")
    }

    @Test
    @DisplayName("수업 시작 시간이 00 또는 30이 아니면 예외가 발생한다")
    fun invalidStartTime() {
        val courseId = 1L
        val startAt = LocalDateTime.of(2025, 3, 6, 10, 15)

        assertThrows<IllegalArgumentException> {
            service.getAvailableTeachers(courseId, startAt)
        }
    }
}