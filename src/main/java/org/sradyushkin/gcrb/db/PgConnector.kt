package org.sradyushkin.gcrb.db

import java.net.URI
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class PgConnector {

    fun getConnection(): Connection {
        val credentials = Properties()
        val dbURI = URI(System.getenv("DATABASE_URL"))
        credentials["user"] = dbURI.userInfo.split(":")[0]
        credentials["password"] = dbURI.userInfo.split(":")[1]
        return DriverManager.getConnection(dbURI.host, credentials)
    }
}