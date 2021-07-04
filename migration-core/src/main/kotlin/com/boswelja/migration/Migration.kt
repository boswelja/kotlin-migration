package com.boswelja.migration

/**
 * An interface that all Migrations must implement, to be supported by [Migrator].
 */
interface Migration {

    /**
     * The version the system will be at after applying the migration, or null if the
     * version shouldn't change.
     */
    val toVersion: Int?

    /**
     * Performs migration logic.
     * @return See [Result].
     */
    suspend fun migrate(): Result

    /**
     * Whether this migration should be run.
     * @param fromVersion The version we are trying to migrate from.
     * @return true if this migration should be run, false otherwise.
     */
    suspend fun shouldMigrate(fromVersion: Int): Boolean
}

/**
 * Defines a migration from one version to the next.
 * @param fromVersion The old version to migrate from.
 * @param toVersion The version to migrate to.
 */
abstract class VersionMigration(
    val fromVersion: Int,
    final override val toVersion: Int
) : Migration {
    final override suspend fun shouldMigrate(fromVersion: Int): Boolean {
        // Run this migration if fromVersion is the same as the version provided by the user
        return fromVersion == this.fromVersion
    }
}

/**
 * Defines a migration that should be run if some condition is met.
 */
abstract class ConditionalMigration : Migration {
    override val toVersion: Int? = null
}
