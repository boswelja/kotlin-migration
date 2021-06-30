package com.boswelja.migration

/**
 * Split a list into two parts, one with all elements matching a given predicate, and one with all
 * elements not matching the given predicate.
 * @param predicate The predicate to filter elements by.
 * @return a [Pair] of [List]s, where [Pair.first] contains all elements matching [predicate], and
 * [Pair.second] contains all elements not matching [predicate].
 */
suspend fun <T> List<T>.separate(predicate: suspend (T) -> Boolean): Pair<List<T>, List<T>> {
    val matching = mutableListOf<T>()
    val notMatching = mutableListOf<T>()

    forEach { item ->
        if (predicate(item)) {
            matching.add(item)
        } else {
            notMatching.add(item)
        }
    }

    return Pair(matching, notMatching)
}
