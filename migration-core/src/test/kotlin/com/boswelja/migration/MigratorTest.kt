package com.boswelja.migration

import kotlinx.coroutines.runBlocking
import org.junit.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class MigratorTest {

    @Test
    fun `runConstantMigrations does nothing with no migrations`(): Unit = runBlocking {
        val migrationCount = 10
        // Create a migrator for running tests
        val migrator = ConcreteMigrator(
            1,
            1,
            false,
            emptyList()
        )

        // Check with empty migration list
        expectThat(
            migrator.runConstantMigrations(1, emptyList())
        ).isEqualTo(Result.NOT_NEEDED)

        // Check with migrations whose shouldMigrate returns false
        val migrations = createConstantMigrations(
            count = migrationCount,
            shouldMigrate = { false },
            migrateResult = { false }
        )
        expectThat(
            migrator.runConstantMigrations(1, migrations)
        ).isEqualTo(Result.NOT_NEEDED)
    }

    @Test
    fun `runConstantMigrations aborts when abortOnError is true`(): Unit = runBlocking {
        val migrationCount = 10

        // Create a migrator for running tests
        val migrator = ConcreteMigrator(
            1,
            1,
            true,
            emptyList()
        )

        // Run migrations
        var migrationExecutionCount = 0
        val migrations = createConstantMigrations(
            count = migrationCount,
            shouldMigrate = { true },
            migrateResult = {
                migrationExecutionCount++
                false
            }
        )
        migrator.runConstantMigrations(1, migrations)

        // Verify only one migration was called
        expectThat(migrationExecutionCount).isEqualTo(1)
    }

    @Test
    fun `runConstantMigrations returns FAILED on error`(): Unit = runBlocking {
        val migrationCount = 10

        // Create migrations
        val migrations = createConstantMigrations(
            count = migrationCount,
            shouldMigrate = { true },
            migrateResult = { false }
        )

        // Check with abortOnError = true
        ConcreteMigrator(
            1,
            1,
            true,
            emptyList()
        ).also { migrator ->
            // Check result
            expectThat(migrator.runConstantMigrations(1, migrations)).isEqualTo(Result.FAILED)
        }

        // Check with abortOnError = false
        ConcreteMigrator(
            1,
            1,
            false,
            emptyList()
        ).also { migrator ->
            // Check result
            expectThat(migrator.runConstantMigrations(1, migrations)).isEqualTo(Result.FAILED)
        }
    }

    @Test
    fun `runConstantMigrations throws exception if non-contant migrations are given`(): Unit = runBlocking {
        // Create migrations
        val migrations = createVersionedMigrations(
            1,
            2,
            migrateResult = { true }
        )

        // Check with abortOnError = true
        ConcreteMigrator(
            1,
            1,
            true,
            emptyList()
        ).also { migrator ->
            // Check result
            expectThrows<IllegalArgumentException> {
                migrator.runConstantMigrations(1, migrations)
            }
        }
    }

    @Test
    fun `runConstantMigrations returns SUCCESS on successful migrations`(): Unit = runBlocking {
        val migrationCount = 10

        // Create migrations
        val migrations = createConstantMigrations(
            count = migrationCount,
            shouldMigrate = { true },
            migrateResult = { true }
        )

        // Create a migrator to use
        ConcreteMigrator(
            1,
            1,
            true,
            emptyList()
        ).also { migrator ->
            // Check result
            expectThat(migrator.runConstantMigrations(1, migrations)).isEqualTo(Result.SUCCESS)
        }
    }

    @Test
    fun `runVersionedMigrations does nothing with no migrations`(): Unit = runBlocking {
        val fromVersion = 1
        val toVersion = 10
        // Create a migrator for running tests
        val migrator = ConcreteMigrator(
            toVersion,
            toVersion,
            false,
            emptyList()
        )

        // Check with empty migration list
        expectThat(
            migrator.runVersionedMigrations(toVersion, emptyList())
        ).isEqualTo(Result.NOT_NEEDED)

        // Check with migrations that won't change the version
        val migrations = createVersionedMigrations(
            fromVersion,
            toVersion,
            migrateResult = { true }
        )
        expectThat(
            migrator.runVersionedMigrations(toVersion, migrations)
        ).isEqualTo(Result.NOT_NEEDED)
    }

    @Test
    fun `runVersionedMigrations aborts when abortOnError is true`(): Unit = runBlocking {
        val fromVersion = 1
        val toVersion = 10

        // Create a migrator for running tests
        val migrator = ConcreteMigrator(
            fromVersion,
            toVersion,
            true,
            emptyList()
        )

        // Run migrations
        var migrationExecutionCount = 0
        val migrations = createVersionedMigrations(
            fromVersion,
            toVersion,
            migrateResult = {
                migrationExecutionCount++
                false
            }
        )
        migrator.runVersionedMigrations(1, migrations)

        // Verify only one migration was called
        expectThat(migrationExecutionCount).isEqualTo(1)
    }

    @Test
    fun `runVersionedMigrations returns FAILED on error`(): Unit = runBlocking {
        val fromVersion = 1
        val toVersion = 10

        // Create migrations
        val migrations = createVersionedMigrations(
            fromVersion,
            toVersion,
            migrateResult = { false }
        )

        // Check with abortOnError = true
        ConcreteMigrator(
            fromVersion,
            toVersion,
            true,
            emptyList()
        ).also { migrator ->
            // Check result
            expectThat(migrator.runVersionedMigrations(1, migrations)).isEqualTo(Result.FAILED)
        }

        // Check with abortOnError = false
        ConcreteMigrator(
            fromVersion,
            toVersion,
            false,
            emptyList()
        ).also { migrator ->
            // Check result
            expectThat(migrator.runVersionedMigrations(1, migrations)).isEqualTo(Result.FAILED)
        }
    }

    @Test
    fun `runVersionedMigrations throws exception if contant migrations are given`(): Unit = runBlocking {
        val migrationCount = 10
        // Create migrations
        val migrations = createConstantMigrations(
            migrationCount,
            shouldMigrate = { true },
            migrateResult = { true }
        )

        // Check with abortOnError = true
        ConcreteMigrator(
            1,
            1,
            true,
            emptyList()
        ).also { migrator ->
            // Check result
            expectThrows<IllegalArgumentException> {
                migrator.runVersionedMigrations(1, migrations)
            }
        }
    }

    @Test
    fun `runVersionedMigrations returns SUCCESS on successful migrations`(): Unit = runBlocking {
        val fromVersion = 1
        val toVersion = 10

        // Create migrations
        val migrations = createVersionedMigrations(
            fromVersion,
            toVersion,
            migrateResult = { true }
        )

        // Create a migrator to use
        ConcreteMigrator(
            fromVersion,
            toVersion,
            true,
            emptyList()
        ).also { migrator ->
            // Check result
            expectThat(
                migrator.runVersionedMigrations(fromVersion, migrations)
            ).isEqualTo(Result.SUCCESS)
        }
    }

    @Test
    fun `onMigratedTo is called after successful migration`() {
        val fromVersion = 1
        val toVersion = 3

        val migrations = createVersionedMigrations(
            fromVersion,
            toVersion
        ) { true }

        val migrator = ConcreteMigrator(
            oldVersion = fromVersion,
            currentVersion = toVersion,
            migrations = migrations
        )

        runBlocking { migrator.migrate() }

        expectThat(migrator.migratedTo).isEqualTo(3)
    }

    @Test
    fun `onMigratedTo is called after failed migration`() {
        val fromVersion = 1
        val toVersion = 3

        val migrations = createVersionedMigrations(
            fromVersion,
            toVersion
        ) { false }

        val migrator = ConcreteMigrator(
            oldVersion = fromVersion,
            currentVersion = toVersion,
            migrations = migrations
        )

        runBlocking { migrator.migrate() }

        expectThat(migrator.migratedTo).isEqualTo(fromVersion)
    }

    @Test
    fun `onMigratedTo is not called if no migrations were run`() {
        val fromVersion = 1
        val toVersion = 3

        val migrations = createVersionedMigrations(
            fromVersion,
            toVersion
        ) { false }

        val migrator = ConcreteMigrator(
            oldVersion = toVersion,
            currentVersion = toVersion,
            migrations = migrations
        )

        runBlocking { migrator.migrate() }

        expectThat(migrator.migratedTo).isNull()
    }

    @Test
    fun `migrate throws IllegalStateException if getOldVersion returns higher than currentVersion`() {
        val migrator = ConcreteMigrator(
            oldVersion = 2,
            currentVersion = 1,
            migrations = emptyList()
        )
        expectThrows<IllegalStateException> {
            migrator.migrate()
        }
    }
}
