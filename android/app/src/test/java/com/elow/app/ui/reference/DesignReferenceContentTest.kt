package com.elow.app.ui.reference

import com.elow.app.core.model.ItemType
import org.junit.Assert.assertEquals
import org.junit.Test

class DesignReferenceContentTest {
    @Test
    fun homeReferenceContentMatchesTheDesignMock() {
        val records = DesignReferenceContent.homeRecentRecords(nowEpochMillis = 1_800_000L)

        assertEquals(28.0, DesignReferenceContent.homeSugarGrams, 0.01)
        assertEquals(1, DesignReferenceContent.homeAlcoholOccasions)
        assertEquals(listOf(ItemType.COLA, ItemType.WINE), records.map { it.itemType })
        assertEquals(33.0, records.first().metrics.sugarGrams, 0.01)
        assertEquals(1.7, records.last().metrics.alcoholGrams / 14.0, 0.05)
    }
}
