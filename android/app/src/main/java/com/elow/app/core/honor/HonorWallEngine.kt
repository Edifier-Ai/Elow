package com.elow.app.core.honor

import com.elow.app.core.goals.GoalEvaluator
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.HonorReward
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemType

object HonorWallEngine {
    fun rewardsForWeek(
        records: List<IntakeRecord>,
        nowEpochMillis: Long,
        settings: GoalSettings
    ): List<HonorReward> {
        if (records.isEmpty()) return emptyList()

        val status = GoalEvaluator.evaluateWeek(records, nowEpochMillis, settings)
        val rewards = mutableListOf(
            HonorReward(
                id = "first-record",
                title = "First Record",
                description = "You made the first clear record.",
                itemType = records.minByOrNull { it.timestampEpochMillis }?.itemType,
                tier = 1
            )
        )

        records.map { it.itemType }.distinct().take(4).forEach { item ->
            rewards += HonorReward(
                id = "collectible-${item.name.lowercase()}",
                title = collectibleTitle(item),
                description = "Unlocked by recording ${collectibleTitle(item).lowercase()}.",
                itemType = item,
                tier = 1
            )
        }

        if (status.sweetDrinksThisWeek <= status.sweetDrinkLimit) {
            rewards += HonorReward(
                id = "sweet-drink-shelf-1",
                title = "Sweet Shelf",
                description = "Stayed within your sweet drink target this week.",
                itemType = ItemType.COLA,
                tier = 1
            )
        }

        if (status.alcoholOccasionsThisWeek <= status.alcoholOccasionLimit) {
            rewards += HonorReward(
                id = "alcohol-shelf-1",
                title = "Clear Shelf",
                description = "Stayed within your alcohol target this week.",
                itemType = ItemType.BEER,
                tier = 1
            )
        }

        if (records.size >= 7) {
            rewards += HonorReward(
                id = "seven-records",
                title = "7 Records",
                description = "Seven honest records are on the wall.",
                itemType = null,
                tier = 2
            )
        }

        return rewards.distinctBy { it.id }
    }

    private fun collectibleTitle(itemType: ItemType): String =
        when (itemType) {
            ItemType.COLA -> "Cola"
            ItemType.MILK_TEA -> "Milk Tea"
            ItemType.BEER -> "Beer"
            ItemType.WINE -> "Wine"
        }
}

