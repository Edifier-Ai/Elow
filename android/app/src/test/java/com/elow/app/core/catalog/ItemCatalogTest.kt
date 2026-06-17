package com.elow.app.core.catalog

import com.elow.app.core.model.ItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ItemCatalogTest {
    @Test
    fun firstVersionContainsExactlyFourItems() {
        assertEquals(
            listOf(ItemType.COLA, ItemType.MILK_TEA, ItemType.BEER, ItemType.WINE),
            ItemCatalog.firstVersionItems.map { it.type }
        )
    }

    @Test
    fun sugarItemsHaveSugarAndAlcoholItemsHaveAlcohol() {
        val cola = ItemCatalog.definitionFor(ItemType.COLA)
        val milkTea = ItemCatalog.definitionFor(ItemType.MILK_TEA)
        val beer = ItemCatalog.definitionFor(ItemType.BEER)
        val wine = ItemCatalog.definitionFor(ItemType.WINE)

        assertTrue(cola.fullServingSugarGrams > 0.0)
        assertTrue(milkTea.fullServingSugarGrams > 0.0)
        assertEquals(0.0, cola.fullServingAlcoholGrams, 0.0)
        assertEquals(0.0, milkTea.fullServingAlcoholGrams, 0.0)
        assertTrue(beer.fullServingAlcoholGrams > 0.0)
        assertTrue(wine.fullServingAlcoholGrams > 0.0)
    }
}

