package org.sradyushkin.gcrb.schedule

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.sradyushkin.gcrb.dao.CalendarDao

class CalendarEventReceiverJob : Job {
    override fun execute(context: JobExecutionContext?) {
        val jobDataMap = context?.jobDetail?.jobDataMap
        if (jobDataMap != null) {
            if (jobDataMap.containsKey("eventListener")) {
                val eventListener = jobDataMap["eventListener"] as EventListener
                val calendarDao = jobDataMap["calendarDao"] as CalendarDao
                val calendars = calendarDao.getAllCalendars()

                calendars.groupBy { it.chatId }
                    .forEach { map ->
                        map.value.forEach { calendar ->
                            val eventReceiver = CalendarEventReceiver(calendar.accessKey, calendar.name)
                            eventReceiver.getEvents().forEach {
                                eventListener.processUpdate(EventData(map.key, it, calendar.name))
                            }
                        }
                    }
            }
        }
    }
}