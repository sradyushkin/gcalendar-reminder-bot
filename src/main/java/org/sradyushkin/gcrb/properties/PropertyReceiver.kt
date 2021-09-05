package org.sradyushkin.gcrb.properties

import java.lang.RuntimeException
import java.util.*

open class PropertyReceiver {

    private val prop = Properties()

    open fun getPropertyValue(key: String): String {
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