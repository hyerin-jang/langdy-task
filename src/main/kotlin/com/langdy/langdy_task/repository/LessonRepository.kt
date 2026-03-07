package com.langdy.langdy_task.repository

import com.langdy.langdy_task.entity.Lesson
import org.springframework.data.jpa.repository.JpaRepository

interface LessonRepository : JpaRepository<Lesson, Long>