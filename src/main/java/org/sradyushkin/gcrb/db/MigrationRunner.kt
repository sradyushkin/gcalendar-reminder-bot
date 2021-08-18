package org.sradyushkin.gcrb.db

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import org.flywaydb.core.api.configuration.ClassicConfiguration
import java.net.URI

class MigrationRunner {

    companion object {
        fun runMigration() {
            val config = ClassicConfiguration()
            val dbURI = URI(System.getenv("DATABASE_URL"))
            config.setLocations(Location("classpath:db/migration"))
            config.setDataSource(
                "jdbc:postgresql://${dbURI.host}:${dbURI.port}${dbURI.path}",
                dbURI.userInfo.split(":")[0],
                dbURI.userInfo.split(":")[1]
            )
            val flyway = Flyway(config)
            flyway.migrate()
        }
    }
}