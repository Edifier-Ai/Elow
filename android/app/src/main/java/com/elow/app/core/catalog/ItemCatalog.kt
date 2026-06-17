package com.elow.app.core.catalog

import com.elow.app.core.model.IntakeFamily
import com.elow.app.core.model.ItemDefinition
import com.elow.app.core.model.ItemType

object ItemCatalog {
    val firstVersionItems: List<ItemDefinition> = listOf(
        ItemDefinition(
            type = ItemType.COLA,
            displayName = "Cola",
            family = IntakeFamily.SUGAR,
            fullServingLabel = "1 bottle",
            fullServingMl = 500,
            fullServingSugarGrams = 50.0,
            fullServingAlcoholGrams = 0.0,
            fullServingCalories = 140.0,
            fullServingMoney = 1.50
        ),
        ItemDefinition(
            type = ItemType.MILK_TEA,
            displayName = "Milk Tea",
            family = IntakeFamily.SUGAR,
            fullServingLabel = "1 cup",
            fullServingMl = 500,
            fullServingSugarGrams = 45.0,
            fullServingAlcoholGrams = 0.0,
            fullServingCalories = 280.0,
            fullServingMoney = 5.50
        ),
        ItemDefinition(
            type = ItemType.BEER,
            displayName = "Beer",
            family = IntakeFamily.ALCOHOL,
            fullServingLabel = "1 can",
            fullServingMl = 355,
            fullServingSugarGrams = 0.0,
            fullServingAlcoholGrams = 14.0,
            fullServingCalories = 153.0,
            fullServingMoney = 4.00
        ),
        ItemDefinition(
            type = ItemType.WINE,
            displayName = "Wine",
            family = IntakeFamily.ALCOHOL,
            fullServingLabel = "1 glass",
            fullServingMl = 150,
            fullServingSugarGrams = 0.0,
            fullServingAlcoholGrams = 24.0,
            fullServingCalories = 125.0,
            fullServingMoney = 6.00
        )
    )

    fun definitionFor(type: ItemType): ItemDefinition =
        firstVersionItems.first { it.type == type }

    fun definitionFor(type: ItemType, catalog: List<ItemDefinition>): ItemDefinition =
        catalog.firstOrNull { it.type == type } ?: definitionFor(type)

    fun visibleItems(catalog: List<ItemDefinition>): List<ItemDefinition> =
        catalog.filter { it.enabled }.ifEmpty { firstVersionItems }
}
