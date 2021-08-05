package com.boswelja.migration

import kotlin.test.Test
import kotlin.test.assertTrue

class VersionMigrationTest {

    @Test
    fun `shouldMigrate returns true if the migration can be applied`() {
        val fromVersion = 1
        val toVersion = 2
        val migration = versionMigration(fromVersion, toVersion) { true }

        val shouldMigrate = runBlocking { migration.shouldMigrate(fromVersion) }
        assertTrue(shouldMigrate)
    }

    @Test
    fun `shouldMigrate returns false if the migration cannot be applied`() {
        val fromVersion = 1
        val toVersion = 2
        val migration = versionMigration(fromVersion, toVersion) { true }

        val shouldMigrate = runBlocking { migration.shouldMigrate(toVersion) }
        assertTrue(shouldMigrate)
    }
}
