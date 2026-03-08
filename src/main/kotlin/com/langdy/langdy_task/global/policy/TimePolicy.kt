package com.langdy.langdy_task.global.policy

import java.time.LocalDateTime

object TimePolicy {
    private const val LESSON_MINUTES = 20L

    fun validateStartAt(startAt: LocalDateTime) {
        val minute = startAt.minute
        require(minute == 0 || minute == 30) { "수업 시작 시간은 00분 또는 30분이어야 합니다." }
        require(startAt.second == 0 && startAt.nano == 0) { "수업 시작 시간은 00분 또는 30분이어야 합니다." }
    }

    fun calculateEndAt(startAt: LocalDateTime): LocalDateTime {
        return startAt.plusMinutes(20)
    }
}