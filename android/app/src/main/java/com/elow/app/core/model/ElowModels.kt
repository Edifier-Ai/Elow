package com.elow.app.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class ItemType {
    COLA,
    MILK_TEA,
    BEER,
    WINE
}

@Serializable
enum class IntakeFamily {
    SUGAR,
    ALCOHOL
}

@Serializable
data class IntakeRecord(
    val id: String,
    val itemType: ItemType,
    val amountFraction: Double,
    val timestampEpochMillis: Long,
    val metrics: MetricsEstimate,
    val note: String = ""
)

@Serializable
data class MetricsEstimate(
    val sugarGrams: Double,
    val alcoholGrams: Double,
    val calories: Double,
    val money: Double
)

@Serializable
data class ItemDefinition(
    val type: ItemType,
    val displayName: String,
    val family: IntakeFamily,
    val fullServingLabel: String,
    val fullServingMl: Int,
    val fullServingSugarGrams: Double,
    val fullServingAlcoholGrams: Double,
    val fullServingCalories: Double,
    val fullServingMoney: Double,
    val imageUrl: String = "",
    val enabled: Boolean = true
)

@Serializable
data class GoalSettings(
    val weeklySweetDrinkLimit: Int = 7,
    val weeklyAlcoholOccasionLimit: Int = 3,
    val dailySugarGramTarget: Int = 60
)

@Serializable
data class GoalStatus(
    val sweetDrinksThisWeek: Int,
    val alcoholOccasionsThisWeek: Int,
    val sweetDrinkLimit: Int,
    val alcoholOccasionLimit: Int
)

@Serializable
data class HonorReward(
    val id: String,
    val title: String,
    val description: String,
    val itemType: ItemType?,
    val tier: Int
)
