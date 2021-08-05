package com.boswelja.migration

actual fun <R> runBlocking(block: suspend () -> R): R = runBlocking(block)
