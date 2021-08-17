package org.sradyushkin.gcrb

import org.sradyushkin.gcrb.db.MigrationRunner
import org.sradyushkin.gcrb.schedule.JobConfig
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

fun main() {
    try {
        MigrationRunner.runMigration()
        val calendarBot = CalendarBot()
        val jobConfig = JobConfig(calendarBot)
        jobConfig.createJob()
        val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
        botsApi.registerBot(calendarBot)
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}