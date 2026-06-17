package com.elow.app.data

import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemDefinition
import kotlinx.coroutines.flow.Flow

interface ElowRepository {
    val catalog: Flow<List<ItemDefinition>>
    val records: Flow<List<IntakeRecord>>
    val goals: Flow<GoalSettings>
    val onboardingComplete: Flow<Boolean>

    suspend fun refresh()
    suspend fun addRecord(record: IntakeRecord)
    suspend fun updateGoals(settings: GoalSettings)
    suspend fun setOnboardingComplete(complete: Boolean)
}
