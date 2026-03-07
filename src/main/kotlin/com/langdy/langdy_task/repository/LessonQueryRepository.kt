package com.langdy.langdy_task.repository

import com.langdy.langdy_task.entity.QLesson.lesson
import com.langdy.langdy_task.entity.QTeacher.teacher
import com.langdy.langdy_task.entity.enums.LessonStatus
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class LessonQueryRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findAvailableTeachers(courseId: Long, startAt: LocalDateTime): List<Long> {
        return queryFactory
            .select(teacher.id)
            .from(teacher)
            .leftJoin(lesson)
            .on(
                lesson.teacher.eq(teacher),
                lesson.course.id.eq(courseId),
                lesson.startAt.eq(startAt),
                lesson.status.eq(LessonStatus.BOOKED)
            )
            .where(lesson.id.isNull)
            .fetch()
    }
}