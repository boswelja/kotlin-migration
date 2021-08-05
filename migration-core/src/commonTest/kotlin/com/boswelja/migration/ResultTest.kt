package com.boswelja.migration

import kotlin.test.Test
import kotlin.test.assertEquals

class ResultTest {

    @Test
    fun combineResultsReturnsSUCCESSIfAllPassedAreSUCCESS() {
        val results = arrayOf(
            Result.SUCCESS,
            Result.SUCCESS,
            Result.SUCCESS
        )

        assertEquals(combineResults(*results), Result.SUCCESS)
    }

    @Test
    fun combineResultsReturnsFAILEDIfOnePassedIsFAILED() {
        val results = arrayOf(
            Result.SUCCESS,
            Result.FAILED,
            Result.SUCCESS
        )

        assertEquals(combineResults(*results), Result.FAILED)
    }

    @Test
    fun combineResultsReturnsNOT_NEEDEDIfAllPassedAreNOT_NEEDED() {
        val results = arrayOf(
            Result.NOT_NEEDED,
            Result.NOT_NEEDED,
            Result.NOT_NEEDED
        )

        assertEquals(combineResults(*results), Result.NOT_NEEDED)
    }
}
