package org.sradyushkin.gcrb

import org.apache.commons.lang3.reflect.FieldUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.sradyushkin.gcrb.dao.AuthUserDao
import org.sradyushkin.gcrb.dao.CalendarDao
import org.sradyushkin.gcrb.properties.PropertyReceiver
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

class CalendarBotTest {

    private val bot = mock(CalendarBot::class.java)

    private val calendarDao = mock(CalendarDao::class.java)

    private val authUserDao = mock(AuthUserDao::class.java)

    private val propertyReceiver = mock(PropertyReceiver::class.java)

    @Before
    fun init() {
        injectField("authUserDao", authUserDao)
        injectField("calendarDao", calendarDao)
        injectField("propertyReceiver", propertyReceiver)
    }

    @Test
    fun sendRegisterMessageTest() {
        val update = Update()
        val msg = Message()
        val chat = Chat()
        chat.id = 12345
        val text = StringBuilder()
        for (i in 1..1000) {
         text.append(i)
        }
        msg.text = "/register $text"
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

    @Test
    fun sendUnregisteredMessageText() {
        val update = Update()
        val msg = Message()
        val chat = Chat()
        chat.id = 12345
        msg.text = "/unregister"
        msg.chat = chat
        update.message = msg
        `when`(bot.onUpdateReceived(update)).thenCallRealMethod()
        val sendMessageCaptor = ArgumentCaptor.forClass(SendMessage::class.java)
        bot.onUpdateReceived(update)

        verify(bot, times(1)).execute(sendMessageCaptor.capture())
        Assert.assertEquals(CalendarBot.ALL_USER_INFO_REMOVED_MESSAGE, sendMessageCaptor.value.text)
    }

    @Test
    fun sendDeleteMessageText() {
        val update = Update()
        val msg = Message()
        val chat = Chat()
        chat.id = 12345
        msg.text = "/delete some_calendar_name"
        msg.chat = chat
        update.message = msg
        `when`(bot.onUpdateReceived(update)).thenCallRealMethod()
        `when`(calendarDao.existByNameAndAuthUserId(anyString(), anyInt())).thenReturn(true)
        val sendMessageCaptor = ArgumentCaptor.forClass(SendMessage::class.java)
        bot.onUpdateReceived(update)

        verify(bot, times(1)).execute(sendMessageCaptor.capture())
        Assert.assertEquals(CalendarBot.CALENDAR_DELETED_MESSAGE, sendMessageCaptor.value.text)
    }

    private fun injectField(fieldName: String, obj: Any) {
        val f = FieldUtils.getField(CalendarBot::class.java, fieldName, true)
        FieldUtils.removeFinalModifier(f)
        f.set(bot, obj)
    }
}