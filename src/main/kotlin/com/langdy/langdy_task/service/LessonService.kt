package com.langdy.langdy_task.service

import com.langdy.langdy_task.controller.response.CreateLessonResponse
import com.langdy.langdy_task.entity.Lesson
import com.langdy.langdy_task.entity.enums.LessonStatus
import com.langdy.langdy_task.global.exception.StudentAlreadyBookedException
import com.langdy.langdy_task.global.exception.TeacherAlreadyBookedException
import com.langdy.langdy_task.global.policy.TimePolicy
import com.langdy.langdy_task.notification.NotificationPublisher
import com.langdy.langdy_task.repository.CourseRepository
import com.langdy.langdy_task.repository.LessonRepository
import com.langdy.langdy_task.repository.StudentRepository
import com.langdy.langdy_task.repository.TeacherRepository
import com.langdy.langdy_task.service.command.CreateLessonCommand
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class LessonService(
    private val lessonRepository: LessonRepository,
    private val teacherRepository: TeacherRepository,
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository,
    private val notificationPublisher: NotificationPublisher,
) {
    @Transactional
    fun createLesson(command: CreateLessonCommand): CreateLessonResponse {
        // 수업 시작 시간 정책 검증
        TimePolicy.validateStartAt(command.startAt)

        val endAt = TimePolicy.calculateEndAt(command.startAt)
        val teacher = teacherRepository.findByIdOrNull(command.teacherId)
            ?: throw IllegalArgumentException("Teacher not found")
        val student = studentRepository.findByIdOrNull(command.studentId)
            ?: throw IllegalArgumentException("Student not found")
        val course = courseRepository.findByIdOrNull(command.courseId)
            ?: throw IllegalArgumentException("Course not found")

        // teacher 중복 예약 확인
        val teacherExists = lessonRepository.existsByTeacherAndStartAtAndStatus(
            teacher = teacher,
            startAt = command.startAt,
            status = LessonStatus.BOOKED
        )

        if (teacherExists) {
            throw TeacherAlreadyBookedException()
        }

        // student 중복 예약 확인
        val studentExists = lessonRepository.existsByStudentAndStartAtAndStatus(
            student = student,
            startAt = command.startAt,
            status = LessonStatus.BOOKED
        )

        if (studentExists) {
            throw StudentAlreadyBookedException()
        }

        val lesson = lessonRepository.save(
            Lesson(
                course = course,
                teacher = teacher,
                student = student,
                status = LessonStatus.BOOKED,
                startAt = command.startAt,
                endAt = endAt
            )
        )

        // 알림 발송
        notificationPublisher.publishNotification()

        return CreateLessonResponse(lesson.id)
    }
}