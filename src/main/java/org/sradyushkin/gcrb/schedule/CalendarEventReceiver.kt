package org.sradyushkin.gcrb.schedule

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Events
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

class CalendarEventReceiver(
    private val accessKey: String,
    private val calendarId: String
) {

    private val log: Logger = LoggerFactory.getLogger(CalendarEventReceiver::class.java)

    fun getEvents(): List<EventData> {
        log.info("Attempt to receive events from google api. CalendarId - $calendarId")
        val events = receiveEvents()
        if (events != null) {
            val eventSize = events.items?.size ?: 0
            log.info("Attempt was successfully. Received - $eventSize events")
            val items = ArrayList<EventData>(eventSize)
            events.items?.forEach {
                items.add(
                    EventData(
                        it.summary,
                        events.summary,
                        if (it.start != null) toLocalDateTime(it.start.dateTime) else null,
                        if (it.end != null) toLocalDateTime(it.end.dateTime) else null,
                        it.location,
                        it.description
                    )
                )
            }
            return items.filter { i -> i.header != null && i.calendarName != null }
        }
        return emptyList()
    }

    private fun receiveEvents(): Events? {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val jacksonFactory = GsonFactory.getDefaultInstance()
        val credential = GoogleCredential.fromStream(ByteArrayInputStream(accessKey.toByteArray()))
            .createScoped(listOf(CalendarScopes.CALENDAR_READONLY))

        val startOfDate = toDate(LocalDateTime.now().with(LocalTime.MIN))
        val endOfDate = toDate(LocalDateTime.now().with(LocalTime.MAX))

        val calendar = Calendar(httpTransport, jacksonFactory, credential)
        try {
            return calendar.events()
                .list(calendarId)
                .setTimeMin(DateTime(startOfDate))
                .setTimeMax(DateTime(endOfDate))
                .execute()
        } catch (e: Exception) {
            log.error("Receive event data from google api error. CalendarId - $calendarId", e)
        }
        return null
    }

    private fun toDate(date: LocalDateTime): Date {
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant())
    }

    private fun toLocalDateTime(date: DateTime?): LocalDateTime? {
        if (date == null) {
            return null
        }
        return Instant.ofEpochMilli(date.value).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }
}