package com.elow.app.data

import com.elow.app.core.catalog.ItemCatalog
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemDefinition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first

class LocalElowRepository(private val dataStore: ElowDataStore) : ElowRepository {
    override val catalog: Flow<List<ItemDefinition>> = flowOf(ItemCatalog.firstVersionItems)
    override val records: Flow<List<IntakeRecord>> = dataStore.records
    override val goals: Flow<GoalSettings> = dataStore.goals
    override val onboardingComplete: Flow<Boolean> = dataStore.onboardingComplete

    override suspend fun refresh() = Unit

    override suspend fun addRecord(record: IntakeRecord) {
        dataStore.saveRecords(dataStore.records.first() + record)
    }

    override suspend fun updateGoals(settings: GoalSettings) {
        dataStore.saveGoals(settings)
    }

    override suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.saveOnboardingComplete(complete)
    }
}
