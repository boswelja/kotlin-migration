package com.boswelja.migration

import kotlin.test.Test
import kotlin.test.assertEquals

class ResultTest {

    @Test
    fun `combineResults returns SUCCESS if all passed are SUCCESS`() {
        val results = arrayOf(
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS
        )

        assertEquals(combineResults(*results), Result.SUCCESS)
    }

    @Test
    fun `combineResults returns FAILED if one passed is FAILED`() {
        val results = arrayOf(
            Result.SUCCESS,
            Result.FAILED,
            Result.SUCCESS
        )

        assertEquals(combineResults(*results), Result.FAILED)
    }

    @Test
    fun `combineResults returns NOT_NEEDED if all passed are NOT_NEEDED`() {
        val results = arrayOf(
            Result.NOT_NEEDED,
            Result.NOT_NEEDED,
            Result.NOT_NEEDED
        )

        assertEquals(combineResults(*results), Result.NOT_NEEDED)
    }
}
