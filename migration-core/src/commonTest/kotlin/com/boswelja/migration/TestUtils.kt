package com.boswelja.migration

expect fun <R> runBlocking(block: suspend () -> R): R
