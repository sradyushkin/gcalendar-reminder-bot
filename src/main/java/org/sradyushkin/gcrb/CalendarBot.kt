package org.sradyushkin.gcrb

import org.sradyushkin.gcrb.dao.AuthUserDao
import org.sradyushkin.gcrb.dao.CalendarDao
import org.sradyushkin.gcrb.exception.CalendarBotException
import org.sradyushkin.gcrb.properties.PropertyReceiver
import org.sradyushkin.gcrb.schedule.CalendarEventReceiver
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
                e.printStackTrace()
                e.message ?: UNRECOGNIZED_ERROR_MESSAGE
            }
            val message = SendMessage()
            message.chatId = update.message.chatId.toString()
            message.text = replyMsg

            try {
                execute(message)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        }
    }

    private fun executeSendingMessages(message: SendMessage, update: Update) {
        val eventReceiver = CalendarEventReceiver(
            "/home/sergey/Downloads/key.json",
            "smitty90me@gmail.com"
        )

        eventReceiver.getEvents().forEach {
            message.chatId = update.message.chatId.toString()
            message.text = it

            try {
                execute(message)
            } catch (e: TelegramApiException) {
                e.printStackTrace()
            }
        }
    }

    private fun handleMessage(message: String, chatId: String): String {
        val trimmedMsg = message.trim().substring(1)
        val spaceIndex = trimmedMsg.indexOf(" ", 0)
        if (spaceIndex > 0) {
            when (trimmedMsg.substring(0, spaceIndex)) {
                CommandType.REGISTER.toString().lowercase() -> {
                    val accessKey = trimmedMsg.substring(spaceIndex + 1)
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
        println("It's")
        println(event.chatId)
    }

    companion object {
        const val KEY_SAVED_MESSAGE: String = "Your access key has been successfully saved!"
        const val CALENDAR_SAVED_MESSAGE: String = "Your calendar has been successfully saved!"
        const val HELP_MESSAGE = "Allows commands: /register - pass your google service account key, " +
                "/calendar - pass calendar name to receive events"
        const val UNDEFINED_MESSAGE = "Your command isn't recognized"
        const val UNRECOGNIZED_ERROR_MESSAGE = "An error occurred"
    }
}

enum class CommandType {
    REGISTER, CALENDAR, HELP
}