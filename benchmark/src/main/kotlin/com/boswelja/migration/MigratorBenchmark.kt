package com.boswelja.migration

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Mode
import kotlinx.benchmark.Setup
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class MigratorBenchmark {
    final val fromVersion = 1
    final val toVersion = 100
    // Create some basic migrations that do nothing
    private val versionedMigrations = (fromVersion..toVersion).map { fromVer ->
        object : VersionMigration(fromVer, fromVer + 1) {
            override suspend fun migrate(): Boolean = true
        }
    }
    private val constantMigrations = (fromVersion..toVersion).map {
        object : ConditionalMigration() {
            override suspend fun shouldMigrate(fromVersion: Int): Boolean = true
            override suspend fun migrate(): Boolean = true
        }
    }

    private lateinit var migrator: Migrator

    @Setup
    fun setUp() {
        migrator = object : Migrator(
            currentVersion = toVersion,
            migrations = versionedMigrations + constantMigrations
        ) {
            override suspend fun onMigratedTo(version: Int) {
                // Do nothing
            }

            override suspend fun getOldVersion(): Int = fromVersion
        }
    }

    @Benchmark
    fun migrateBenchmark(): Unit = runBlocking {
        migrator.migrate()
    }

    @Benchmark
    fun runVersionedMigrationsBenchmark(): Unit = runBlocking {
        migrator.runVersionedMigrations(
            fromVersion,
            versionedMigrations
        )
    }

    @Benchmark
    fun runConstantMigrationsBenchmark(): Unit = runBlocking {
        migrator.runConstantMigrations(
            fromVersion,
            constantMigrations
        )
    }
}
