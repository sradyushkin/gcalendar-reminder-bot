package org.sradyushkin.properties

import java.lang.RuntimeException
import java.util.*

class PropertyReceiver {

    private val prop = Properties()

    fun getPropertyValue(key: String): String {
        try {
            prop.load(PropertyReceiver::class.java.getResourceAsStream("/$APP_PROP"))
            return prop.getProperty(key)
        } catch (ex : Exception) {
            ex.printStackTrace()
            throw RuntimeException()
        }
    }

    companion object {
        private const val APP_PROP: String = "application.properties"
    }
}