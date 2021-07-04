package com.boswelja.migration

/**
 * A class for executing [Migration]s.
 * @param currentVersion The current version to use when building migration maps.
 * @param abortOnError Whether [migrate] should abort when a migration fails.
 * @param migrations The available [Migration]s to use.
 */
abstract class Migrator(
    val currentVersion: Int,
    private val abortOnError: Boolean = true,
    private val migrations: List<Migration>
) {

    /**
     * Get the old version to migrate from.
     */
    abstract suspend fun getOldVersion(): Int

    /**
     * Called when we've successfully migrated to a new version, and there are no pending migrations
     * left.
     * @param version The version we migrated to.
     */
    abstract suspend fun onMigratedTo(version: Int)

    suspend fun migrate(): Result {
        // Get versions
        val oldVersion = getOldVersion()

        // Check old version isn't greater than current version
        check(oldVersion <= currentVersion)

        val (versionMigrations, constantMigrations) = migrations.separate { it.toVersion != null }

        // Handle constant migrations
        runConstantMigrations(oldVersion, constantMigrations)

        // Handle versioned migrations
        val result = runVersionedMigrations(oldVersion, versionMigrations)

        return result
    }

    /**
     * Runs all given migrations that have no [Migration.toVersion], and
     * [Migration.shouldMigrate] returns true. This will throw [IllegalArgumentException]
     * if any of the migrations provided have a non-null [Migration.toVersion].
     * @param fromVersion The version to migrate from.
     * @param migrations The [List] of [Migration]s that don't have [Migration.toVersion]
     * set.
     */
    suspend fun runConstantMigrations(
        fromVersion: Int,
        migrations: List<Migration>
    ) {
        // Throw an exception if any of the given migrations have a non-null toVersion
        require(migrations.all { it.toVersion == null })

        // Run all migrations where shouldMigrate returns true
        migrations.filter { it.shouldMigrate(fromVersion) }.forEach { migration ->
            migration.migrate()
        }
    }

    suspend fun runVersionedMigrations(
        fromVersion: Int,
        migrations: List<Migration>
    ): Result {
        // Throw an exception if any of the given migrations have no toVersion set
        require(migrations.all { it.toVersion != null })

        var version = fromVersion
        var result = Result.NOT_NEEDED
        while (version < currentVersion) {
            // Get the next migration
            val migration = migrations
                .filter { it.shouldMigrate(version) }
                .maxByOrNull { it.toVersion!! }
            checkNotNull(migration) { "Couldn't find a migration from version $version" }

            val migrateResult = migration.migrate()
            if (migrateResult) {
                // Don't update result if it's in a failed state
                if (result != Result.FAILED) {
                    result = Result.SUCCESS
                }
            } else {
                result = Result.FAILED
                // If abort on error is true, return result now
                if (abortOnError) {
                    break
                }
            }
            // Update version and continue
            version = migration.toVersion!!
        }

        onMigratedTo(version)

        return result
    }
}
