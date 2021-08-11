package org.sradyushkin.gcrb

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Events
import java.io.FileInputStream
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList


class CalendarEventReceiver(
    private val accessKey: String,
    private val calendarId: String
) {

    fun getEvents(): List<String> {
        val events = receiveEvents()
        if (events != null) {
            val items = ArrayList<String>(events.items?.size ?: 0)
            events.items?.forEach { items.add(it.summary) }
            return items.filter { true }
        }
        return emptyList()
    }

    private fun receiveEvents(): Events? {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val jacksonFactory = GsonFactory.getDefaultInstance()
        val credential = GoogleCredential.fromStream(FileInputStream(accessKey))
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
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    private fun toDate(date: LocalDateTime): Date {
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant())
    }
}