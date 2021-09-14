package org.sradyushkin.gcrb.dao

import org.sradyushkin.gcrb.dao.dto.CalendarData
import org.sradyushkin.gcrb.db.PgConnector
import java.sql.SQLException

open class CalendarDao(private val connector: PgConnector) {

    open fun saveCalendar(name: String, userId: Int) {
        connector.getConnection().use {
            try {
                val ps = it.prepareStatement(SAVE_CALENDAR_QUERY)
                ps.setString(1, name)
                ps.setInt(2, userId)
                ps.execute()
            } catch (e: SQLException) {
                e.printStackTrace()
                throw e
            }
        }
    }

    fun getAllCalendars(): List<CalendarData> {
        connector.getConnection().use {
            try {
                val ps = it.prepareStatement(GET_ALL_CALENDARS_QUERY)
                val rs = ps.executeQuery()
                val dataList = ArrayList<CalendarData>()
                while (rs.next()) {
                    val data = CalendarData(
                        rs.getString("chat_id"),
                        rs.getString("name"),
                        rs.getString("access_key")
                    )
                    dataList.add(data)
                }
                return dataList
            } catch (e: SQLException) {
                e.printStackTrace()
                throw e
            }
        }
    }

    companion object {
        private const val SAVE_CALENDAR_QUERY = "INSERT INTO calendar(name, auth_user_id) VALUES (?, ?);"
        private const val GET_ALL_CALENDARS_QUERY = "SELECT * FROM calendar INNER JOIN auth_user " +
                "ON auth_user_id = auth_user.id";
    }
}