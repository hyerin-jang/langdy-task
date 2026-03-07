package com.langdy.langdy_task.repository

import com.langdy.langdy_task.entity.Teacher
import org.springframework.data.jpa.repository.JpaRepository

interface TeacherRepository : JpaRepository<Teacher, Long>