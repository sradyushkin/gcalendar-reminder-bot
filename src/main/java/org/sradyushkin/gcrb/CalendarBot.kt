package org.sradyushkin.gcrb

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.sradyushkin.gcrb.dao.AuthUserDao
import org.sradyushkin.gcrb.dao.CalendarDao
import org.sradyushkin.gcrb.exception.CalendarBotException
import org.sradyushkin.gcrb.properties.PropertyReceiver
import org.sradyushkin.gcrb.schedule.EventData
import org.sradyushkin.gcrb.schedule.EventListener
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

open class CalendarBot(
    private val propertyReceiver : PropertyReceiver,
    private val calendarDao : CalendarDao,
    private val authUserDao : AuthUserDao
) : TelegramLongPollingBot(), EventListener {

    private val log: Logger = LoggerFactory.getLogger(CalendarBot::class.java)

    override fun getBotToken(): String {
        return propertyReceiver.getPropertyValue("bot.access.token")
    }

    override fun getBotUsername(): String {
        return propertyReceiver.getPropertyValue("bot.name")
    }

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage() && update.message.hasText() && update.message.text.startsWith("/")) {
            val replyMsg: String = try {
                handleMessage(update.message.text, update.message.chatId.toString())
            } catch (e: CalendarBotException) {
                log.error("Input message handle error", e)
                e.message ?: UNRECOGNIZED_ERROR_MESSAGE
            }
            val message = SendMessage()
            message.chatId = update.message.chatId.toString()
            message.text = replyMsg

            try {
                execute(message)
            } catch (e: TelegramApiException) {
                log.error("Send message to receiver error", e)
            }
        }
    }

    private fun handleMessage(message: String, chatId: String): String {
        val trimmedMsg = message.trim().substring(1)
        val spaceIndex = trimmedMsg.indexOf(" ", 0)
        if (spaceIndex > 0) {
            when (trimmedMsg.substring(0, spaceIndex)) {
                CommandType.REGISTER.toString().lowercase() -> {
                    val accessKey = trimmedMsg.substring(spaceIndex + 1).replace("\n", "")
                    authUserDao.saveUserData(accessKey, chatId)
                    return KEY_SAVED_MESSAGE
                }
                CommandType.CALENDAR.toString().lowercase() -> {
                    val calendarName = trimmedMsg.substring(spaceIndex + 1)
                    val authUserId = authUserDao.getIdByChatId(chatId)
                    calendarDao.saveCalendar(calendarName, authUserId)
                    return CALENDAR_SAVED_MESSAGE
                }
            }
        }
        if (CommandType.HELP.toString().lowercase() == trimmedMsg) {
            return HELP_MESSAGE
        }
        return UNDEFINED_MESSAGE
    }

    override fun processUpdate(event: EventData) {
        val message = SendMessage()
        message.chatId = event.chatId
        message.text = NEW_EVENT_MESSAGE + "<b>${event.calendarName}</b>" + "\n<b>${event.text}</b>"
        message.enableHtml(true)

        try {
            log.info("Attempt to send event. ChatId - ${message.chatId}")
            execute(message)
        } catch (e: TelegramApiException) {
            log.error("Send message to receiver error", e)
        }
    }

    companion object {
        const val KEY_SAVED_MESSAGE: String = "Your access key has been successfully saved!"
        const val CALENDAR_SAVED_MESSAGE: String = "Your calendar has been successfully saved!"
        const val HELP_MESSAGE = "Allows commands: /register - pass your google service account key, " +
                "/calendar - pass calendar name to receive events"
        const val UNDEFINED_MESSAGE = "Your command isn't recognized"
        const val UNRECOGNIZED_ERROR_MESSAGE = "An error occurred"
        const val NEW_EVENT_MESSAGE = "You have new event from calendar: "
    }
}

enum class CommandType {
    REGISTER, CALENDAR, HELP
}