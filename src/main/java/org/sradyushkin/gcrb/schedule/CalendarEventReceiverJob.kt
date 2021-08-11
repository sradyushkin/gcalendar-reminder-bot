package org.sradyushkin.gcrb.schedule

import org.quartz.Job
import org.quartz.JobExecutionContext

class CalendarEventReceiverJob : Job {
    override fun execute(context: JobExecutionContext?) {
        val jobDataMap = context?.jobDetail?.jobDataMap
        if (jobDataMap != null) {
            if (jobDataMap.containsKey("eventListener")) {
                val eventListener = jobDataMap["eventListener"] as EventListener
                eventListener.processUpdate(EventData("dsg"))
            }
        }
    }
}