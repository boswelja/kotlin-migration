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
     * @return true if migration was successful, false otherwise.
     */
    suspend fun migrate(): Boolean

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
 * A concrete representation of [VersionMigration] that accepts lambdas in place of overriding
 * functions in a custom class.
 */
internal class SimpleVersionMigration(
    fromVersion: Int,
    toVersion: Int,
    private val onMigrate: suspend () -> Boolean
) : VersionMigration(fromVersion, toVersion) {
    override suspend fun migrate(): Boolean = onMigrate()
}

/**
 * Create a [VersionMigration] with the given parameters.
 * @param fromVersion The version this migration can migrate from.
 * @param toVersion The version this migration will migrate to.
 * @param onMigrate A lambda function to be executed when this migration is applied. See
 * [VersionMigration.migrate].
 */
fun versionMigration(
    fromVersion: Int,
    toVersion: Int,
    onMigrate: suspend () -> Boolean
) = SimpleVersionMigration(fromVersion, toVersion, onMigrate) as VersionMigration

/**
 * Defines a migration that should be run if some condition is met.
 */
abstract class ConditionalMigration : Migration {
    override val toVersion: Int? = null
}

/**
 * A concrete representation of [ConditionalMigration] that accepts lambdas in place of overriding
 * functions in a custom class.
 */
internal class SimpleConditionalMigration(
    private val onShouldMigrate: suspend (fromVersion: Int) -> Boolean,
    private val onMigrate: suspend () -> Boolean
) : ConditionalMigration() {
    override suspend fun shouldMigrate(fromVersion: Int): Boolean = onShouldMigrate(fromVersion)
    override suspend fun migrate(): Boolean = onMigrate()
}

/**
 * Create a [ConditionalMigration] with the given parameters.
 * @param onShouldMigrate A lambda function that will be called to check whether this migration
 * should be executed. See [ConditionalMigration.shouldMigrate]
 * @param onMigrate A lambda function to be executed when this migration is applied. See
 * [ConditionalMigration.migrate].
 */
fun conditionalMigration(
    onShouldMigrate: suspend (fromVersion: Int) -> Boolean,
    onMigrate: suspend () -> Boolean
) = SimpleConditionalMigration(onShouldMigrate, onMigrate) as ConditionalMigration
