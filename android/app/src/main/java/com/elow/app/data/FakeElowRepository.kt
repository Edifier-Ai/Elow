package com.elow.app.data

import com.elow.app.core.catalog.ItemCatalog
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemDefinition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeElowRepository : ElowRepository {
    private val catalogState = MutableStateFlow(ItemCatalog.firstVersionItems)
    private val recordsState = MutableStateFlow<List<IntakeRecord>>(emptyList())
    private val goalsState = MutableStateFlow(GoalSettings())
    private val onboardingState = MutableStateFlow(false)

    override val catalog: Flow<List<ItemDefinition>> = catalogState.asStateFlow()
    override val records: Flow<List<IntakeRecord>> = recordsState.asStateFlow()
    override val goals: Flow<GoalSettings> = goalsState.asStateFlow()
    override val onboardingComplete: Flow<Boolean> = onboardingState.asStateFlow()

    override suspend fun refresh() = Unit

    override suspend fun addRecord(record: IntakeRecord) {
        recordsState.value = recordsState.value + record
    }

    override suspend fun updateGoals(settings: GoalSettings) {
        goalsState.value = settings
    }

    override suspend fun setOnboardingComplete(complete: Boolean) {
        onboardingState.value = complete
    }
}
