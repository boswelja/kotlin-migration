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
        // Do nothing for now
    }
}
