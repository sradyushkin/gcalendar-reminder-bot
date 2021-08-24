package org.sradyushkin.gcrb.dao

import org.sradyushkin.gcrb.db.PgConnector
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
                throw e
            }
        }
    }

    fun getIdByChatId(chatId: Int): String {
        connector.getConnection().use {
            try {
                val ps = it.prepareStatement(GET_ID_BY_CHAT_ID_QUERY)
                ps.setInt(1, chatId)
                val rs = ps.executeQuery()
                return rs.getString("id")
            } catch (e: SQLException) {
                e.printStackTrace()
                throw e
            }
        }
    }

    companion object {
        private const val SAVE_USER_DATA_QUERY = "INSERT INTO auth_user(access_key, chat_id) VALUES (?, ?);"
        private const val GET_ID_BY_CHAT_ID_QUERY = "SELECT id FROM auth_user WHERE chat_id = ?;"
    }
}