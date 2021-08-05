package com.boswelja.migration

import kotlin.test.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ResultTest {

    @Test
    fun `combineResults returns SUCCESS if all passed are SUCCESS`() {
        val results = arrayOf(
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS
        )

        expectThat(combineResults(*results)).isEqualTo(Result.SUCCESS)
    }

    @Test
    fun `combineResults returns FAILED if one passed is FAILED`() {
        val results = arrayOf(
            Result.SUCCESS,
            Result.FAILED,
            Result.SUCCESS
        )

        expectThat(combineResults(*results)).isEqualTo(Result.FAILED)
    }

    @Test
    fun `combineResults returns NOT_NEEDED if all passed are NOT_NEEDED`() {
        val results = arrayOf(
            Result.NOT_NEEDED,
            Result.NOT_NEEDED,
            Result.NOT_NEEDED
        )

        expectThat(combineResults(*results)).isEqualTo(Result.NOT_NEEDED)
    }
}
