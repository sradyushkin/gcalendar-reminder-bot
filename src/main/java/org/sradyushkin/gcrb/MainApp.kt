package org.sradyushkin.gcrb

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.sradyushkin.gcrb.dao.AuthUserDao
import org.sradyushkin.gcrb.dao.CalendarDao
import org.sradyushkin.gcrb.db.MigrationRunner
import org.sradyushkin.gcrb.db.PgConnector
import org.sradyushkin.gcrb.properties.PropertyReceiver
import org.sradyushkin.gcrb.schedule.JobConfig
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

object MainApp {
    private val log: Logger = LoggerFactory.getLogger(MainApp::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            MigrationRunner.runMigration()
            val connector = PgConnector()
            val calendarDao = CalendarDao(connector)
            val authUserDao = AuthUserDao(connector)
            val propertyReceiver = PropertyReceiver()
            val calendarBot = CalendarBot(propertyReceiver, calendarDao, authUserDao)
            val jobConfig = JobConfig(calendarBot, calendarDao, propertyReceiver)
            jobConfig.createJob()
            val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
            botsApi.registerBot(calendarBot)
        } catch (e: Exception) {
            log.error("An error occurred during app starting", e)
        }
    }
}