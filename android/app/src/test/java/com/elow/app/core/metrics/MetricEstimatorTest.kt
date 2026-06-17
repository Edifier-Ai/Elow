package com.elow.app.core.metrics

import com.elow.app.core.model.ItemType
import org.junit.Assert.assertEquals
import org.junit.Test

class MetricEstimatorTest {
    @Test
    fun halfColaEstimatesHalfTheFullServing() {
        val estimate = MetricEstimator.estimate(ItemType.COLA, 0.5)

        assertEquals(25.0, estimate.sugarGrams, 0.01)
        assertEquals(0.0, estimate.alcoholGrams, 0.01)
        assertEquals(70.0, estimate.calories, 0.01)
        assertEquals(0.75, estimate.money, 0.01)
    }

    @Test
    fun designReferenceColaAmountShowsThirtyThreeSugarGrams() {
        val estimate = MetricEstimator.estimate(ItemType.COLA, 0.66)

        assertEquals(33.0, estimate.sugarGrams, 0.01)
    }

    @Test
    fun designReferenceWineGlassShowsOnePointSevenDrinks() {
        val estimate = MetricEstimator.estimate(ItemType.WINE, 1.0)

        assertEquals(1.7, estimate.alcoholGrams / 14.0, 0.05)
    }

    @Test
    fun amountFractionIsClamped() {
        val low = MetricEstimator.estimate(ItemType.BEER, -1.0)
        val high = MetricEstimator.estimate(ItemType.BEER, 2.0)

        assertEquals(0.0, low.alcoholGrams, 0.01)
        assertEquals(14.0, high.alcoholGrams, 0.01)
    }
}
