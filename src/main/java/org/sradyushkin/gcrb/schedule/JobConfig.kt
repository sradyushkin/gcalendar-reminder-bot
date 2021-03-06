package org.sradyushkin.gcrb.schedule

import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobDataMap
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory
import org.sradyushkin.gcrb.dao.CalendarDao
import org.sradyushkin.gcrb.properties.PropertyReceiver

class JobConfig(
    private val eventListener: EventListener,
    private val calendarDao: CalendarDao,
    private val propertyReceiver: PropertyReceiver
) {
    fun createJob() {
        val jobDataMap = JobDataMap()
        jobDataMap["eventListener"] = eventListener
        jobDataMap["calendarDao"] = calendarDao
        val jobDetail = JobBuilder.newJob(CalendarEventReceiverJob::class.java)
            .withIdentity("calendarEventReceiverJob")
            .setJobData(jobDataMap)
            .build()

        val trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity("triggerCalendarEventReceiverJob")
            .withSchedule(
                CronScheduleBuilder.cronSchedule(
                    propertyReceiver.getPropertyValue("receive.events.cron")
                )
            )
            .build()

        val scheduleFactory = StdSchedulerFactory()
        val scheduler = scheduleFactory.scheduler
        scheduler.start()
        scheduler.scheduleJob(jobDetail, trigger)
    }
}