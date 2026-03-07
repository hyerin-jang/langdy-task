package com.langdy.langdy_task.repository

import com.langdy.langdy_task.entity.Course
import org.springframework.data.jpa.repository.JpaRepository

interface CourseRepository : JpaRepository<Course, Long>