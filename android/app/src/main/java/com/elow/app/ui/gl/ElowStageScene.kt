package com.elow.app.ui.gl

import com.elow.app.core.model.ItemType
import kotlin.math.roundToInt

sealed class ElowStageScene {
    data object OnboardingShelf : ElowStageScene()

    data object HomeSugar : ElowStageScene()

    data object HomeAlcohol : ElowStageScene() {
        val items: List<ItemType> = listOf(ItemType.WINE, ItemType.BEER)
    }

    data class AddObject(
        val itemType: ItemType,
        val amountFraction: Double
    ) : ElowStageScene() {
        val displayAmountMl: Int =
            (displayMaxMl(itemType) * amountFraction.coerceIn(0.0, 1.0)).roundToInt()
    }

    data object HonorCollectibles : ElowStageScene()
}

private fun displayMaxMl(itemType: ItemType): Int =
    if (itemType == ItemType.WINE) 250 else 500
