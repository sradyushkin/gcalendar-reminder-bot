package org.sradyushkin.gcrb.dao

import org.sradyushkin.gcrb.db.PgConnector
import org.sradyushkin.gcrb.exception.CalendarBotException
import java.sql.SQLException

class AuthUserDao {
    private val connector = PgConnector()

    fun saveUserData(accessKey: String, chatId: String) {
        connector.getConnection().use {
            try {
                val ps = it.prepareStatement(SAVE_USER_DATA_QUERY)
                ps.setString(1, accessKey)
                ps.setString(2, chatId)
                ps.execute()
            } catch (e: SQLException) {
                e.printStackTrace()
                throw CalendarBotException(null)
            }
        }
    }

    fun getIdByChatId(chatId: String): Int {
        connector.getConnection().use {
            try {
                val ps = it.prepareStatement(GET_ID_BY_CHAT_ID_QUERY)
                ps.setString(1, chatId)
                val rs = ps.executeQuery()
                while (rs.next()) {
                    return rs.getString("id").toInt()
                }
                throw CalendarBotException(ROW_NOT_FOUND)
            } catch (e: SQLException) {
                e.printStackTrace()
                throw CalendarBotException(null)
            }
        }
    }

    companion object {
        private const val SAVE_USER_DATA_QUERY = "INSERT INTO auth_user(access_key, chat_id) VALUES (?, ?);"
        private const val GET_ID_BY_CHAT_ID_QUERY = "SELECT id FROM auth_user WHERE chat_id = ?;"
        private const val ROW_NOT_FOUND = "You need register your access key first"
    }
}