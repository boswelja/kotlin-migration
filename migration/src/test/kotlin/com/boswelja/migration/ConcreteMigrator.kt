package com.boswelja.migration

class ConcreteMigrator(
    private val oldVersion: Int,
    currentVersion: Int,
    abortOnError: Boolean = true,
    migrations: List<Migration>
) : Migrator(currentVersion, abortOnError, migrations) {
    override suspend fun getOldVersion(): Int = oldVersion
}
