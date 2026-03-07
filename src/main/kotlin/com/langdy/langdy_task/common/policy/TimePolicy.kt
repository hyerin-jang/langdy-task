package com.langdy.langdy_task.common.policy

import java.time.LocalDateTime

object TimePolicy {
    private const val LESSON_MINUTES = 20L

    fun validateStartAt(startAt: LocalDateTime) {
        val minute = startAt.minute
        require(minute == 0 || minute == 30) { "startAt must be at minute 00 or 30" }
        require(startAt.second == 0 && startAt.nano == 0) { "startAt must be aligned to minute" }
    }
}