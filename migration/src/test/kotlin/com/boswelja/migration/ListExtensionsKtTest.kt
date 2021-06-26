package com.boswelja.migration

import kotlinx.coroutines.runBlocking
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo

class ListExtensionsKtTest {

    @Test
    fun `separate handles empty list`() {
        val list = emptyList<String>()
        val result = runBlocking { list.separate { it.isEmpty() } }

        expectThat(result.first).isEmpty()
        expectThat(result.second).isEmpty()
    }

    @Test
    fun `separate handles all elements matching predicate`() {
        val list = List(10) { 1 }

        val result = runBlocking { list.separate { it == 1 } }

        expectThat(result.first).containsExactlyInAnyOrder(list)
        expectThat(result.second).isEmpty()
    }

    @Test
    fun `separate handles all elements not matching predicate`() {
        val list = List(10) { 1 }

        val result = runBlocking { list.separate { it != 1 } }

        expectThat(result.first).isEmpty()
        expectThat(result.second).containsExactlyInAnyOrder(list)
    }

    @Test
    fun `separate handles mix of elements matching predicates`() {
        val list = List(10) { if (it % 2 == 0) 1 else 0 }

        val result = runBlocking { list.separate { it == 1 } }

        result.first.forEach {
            expectThat(it).isEqualTo(1)
        }
        result.second.forEach {
            expectThat(it).isEqualTo(0)
        }
    }
}
