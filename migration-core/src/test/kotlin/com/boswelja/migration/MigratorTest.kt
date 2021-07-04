package com.boswelja.migration

import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo

class MigratorTest {

    @Test
    fun `migrate() succeeds with one migration`() {
        // Set up dummy migrations
        val migration = object : VersionMigration(1, 2) {
            override suspend fun migrate(): Result {
                return Result.SUCCESS
            }
        }
        val migrator = ConcreteMigrator(
            oldVersion = 1,
            currentVersion = 2,
            migrations = listOf(migration)
        )

        val result = runBlocking { migrator.migrate() }

        expectThat(result).isEqualTo(Result.SUCCESS)
    }

    @Test
    fun `migrate() fails when migration fails`() {
        // Set up dummy migrations
        val migration = object : VersionMigration(1, 2) {
            override suspend fun migrate(): Result {
                return Result.FAILED
            }
        }
        val migrator = ConcreteMigrator(
            oldVersion = 1,
            currentVersion = 2,
            migrations = listOf(migration)
        )

        val result = runBlocking { migrator.migrate() }

        expectThat(result).isEqualTo(Result.FAILED)
    }

    @Test
    fun `migrate() runs migrations in the correct order`() {
        // Create some ordered migrations
        val orderedMigrations = listOf(
            spyk(
                object : VersionMigration(1, 2) {
                    override suspend fun migrate(): Result = Result.SUCCESS
                }
            ),
            spyk(
                object : VersionMigration(2, 3) {
                    override suspend fun migrate(): Result = Result.SUCCESS
                }
            ),
            spyk(
                object : VersionMigration(3, 4) {
                    override suspend fun migrate(): Result = Result.SUCCESS
                }
            )
        )

        // Create a Migrator with shuffled migrations
        val migrator = ConcreteMigrator(
            oldVersion = 1,
            currentVersion = 4,
            migrations = orderedMigrations.shuffled()
        )

        // Run migrations
        runBlocking { migrator.migrate() }

        // Check migrations were executed in the right order
        coVerifyOrder {
            orderedMigrations[0].migrate()
            orderedMigrations[1].migrate()
            orderedMigrations[2].migrate()
        }
    }

    @Test
    fun `migrate() returns not_needed when no migrations were run`() {
        // Create a Migrator with no migrations
        val migrator = ConcreteMigrator(
            oldVersion = 4,
            currentVersion = 4,
            migrations = emptyList()
        )

        // Run migrations
        val result = runBlocking { migrator.migrate() }

        expectThat(result).isEqualTo(Result.NOT_NEEDED)
    }

    @Test
    fun `migrate() runs conditional migrations even when version hasn't changed`() {
        // Create some ordered migrations
        val migrations = listOf(
            spyk(
                object : VersionMigration(1, 2) {
                    override suspend fun migrate(): Result = Result.SUCCESS
                }
            ),
            spyk(
                object : VersionMigration(2, 3) {
                    override suspend fun migrate(): Result = Result.SUCCESS
                }
            ),
            spyk(
                object : ConditionalMigration() {
                    override suspend fun migrate(): Result = Result.SUCCESS
                    override suspend fun shouldMigrate(fromVersion: Int): Boolean = true
                }
            )
        )

        // Create a Migrator with shuffled migrations
        val migrator = ConcreteMigrator(
            oldVersion = 4,
            currentVersion = 4,
            migrations = migrations
        )

        // Run migrations
        runBlocking { migrator.migrate() }

        // Check version migrations were not executed
        coVerify(inverse = true) {
            migrations[0].migrate()
            migrations[1].migrate()
        }
        // Check conditional migration was executed
        coVerify { migrations[2].migrate() }
    }

    @Test
    fun `abortOnError aborts on error when true`() {
        val migrations = listOf(
            spyk(
                object : VersionMigration(1, 2) {
                    override suspend fun migrate(): Result = Result.FAILED
                }
            ),
            spyk(
                object : VersionMigration(2, 3) {
                    override suspend fun migrate(): Result = Result.SUCCESS
                }
            )
        )
        val migrator = ConcreteMigrator(
            oldVersion = 1,
            currentVersion = 3,
            abortOnError = true,
            migrations = migrations
        )

        runBlocking { migrator.migrate() }

        coVerify(inverse = true) { migrations[1].migrate() }
    }

