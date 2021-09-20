package org.sradyushkin.gcrb.dao

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.sradyushkin.gcrb.db.PgConnector
import org.sradyushkin.gcrb.exception.CalendarBotException
import java.sql.SQLException

open class AuthUserDao(private val connector: PgConnector) {

    private val log: Logger = LoggerFactory.getLogger(AuthUserDao::class.java)

    open fun save(accessKey: String, chatId: String) {
        connector.getConnection().use {
            try {
                val ps = it.prepareStatement(SAVE_USER_DATA_QUERY)
                ps.setString(1, accessKey)
                ps.setString(2, chatId)
                ps.execute()
            } catch (e: SQLException) {
                log.error("Save user's access key error", e)
                throw CalendarBotException(null)
            }
        }
    }

    open fun getIdByChatId(chatId: String): Int {
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
                log.error("Receive id error", e)
                throw CalendarBotException(null)
            }
        }
    }

    fun deleteByChatId(chatId: String) {
        connector.getConnection().use {
            try {
                val ps = it.prepareStatement(DELETE_BY_CHAT_ID_QUERY)
                ps.setString(1, chatId)
                ps.execute()
            } catch (e: SQLException) {
                log.error("Delete auth user info error", e)
                throw CalendarBotException(null)
            }
        }
    }

    companion object {
        private const val SAVE_USER_DATA_QUERY = "INSERT INTO auth_user(access_key, chat_id) VALUES (?, ?);"
        private const val GET_ID_BY_CHAT_ID_QUERY = "SELECT id FROM auth_user WHERE chat_id = ?;"
        private const val DELETE_BY_CHAT_ID_QUERY = "DELETE FROM auth_user WHERE chat_id = ?;"
        private const val ROW_NOT_FOUND = "You need register your access key first"
    }
}