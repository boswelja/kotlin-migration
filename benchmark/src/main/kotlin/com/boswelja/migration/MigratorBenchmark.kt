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
    private val migrations = (fromVersion..toVersion).map { fromVer ->
        object : VersionMigration(fromVer, fromVer + 1) {
            override suspend fun migrate(): Result = Result.SUCCESS
        }
    }

    private lateinit var migrator: Migrator

    @Setup
    fun setUp() {
        migrator = object : Migrator(
            currentVersion = toVersion,
            migrations = migrations
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
    fun buildMigrationMapBenchmark(): Unit = runBlocking {
        migrator.buildMigrationMap(
            migrations,
            fromVersion
        )
    }
}
