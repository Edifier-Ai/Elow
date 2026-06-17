package com.elow.app.ui.reference

import com.elow.app.core.metrics.MetricEstimator
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

object DesignReferenceContent {
    const val homeSugarGrams: Double = 28.0
    const val homeAlcoholOccasions: Int = 1

    fun homeRecentRecords(nowEpochMillis: Long): List<IntakeRecord> {
        val zone = ZoneId.systemDefault()
        val date = Instant.ofEpochMilli(nowEpochMillis).atZone(zone).toLocalDate()
        return listOf(
            IntakeRecord(
                id = "design-reference-cola",
                itemType = ItemType.COLA,
                amountFraction = 0.66,
                timestampEpochMillis = date.atDesignTime(zone, hour = 16, minute = 35),
                metrics = MetricEstimator.estimate(ItemType.COLA, 0.66)
            ),
            IntakeRecord(
                id = "design-reference-wine",
                itemType = ItemType.WINE,
                amountFraction = 1.0,
                timestampEpochMillis = date.atDesignTime(zone, hour = 19, minute = 12),
                metrics = MetricEstimator.estimate(ItemType.WINE, 1.0)
            )
        )
    }
}

private fun LocalDate.atDesignTime(zone: ZoneId, hour: Int, minute: Int): Long =
    ZonedDateTime.of(this, LocalTime.of(hour, minute), zone).toInstant().toEpochMilli()
