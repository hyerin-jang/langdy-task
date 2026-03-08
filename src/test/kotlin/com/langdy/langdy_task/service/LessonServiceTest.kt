package com.langdy.langdy_task.service

import com.langdy.langdy_task.entity.Course
import com.langdy.langdy_task.entity.Lesson
import com.langdy.langdy_task.entity.Student
import com.langdy.langdy_task.entity.Teacher
import com.langdy.langdy_task.entity.enums.LessonStatus
import com.langdy.langdy_task.entity.enums.Os
import com.langdy.langdy_task.global.exception.StudentAlreadyBookedException
import com.langdy.langdy_task.global.exception.TeacherAlreadyBookedException
import com.langdy.langdy_task.repository.CourseRepository
import com.langdy.langdy_task.repository.LessonRepository
import com.langdy.langdy_task.repository.StudentRepository
import com.langdy.langdy_task.repository.TeacherRepository
import com.langdy.langdy_task.service.command.CreateLessonCommand
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.Optional

class LessonServiceTest {
    private val lessonRepository: LessonRepository = mockk()
    private val teacherRepository: TeacherRepository = mockk()
    private val studentRepository: StudentRepository = mockk()
    private val courseRepository: CourseRepository = mockk()

    private val lessonService = LessonService(
        lessonRepository = lessonRepository,
        teacherRepository = teacherRepository,
        studentRepository = studentRepository,
        courseRepository = courseRepository,
    )

    @Test
    @DisplayName("수업 신청이 정상적으로 완료되면 lesson을 생성한다")
    fun createLesson_success() {
        // given
        val studentId = 1L
        val teacherId = 2L
        val courseId = 3L
        val startAt = LocalDateTime.of(2026, 3, 7, 10, 0)
        val endAt = startAt.plusMinutes(20)

        val student = Student(id = studentId, name = "student1", os = Os.IOS)
        val teacher = Teacher(id = teacherId, name = "teacher1")
        val course = Course(id = courseId, name = "English")

        val savedLesson = Lesson(
            id = 100L,
            course = course,
            teacher = teacher,
            student = student,
            status = LessonStatus.BOOKED,
            startAt = startAt,
            endAt = endAt
        )

        every { teacherRepository.findById(teacherId) } returns Optional.of(teacher)
        every { studentRepository.findById(studentId) } returns Optional.of(student)
        every { courseRepository.findById(courseId) } returns Optional.of(course)

        every {
            lessonRepository.existsByTeacherAndStartAtAndStatus(
                teacher,
                startAt,
                LessonStatus.BOOKED
            )
        } returns false

        every {
            lessonRepository.existsByStudentAndStartAtAndStatus(
                student,
                startAt,
                LessonStatus.BOOKED
            )
        } returns false

        every { lessonRepository.save(any()) } returns savedLesson

        // when
        val command = CreateLessonCommand(
            studentId = studentId,
            courseId = courseId,
            teacherId = teacherId,
            startAt = startAt
        )
        val result = lessonService.createLesson(command)

        // then
        assertThat(result.lessonId).isEqualTo(100L)

        verify(exactly = 1) {
            lessonRepository.existsByTeacherAndStartAtAndStatus(
                teacher,
                startAt,
                LessonStatus.BOOKED
            )
        }

        verify(exactly = 1) {
            lessonRepository.existsByStudentAndStartAtAndStatus(
                student,
                startAt,
                LessonStatus.BOOKED
            )
        }

        verify(exactly = 1) { lessonRepository.save(any()) }
    }

    @Test
    @DisplayName("선생님이 같은 시간에 이미 예약되어 있으면 예외가 발생한다")
    fun createLesson_fail_whenTeacherAlreadyBooked() {
        // given
        val studentId = 1L
        val teacherId = 2L
        val courseId = 3L
        val startAt = LocalDateTime.of(2026, 3, 7, 10, 0)

        val student = Student(id = studentId, name = "student1", os = Os.IOS)
        val teacher = Teacher(id = teacherId, name = "teacher1")
        val course = Course(id = courseId, name = "English")

        every { teacherRepository.findById(teacherId) } returns Optional.of(teacher)
        every { studentRepository.findById(studentId) } returns Optional.of(student)
        every { courseRepository.findById(courseId) } returns Optional.of(course)

        every {
            lessonRepository.existsByTeacherAndStartAtAndStatus(
                teacher,
                startAt,
                LessonStatus.BOOKED
            )
        } returns true

        // when & then
        val command = CreateLessonCommand(
            studentId = studentId,
            courseId = courseId,
            teacherId = teacherId,
            startAt = startAt
        )

        assertThrows<TeacherAlreadyBookedException> {
            lessonService.createLesson(command)
        }

        verify(exactly = 0) { lessonRepository.save(any()) }
    }

    @Test
    @DisplayName("학생이 같은 시간에 이미 예약되어 있으면 예외가 발생한다")
    fun createLesson_fail_whenStudentAlreadyBooked() {
        // given
        val studentId = 1L
        val teacherId = 2L
        val courseId = 3L
        val startAt = LocalDateTime.of(2026, 3, 7, 10, 0)

        val student = Student(id = studentId, name = "student1", os = Os.IOS)
        val teacher = Teacher(id = teacherId, name = "teacher1")
        val course = Course(id = courseId, name = "English")

        every { teacherRepository.findById(teacherId) } returns Optional.of(teacher)
        every { studentRepository.findById(studentId) } returns Optional.of(student)
        every { courseRepository.findById(courseId) } returns Optional.of(course)

        every {
            lessonRepository.existsByTeacherAndStartAtAndStatus(
                teacher,
                startAt,
                LessonStatus.BOOKED
            )
        } returns false

        every {
            lessonRepository.existsByStudentAndStartAtAndStatus(
                student,
                startAt,
                LessonStatus.BOOKED
            )
        } returns true

        // when & then
        val command = CreateLessonCommand(
            studentId = studentId,
            courseId = courseId,
            teacherId = teacherId,
            startAt = startAt
        )

        assertThrows<StudentAlreadyBookedException> {
            lessonService.createLesson(command)
        }

        verify(exactly = 0) { lessonRepository.save(any()) }
    }

    @Test
    @DisplayName("수업 시작 시간이 00분 또는 30분이 아니면 예외가 발생한다")
    fun createLesson_fail_whenInvalidStartAt() {
        // given
        val studentId = 1L
        val teacherId = 2L
        val courseId = 3L
        val startAt = LocalDateTime.of(2025, 3, 6, 10, 15)

        // when & then
        val command = CreateLessonCommand(
            studentId = studentId,
            courseId = courseId,
            teacherId = teacherId,
            startAt = startAt
        )

        assertThrows<IllegalArgumentException> {
            lessonService.createLesson(command)
        }

        verify(exactly = 0) { teacherRepository.findById(any()) }
        verify(exactly = 0) { studentRepository.findById(any()) }
        verify(exactly = 0) { courseRepository.findById(any()) }
        verify(exactly = 0) { lessonRepository.save(any()) }
    }
}