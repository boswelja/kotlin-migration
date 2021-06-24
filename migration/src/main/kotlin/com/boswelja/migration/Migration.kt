package com.boswelja.migration

/**
 * Defines a migration from one version to the next.
 * @param fromVersion The old version to migrate from.
 * @param toVersion The version to migrate to.
 */
abstract class Migration(
    val fromVersion: Int,
    val toVersion: Int
) {

    /**
     * Performs migration logic.
     * @return true if migrating was successful, false otherwise.
     */
    abstract suspend fun migrate(): Boolean
}
