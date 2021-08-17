package org.sradyushkin.gcrb.db

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import org.flywaydb.core.api.configuration.ClassicConfiguration
import org.sradyushkin.gcrb.properties.PropertyReceiver

class MigrationRunner {
    private val propertyReceiver = PropertyReceiver()

    fun runMigration() {
        val config = ClassicConfiguration()
        config.setDataSource(
            propertyReceiver.getPropertyValue("db.url"),
            propertyReceiver.getPropertyValue("db.user"),
            propertyReceiver.getPropertyValue("db.password")
        )
        config.setLocations(Location(DB_FOLDER))
        val flyway = Flyway(config)
        flyway.migrate()
    }

    companion object {
        private const val DB_FOLDER: String = "src/main/resources/db/migration"
    }
}