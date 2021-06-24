package com.boswelja.migration

/**
 * A class for executing [Migration]s.
 * @param migrations The available [Migration]s to use.
 */
abstract class Migrator(
    private vararg val migrations: Migration
) {

    /**
     * Get the old version to migrate from.
     */
    abstract suspend fun getOldVersion(): Int

    /**
     * Gets the new version to migrate to.
     */
    abstract suspend fun getNewVersion(): Int

    suspend fun migrate() {
        // Get versions
        val oldVersion = getOldVersion()
        val newVersion = getNewVersion()

        // Build migration map
        val migrationMap = buildMigrationMap(oldVersion, newVersion)

        migrationMap.forEach { migration ->
            migration.migrate()
        }
    }

    /**
     * A recursive function to build a list of [Migration]s to be run in a sequential order.
     */
    internal fun buildMigrationMap(
        oldVersion: Int,
        newVersion: Int
    ): List<Migration> {
        val migrations = mutableListOf<Migration>()
        val migrationsFromOldVersion = migrations.filter { it.fromVersion == oldVersion }
        if (migrationsFromOldVersion.count() != 1) {
            throw IllegalArgumentException(
                "Error getting a migration from version $oldVersion"
            )
        } else {
            val migration = migrationsFromOldVersion.first()
            migrations.add(migration)
            if (migration.toVersion < newVersion) {
                migrations.addAll(buildMigrationMap(migration.toVersion, newVersion))
            }
        }
        return migrations
    }
}
