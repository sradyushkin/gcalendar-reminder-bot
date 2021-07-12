package org.sradyushkin.properties

import java.util.*

class PropertyReceiver {

    private val appProp: String = "application.properties"

    private val prop = Properties()

    fun getPropertyValue(key: String): String {
        prop.load(PropertyReceiver::class.java.getResourceAsStream("/$appProp"))
        return prop.getProperty(key)
    }
}