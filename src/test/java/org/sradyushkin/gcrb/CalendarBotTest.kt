package org.sradyushkin.gcrb

import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

class CalendarBotTest {

    private val bot = mock(CalendarBot::class.java)

    @Test
    fun sendRegisterMessageTest() {
        val update = Update()
        val msg = Message()
        val chat = Chat()
        chat.id = 12345
        msg.text = "/register some_key"
        msg.chat = chat
        update.message = msg
        `when`(bot.onUpdateReceived(update)).thenCallRealMethod()
        val sendMessageCaptor = ArgumentCaptor.forClass(SendMessage::class.java)
        bot.onUpdateReceived(update)

        verify(bot, times(1)).execute(sendMessageCaptor.capture())
        Assert.assertEquals(CalendarBot.KEY_SAVED_MESSAGE, sendMessageCaptor.value.text)
    }

    @Test
    fun sendCalendarMessageTest() {
        val update = Update()
        val msg = Message()
        val chat = Chat()
        chat.id = 12345
        msg.text = "/calendar some_calendar_name"
        msg.chat = chat
        update.message = msg
        `when`(bot.onUpdateReceived(update)).thenCallRealMethod()
        val sendMessageCaptor = ArgumentCaptor.forClass(SendMessage::class.java)
        bot.onUpdateReceived(update)

        verify(bot, times(1)).execute(sendMessageCaptor.capture())
        Assert.assertEquals(CalendarBot.CALENDAR_SAVED_MESSAGE, sendMessageCaptor.value.text)
    }

    @Test
    fun sendHelpMessageTest() {
        val update = Update()
        val msg = Message()
        val chat = Chat()
        chat.id = 12345
        msg.text = "/help"
        msg.chat = chat
        update.message = msg
        `when`(bot.onUpdateReceived(update)).thenCallRealMethod()
        val sendMessageCaptor = ArgumentCaptor.forClass(SendMessage::class.java)
        bot.onUpdateReceived(update)

        verify(bot, times(1)).execute(sendMessageCaptor.capture())
        Assert.assertEquals(CalendarBot.HELP_MESSAGE, sendMessageCaptor.value.text)
    }

    @Test
    fun sendUndefinedMessageTest() {
        val update = Update()
        val msg = Message()
        val chat = Chat()
        chat.id = 12345
        msg.text = "/undefined_command"
        msg.chat = chat
        update.message = msg
        `when`(bot.onUpdateReceived(update)).thenCallRealMethod()
        val sendMessageCaptor = ArgumentCaptor.forClass(SendMessage::class.java)
        bot.onUpdateReceived(update)

        verify(bot, times(1)).execute(sendMessageCaptor.capture())
        Assert.assertEquals(CalendarBot.UNDEFINED_MESSAGE, sendMessageCaptor.value.text)
    }
}