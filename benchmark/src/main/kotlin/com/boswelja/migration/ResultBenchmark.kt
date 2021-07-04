package com.boswelja.migration

import kotlinx.benchmark.Mode
import org.openjdk.jmh.annotations.Benchmark
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
class ResultBenchmark {

    @Benchmark
    fun combineSuccessResultsBenchmark() {
        combineResults(
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS
        )
    }

    @Benchmark
    fun combineFailedResultsBenchmark() {
        combineResults(
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS,
            Result.FAILED
        )
    }

    @Benchmark
    fun combineNotNeededResultsBenchmark() {
        combineResults(
            Result.NOT_NEEDED,
            Result.NOT_NEEDED,
            Result.NOT_NEEDED,
            Result.NOT_NEEDED,
            Result.NOT_NEEDED,
            Result.NOT_NEEDED,
            Result.NOT_NEEDED,
            Result.NOT_NEEDED,
            Result.NOT_NEEDED,
            Result.NOT_NEEDED
        )
    }
}