    @Test
    fun `abortOnError continues on error when false`() {
        val migrations = listOf(
            spyk(
                object : VersionMigration(1, 2) {
                    override suspend fun migrate(): Result = Result.FAILED
                }
            ),
            spyk(
                object : VersionMigration(2, 3) {
                    override suspend fun migrate(): Result = Result.SUCCESS
                }
            )
        )
        val migrator = ConcreteMigrator(
            oldVersion = 1,
            currentVersion = 3,
            abortOnError = false,
            migrations = migrations
        )

        runBlocking { migrator.migrate() }

        coVerify { migrations[1].migrate() }
    }

    @Test
    fun `abortOnError still returns failure`() {
        val migrations = listOf(
            spyk(
                object : VersionMigration(1, 2) {
                    override suspend fun migrate(): Result = Result.FAILED
                }
            ),
            spyk(
                object : VersionMigration(2, 3) {
                    override suspend fun migrate(): Result = Result.SUCCESS
                }
            )
        )
        val migrator = ConcreteMigrator(
            oldVersion = 1,
            currentVersion = 3,
            abortOnError = false,
            migrations = migrations
        )

        val result = runBlocking { migrator.migrate() }

        expectThat(result).isEqualTo(Result.FAILED)
    }

    @Test
    fun `migrate() returns failed on error`() {
        val migrations = listOf(
            object : VersionMigration(1, 2) {
                override suspend fun migrate(): Result = Result.FAILED
            },
            object : VersionMigration(2, 3) {
                override suspend fun migrate(): Result = Result.SUCCESS
            }
        )
        val migrator = ConcreteMigrator(
            oldVersion = 1,
            currentVersion = 3,
            migrations = migrations
        )

        val result = runBlocking { migrator.migrate() }

        expectThat(result).isEqualTo(Result.FAILED)
    }

    @Test
    fun `migrate() returns success on success`() {
        val migrations = listOf(
            object : VersionMigration(1, 2) {
                override suspend fun migrate(): Result = Result.SUCCESS
            },
            object : VersionMigration(2, 3) {
                override suspend fun migrate(): Result = Result.SUCCESS
            }
        )
        val migrator = ConcreteMigrator(
            oldVersion = 1,
            currentVersion = 3,
            migrations = migrations
        )

        val result = runBlocking { migrator.migrate() }

        expectThat(result).isEqualTo(Result.SUCCESS)
    }

    @Test
    fun `migrate() throws exception when a migration path cannot be found`() {
        val migrator = ConcreteMigrator(
            oldVersion = 1,
            currentVersion = 2,
            migrations = emptyList()
        )

        expectThrows<IllegalStateException> {
            migrator.migrate()
        }
    }

    @Test
    fun `migrate() throws exception when old version is greater than current version`() {
        val migrator = ConcreteMigrator(
            oldVersion = 2,
            currentVersion = 1,
            migrations = emptyList()
        )

        expectThrows<IllegalStateException> {
            migrator.migrate()
        }
    }

    @Test
    fun `onMigrateTo() is called after successful migration`() {
        val migrations = listOf(
            object : VersionMigration(1, 2) {
                override suspend fun migrate(): Result = Result.SUCCESS
            },
            object : VersionMigration(2, 3) {
                override suspend fun migrate(): Result = Result.SUCCESS
            }
        )
        val migrator = ConcreteMigrator(
            oldVersion = 1,
            currentVersion = 3,
            migrations = migrations
        )

        runBlocking { migrator.migrate() }

        expectThat(migrator.migratedTo).isEqualTo(3)
    }

    @Test
    fun `onMigrateTo() is called after failed migration`() {
        val migrations = listOf(
            object : VersionMigration(1, 2) {
                override suspend fun migrate(): Result = Result.SUCCESS
            },
            object : VersionMigration(2, 3) {
                override suspend fun migrate(): Result = Result.FAILED
            }
        )
        val migrator = ConcreteMigrator(
            oldVersion = 1,
            currentVersion = 3,
            migrations = migrations
        )

        runBlocking { migrator.migrate() }

        expectThat(migrator.migratedTo).isEqualTo(2)
    }
}
