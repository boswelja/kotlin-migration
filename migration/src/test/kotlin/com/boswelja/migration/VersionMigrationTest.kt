package com.boswelja.migration

import kotlinx.coroutines.runBlocking
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class VersionMigrationTest {

    @Test
    fun `shouldMigrate returns true if the migration can be applied`() {
        val fromVersion = 1
        val toVersion = 2
        val migration = object : VersionMigration(fromVersion, toVersion) {
            override suspend fun migrate(): Result = Result.SUCCESS
        }

        val shouldMigrate = runBlocking { migration.shouldMigrate(fromVersion) }
        expectThat(shouldMigrate).isTrue()
    }

    @Test
    fun `shouldMigrate returns false if the migration cannot be applied`() {
        val fromVersion = 1
        val toVersion = 2
        val migration = object : VersionMigration(fromVersion, toVersion) {
            override suspend fun migrate(): Result = Result.SUCCESS
        }

        val shouldMigrate = runBlocking { migration.shouldMigrate(toVersion) }
        expectThat(shouldMigrate).isFalse()
    }
}
