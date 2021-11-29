package org.sradyushkin.gcrb.schedule

import java.time.LocalDateTime

data class Event(val chatId: String, val data: EventData)

data class EventData(
    val header: String?,
    val calendarName: String?,
    val start: LocalDateTime?,
    val end: LocalDateTime?,
    val location: String?,
    val description: String?
)