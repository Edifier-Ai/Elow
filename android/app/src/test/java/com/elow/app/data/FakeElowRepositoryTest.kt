package com.elow.app.data

import com.elow.app.core.metrics.MetricEstimator
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FakeElowRepositoryTest {
    @Test
    fun addRecordPublishesRecords() = runTest {
        val repository = FakeElowRepository()
        val record = IntakeRecord(
            id = "record-1",
            itemType = ItemType.COLA,
            amountFraction = 0.5,
            timestampEpochMillis = 1_777_000_000_000L,
            metrics = MetricEstimator.estimate(ItemType.COLA, 0.5)
        )

        repository.addRecord(record)

        assertEquals(listOf(record), repository.records.first())
    }

    @Test
    fun updateGoalsPublishesSettings() = runTest {
        val repository = FakeElowRepository()
        val goals = GoalSettings(weeklySweetDrinkLimit = 4, weeklyAlcoholOccasionLimit = 1)

        repository.updateGoals(goals)

        assertEquals(goals, repository.goals.first())
    }
}

