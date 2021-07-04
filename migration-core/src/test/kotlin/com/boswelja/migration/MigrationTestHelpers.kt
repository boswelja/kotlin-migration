package com.boswelja.migration

fun createVersionedMigrations(
    fromVersion: Int,
    toVersion: Int,
    migrateResult: (fromVersion: Int) -> Boolean
) = (fromVersion..toVersion).map { fromVer ->
    versionMigration(fromVer, fromVer + 1) { migrateResult(fromVer) }
}

fun createConstantMigrations(
    count: Int,
    shouldMigrate: (index: Int) -> Boolean,
    migrateResult: (index: Int) -> Boolean
) = (0 until count).map { index ->
    conditionalMigration(
        onShouldMigrate = { shouldMigrate(index) },
        onMigrate = { migrateResult(index) }
    )
}
