package org.sradyushkin.gcrb.dao

import org.sradyushkin.gcrb.db.PgConnector
import java.sql.SQLException

class CalendarDao {
    private val connector = PgConnector()

    fun saveCalendar(name: String, userId: Int) {
        connector.getConnection().use {
            try {
                val ps = it.prepareStatement(SAVE_CALENDAR_QUERY)
                ps.setString(1, name)
                ps.setInt(1, userId)
                ps.execute()
            } catch (e: SQLException) {
                e.printStackTrace()
                throw e
            }
        }
    }

    companion object {
        private const val SAVE_CALENDAR_QUERY = "INSERT INTO calendar(name, auth_user_id) VALUES (?, ?);"
    }
}