package com.boswelja.migration

import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class MigratorTest {

    @Test
    fun `migrate() succeeds with one migration`() {
        // Set up dummy migrations
        val migration = object : VersionMigration(1, 2) {
            override suspend fun migrate(): Boolean {
                return true
            }
        }
        val migrator = ConcreteMigrator(
            oldVersion = 1,
            currentVersion = 2,
            migrations = listOf(migration)
        )

        val result = runBlocking { migrator.migrate() }

        expectThat(result).isTrue()
    }

    @Test
    fun `migrate() fails when migration fails`() {
        // Set up dummy migrations
        val migration = object : VersionMigration(1, 2) {
            override suspend fun migrate(): Boolean {
                return false
            }
        }
        val migrator = ConcreteMigrator(
            oldVersion = 1,
            currentVersion = 2,
            migrations = listOf(migration)
        )

        val result = runBlocking { migrator.migrate() }

        expectThat(result).isFalse()
    }

    @Test
    fun `migrate() runs migrations in the correct order`() {
        // Create some ordered migrations
        val orderedMigrations = listOf(
            spyk(
                object : VersionMigration(1, 2) {
                    override suspend fun migrate(): Boolean = true
                }
            ),
            spyk(
                object : VersionMigration(2, 3) {
                    override suspend fun migrate(): Boolean = true
                }
            ),
            spyk(
                object : VersionMigration(3, 4) {
                    override suspend fun migrate(): Boolean = true
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
    fun `migrate() does nothing when version hasn't changed`() {
        // Create some ordered migrations
        val migrations = listOf(
            spyk(
                object : VersionMigration(1, 2) {
                    override suspend fun migrate(): Boolean = true
                }
            ),
            spyk(
                object : VersionMigration(2, 3) {
                    override suspend fun migrate(): Boolean = true
                }
            ),
            spyk(
                object : VersionMigration(3, 4) {
                    override suspend fun migrate(): Boolean = true
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

        // Check migrations were not executed
        coVerify(inverse = true) {
            migrations[0].migrate()
            migrations[1].migrate()
            migrations[2].migrate()
        }
    }

    @Test
    fun `abortOnError aborts on error when true`() {
        val migrations = listOf(
            spyk(
                object : VersionMigration(1, 2) {
                    override suspend fun migrate(): Boolean = false
                }
            ),
            spyk(
                object : VersionMigration(2, 3) {
                    override suspend fun migrate(): Boolean = true
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
                    override suspend fun migrate(): Boolean = false
                }
            ),
            spyk(
                object : VersionMigration(2, 3) {
                    override suspend fun migrate(): Boolean = true
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
    fun `migrate() returns false on error`() {
        val migrations = listOf(
            object : VersionMigration(1, 2) {
                override suspend fun migrate(): Boolean = false
            },
            object : VersionMigration(2, 3) {
                override suspend fun migrate(): Boolean = true
            }
        )
        val migrator = ConcreteMigrator(
            oldVersion = 1,
            currentVersion = 3,
            migrations = migrations
        )

        val result = runBlocking { migrator.migrate() }

        expectThat(result).isFalse()
    }

    @Test
    fun `migrate() returns true on success`() {
        val migrations = listOf(
            object : VersionMigration(1, 2) {
                override suspend fun migrate(): Boolean = true
            },
            object : VersionMigration(2, 3) {
                override suspend fun migrate(): Boolean = true
            }
        )
        val migrator = ConcreteMigrator(
            oldVersion = 1,
            currentVersion = 3,
            migrations = migrations
        )

        val result = runBlocking { migrator.migrate() }

        expectThat(result).isTrue()
    }

    @Test
    fun `migrate() throws exception when a migration path cannot be found`() {
        val migrator = ConcreteMigrator(
            oldVersion = 1,
            currentVersion = 2,
            migrations = emptyList()
        )

        expectThrows<IllegalArgumentException> {
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
}
