package com.boswelja.migration

enum class Result {
    /**
     * Indicates the operation succeeded.
     */
    SUCCESS,

    /**
     * Indicates the operation failed.
     */
    FAILED,

    /**
     * Indicates no operations were performed.
     */
    NOT_NEEDED
}

/**
 * Combine a number of [Result]s into a single [Result].
 * @param results The [Result]s to combine.
 * @return the combined result.
 */
fun combineResults(vararg results: Result): Result {
    return when {
        results.all { it == Result.SUCCESS } -> {
            Result.SUCCESS
        }
        results.all { it == Result.NOT_NEEDED } -> {
            Result.NOT_NEEDED
        }
        else -> {
            Result.FAILED
        }
    }
}
