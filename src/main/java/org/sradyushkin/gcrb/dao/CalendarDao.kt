package org.sradyushkin.gcrb.dao

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.sradyushkin.gcrb.dao.dto.CalendarData
import org.sradyushkin.gcrb.db.PgConnector
import org.sradyushkin.gcrb.exception.CalendarBotException
import java.sql.SQLException

open class CalendarDao(private val connector: PgConnector) {

    private val log: Logger = LoggerFactory.getLogger(CalendarDao::class.java)

    open fun save(name: String, userId: Int) {
        connector.getConnection().use {
            try {
                val ps = it.prepareStatement(SAVE_CALENDAR_QUERY)
                ps.setString(1, name)
                ps.setInt(2, userId)
                ps.execute()
            } catch (e: SQLException) {
                log.error("Save user's calendar error", e)
                throw CalendarBotException(null)
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
                log.error("Receive all calendars info error", e)
                throw CalendarBotException(null)
            }
        }
    }

    fun existByNameAndAuthUserId(name: String, authUserId: Int): Boolean {
        connector.getConnection().use {
            try {
                val ps = it.prepareStatement(GET_BY_NAME_AND_USER_ID_QUERY)
                ps.setString(1, name)
                ps.setInt(2, authUserId)
                val rs = ps.executeQuery()
                return rs.next()
            } catch (e: SQLException) {
                log.error("Check calendar exist error", e)
                throw CalendarBotException(null)
            }
        }
    }

    fun deleteByNameAndAuthUserId(name: String, authUserId: Int) {
        connector.getConnection().use {
            try {
                val ps = it.prepareStatement(DELETE_BY_NAME_AND_USER_ID_QUERY)
                ps.setString(1, name)
                ps.setInt(2, authUserId)
                ps.execute()
            } catch (e: SQLException) {
                log.error("Calendar delete error", e)
                throw CalendarBotException(null)
            }
        }
    }

    companion object {
        private const val SAVE_CALENDAR_QUERY = "INSERT INTO calendar(name, auth_user_id) VALUES (?, ?);"
        private const val GET_ALL_CALENDARS_QUERY = "SELECT * FROM calendar INNER JOIN auth_user " +
                "ON auth_user_id = auth_user.id";
        private const val GET_BY_NAME_AND_USER_ID_QUERY = "SELECT id FROM calendar WHERE name = ? AND auth_user_id = ?;"
        private const val DELETE_BY_NAME_AND_USER_ID_QUERY = "DELETE FROM calendar WHERE name = ? AND auth_user_id = ?;"
    }
}