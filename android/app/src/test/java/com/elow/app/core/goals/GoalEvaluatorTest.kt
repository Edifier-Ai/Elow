package com.elow.app.core.goals

import com.elow.app.core.metrics.MetricEstimator
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemType
import org.junit.Assert.assertEquals
import org.junit.Test

class GoalEvaluatorTest {
    @Test
    fun countsSweetDrinksAndAlcoholOccasionsForCurrentWeek() {
        val now = 1_777_000_000_000L
        val records = listOf(
            record("1", ItemType.COLA, now),
            record("2", ItemType.MILK_TEA, now),
            record("3", ItemType.BEER, now),
            record("old", ItemType.WINE, now - 10L * 24L * 60L * 60L * 1000L)
        )

        val status = GoalEvaluator.evaluateWeek(
            records = records,
            nowEpochMillis = now,
            settings = GoalSettings(weeklySweetDrinkLimit = 4, weeklyAlcoholOccasionLimit = 2)
        )

        assertEquals(2, status.sweetDrinksThisWeek)
        assertEquals(1, status.alcoholOccasionsThisWeek)
        assertEquals(4, status.sweetDrinkLimit)
        assertEquals(2, status.alcoholOccasionLimit)
    }

    private fun record(id: String, type: ItemType, time: Long): IntakeRecord =
        IntakeRecord(
            id = id,
            itemType = type,
            amountFraction = 1.0,
            timestampEpochMillis = time,
            metrics = MetricEstimator.estimate(type, 1.0)
        )
}

