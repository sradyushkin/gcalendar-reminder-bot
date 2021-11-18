package org.sradyushkin.gcrb.schedule

data class EventData(val chatId: String, val item: ItemInfo)

data class ItemInfo(val text: String?, val calendarName: String?)