package com.langdy.langdy_task.repository

import com.langdy.langdy_task.entity.Student
import org.springframework.data.jpa.repository.JpaRepository

interface StudentRepository : JpaRepository<Student, Long>