package com.elow.app.core.honor

import com.elow.app.core.metrics.MetricEstimator
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HonorWallEngineTest {
    @Test
    fun awardsSweetDrinkShelfWhenUserStaysBelowWeeklyGoal() {
        val now = 1_777_000_000_000L
        val records = listOf(record("1", ItemType.COLA, now))

        val rewards = HonorWallEngine.rewardsForWeek(
            records = records,
            nowEpochMillis = now,
            settings = GoalSettings(weeklySweetDrinkLimit = 3, weeklyAlcoholOccasionLimit = 2)
        )

        assertTrue(rewards.any { it.id == "sweet-drink-shelf-1" })
    }

    @Test
    fun doesNotAwardAlcoholShelfWhenAlcoholGoalIsExceeded() {
        val now = 1_777_000_000_000L
        val records = listOf(
            record("1", ItemType.BEER, now),
            record("2", ItemType.WINE, now)
        )

        val rewards = HonorWallEngine.rewardsForWeek(
            records = records,
            nowEpochMillis = now,
            settings = GoalSettings(weeklySweetDrinkLimit = 3, weeklyAlcoholOccasionLimit = 1)
        )

        assertEquals(false, rewards.any { it.id == "alcohol-shelf-1" })
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

