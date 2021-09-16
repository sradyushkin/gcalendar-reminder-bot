package org.sradyushkin.gcrb.properties

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.RuntimeException
import java.util.*

open class PropertyReceiver {

    private val log: Logger = LoggerFactory.getLogger(PropertyReceiver::class.java)
    private val prop = Properties()

    open fun getPropertyValue(key: String): String {
        try {
            prop.load(PropertyReceiver::class.java.getResourceAsStream("/$APP_PROP"))
            return prop.getProperty(key)
        } catch (e : Exception) {
            log.error("Property receive error", e)
            throw RuntimeException()
        }
    }

    companion object {
        private const val APP_PROP: String = "application.properties"
    }
}