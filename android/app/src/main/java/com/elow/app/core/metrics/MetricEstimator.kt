package com.elow.app.core.metrics

import com.elow.app.core.catalog.ItemCatalog
import com.elow.app.core.model.ItemDefinition
import com.elow.app.core.model.ItemType
import com.elow.app.core.model.MetricsEstimate

object MetricEstimator {
    fun estimate(itemType: ItemType, amountFraction: Double): MetricsEstimate {
        val fraction = amountFraction.coerceIn(0.0, 1.0)
        val definition = ItemCatalog.definitionFor(itemType)
        return estimate(definition, fraction)
    }

    fun estimate(definition: ItemDefinition, amountFraction: Double): MetricsEstimate {
        val fraction = amountFraction.coerceIn(0.0, 1.0)
        return MetricsEstimate(
            sugarGrams = definition.fullServingSugarGrams * fraction,
            alcoholGrams = definition.fullServingAlcoholGrams * fraction,
            calories = definition.fullServingCalories * fraction,
            money = definition.fullServingMoney * fraction
        )
    }
}
