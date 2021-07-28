package org.sradyushkin

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.sradyushkin.properties.PropertyReceiver

class CalendarBot : TelegramLongPollingBot() {
    private val propertyReceiver = PropertyReceiver()

    override fun getBotToken(): String {
        return propertyReceiver.getPropertyValue("bot.access.token")
    }

    override fun getBotUsername(): String {
        return propertyReceiver.getPropertyValue("bot.name")
    }

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage() && update.message.hasText()) {
            val message = SendMessage()
            /*message.chatId = update.message.chatId.toString()
            message.text = "Каг дела?"

            try {
                execute(message)
            } catch (e: TelegramApiException) {
                e.printStackTrace();
            }*/
            executeSendingMessages(message, update)
        }
    }


    private fun executeSendingMessages(message: SendMessage, update: Update) {
        val eventReceiver = CalendarEventReceiver("/home/sergey/Downloads/key.json",
            "smitty90me@gmail.com")

        eventReceiver.getEvents().forEach {
            message.chatId = update.message.chatId.toString()
            message.text = it

            try {
                execute(message)
            } catch (e: TelegramApiException) {
                e.printStackTrace();
            }
        }
    }
}