package org.sradyushkin.gcrb

import org.sradyushkin.gcrb.dao.AuthUserDao
import org.sradyushkin.gcrb.dao.CalendarDao
import org.sradyushkin.gcrb.db.MigrationRunner
import org.sradyushkin.gcrb.db.PgConnector
import org.sradyushkin.gcrb.properties.PropertyReceiver
import org.sradyushkin.gcrb.schedule.JobConfig
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

fun main() {
    try {
        MigrationRunner.runMigration()
        val connector = PgConnector()
        val calendarBot = CalendarBot(PropertyReceiver(), CalendarDao(connector), AuthUserDao(connector))
        val jobConfig = JobConfig(calendarBot)
        jobConfig.createJob()
        val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
        botsApi.registerBot(calendarBot)
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}