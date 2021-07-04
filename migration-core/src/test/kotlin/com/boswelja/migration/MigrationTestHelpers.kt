package com.boswelja.migration

import io.mockk.spyk

class ConcreteVersionMigration(
    fromVersion: Int,
    toVersion: Int,
    private val migrateResult: (fromVersion: Int) -> Boolean
) : VersionMigration(fromVersion, toVersion) {
    override suspend fun migrate(): Boolean = migrateResult(fromVersion)
}

class ConcreteConstatntMigration(
    private val shouldMigrateResult: (fromVersion: Int) -> Boolean,
    private val migrateResult: () -> Boolean
) : Migration {
    override val toVersion: Int? = null
    override suspend fun shouldMigrate(fromVersion: Int): Boolean = shouldMigrateResult(fromVersion)
    override suspend fun migrate(): Boolean = migrateResult()
}

fun createVersionedMigrations(
    fromVersion: Int,
    toVersion: Int,
    migrateResult: (fromVersion: Int) -> Boolean
) = (fromVersion..toVersion).map { fromVer ->
    spyk(ConcreteVersionMigration(fromVer, fromVer + 1, migrateResult))
}

fun createConstantMigrations(
    count: Int,
    shouldMigrate: (index: Int) -> Boolean,
    migrateResult: (index: Int) -> Boolean
) = (0 until count).map { index ->
    spyk(
        ConcreteConstatntMigration(
            shouldMigrate,
            { migrateResult(index) }
        )
    )
}
