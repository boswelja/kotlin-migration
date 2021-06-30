package com.boswelja.migration

class ConcreteMigrator(
    private val oldVersion: Int,
    currentVersion: Int,
    abortOnError: Boolean = true,
    migrations: List<Migration>
) : Migrator(currentVersion, abortOnError, migrations) {
    var migratedTo = oldVersion

    override suspend fun getOldVersion(): Int = oldVersion
    override suspend fun onMigratedTo(version: Int) {
        migratedTo = version
    }
}
