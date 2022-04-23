package com.boswelja.migration

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class VersionMigrationTest {

    @Test
    fun shouldMigrateReturnsTrueIfTheMigrationCanBeApplied() = runTest {
        val fromVersion = 1
        val toVersion = 2
        val migration = versionMigration(fromVersion, toVersion) { true }

        assertTrue {
            migration.shouldMigrate(fromVersion)
        }
    }

    @Test
    fun shouldMigrateReturnsFalseIfTheMigrationCannotBeApplied() = runTest {
        val fromVersion = 1
        val toVersion = 2
        val migration = versionMigration(fromVersion, toVersion) { true }

        assertFalse {
            migration.shouldMigrate(toVersion)
        }
    }
}
