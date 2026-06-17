package com.elow.app.core.goals

import com.elow.app.core.catalog.ItemCatalog
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.GoalStatus
import com.elow.app.core.model.IntakeFamily
import com.elow.app.core.model.IntakeRecord
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object GoalEvaluator {
    fun evaluateWeek(
        records: List<IntakeRecord>,
        nowEpochMillis: Long,
        settings: GoalSettings,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): GoalStatus {
        val startOfWindow = Instant.ofEpochMilli(nowEpochMillis)
            .atZone(zoneId)
            .truncatedTo(ChronoUnit.DAYS)
            .minusDays(6)
            .toInstant()
            .toEpochMilli()

        val weekRecords = records.filter { it.timestampEpochMillis >= startOfWindow }
        val sweetDrinks = weekRecords.count {
            ItemCatalog.definitionFor(it.itemType).family == IntakeFamily.SUGAR
        }
        val alcoholOccasions = weekRecords.count {
            ItemCatalog.definitionFor(it.itemType).family == IntakeFamily.ALCOHOL
        }

        return GoalStatus(
            sweetDrinksThisWeek = sweetDrinks,
            alcoholOccasionsThisWeek = alcoholOccasions,
            sweetDrinkLimit = settings.weeklySweetDrinkLimit,
            alcoholOccasionLimit = settings.weeklyAlcoholOccasionLimit
        )
    }
}

