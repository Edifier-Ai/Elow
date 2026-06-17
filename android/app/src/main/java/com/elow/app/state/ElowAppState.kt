package com.elow.app.state

import com.elow.app.core.model.AppLanguage
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.GoalStatus
import com.elow.app.core.model.HonorReward
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemDefinition
import com.elow.app.core.model.ItemType

enum class MainTab {
    HOME,
    ADD,
    ME
}

data class AddState(
    val selectedItem: ItemType = ItemType.COLA,
    val amountFraction: Double = 0.66,
    val note: String = ""
)

data class ElowUiState(
    val selectedTab: MainTab = MainTab.HOME,
    val onboardingComplete: Boolean = false,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val catalog: List<ItemDefinition> = emptyList(),
    val records: List<IntakeRecord> = emptyList(),
    val goals: GoalSettings = GoalSettings(),
    val goalStatus: GoalStatus? = null,
    val honorRewards: List<HonorReward> = emptyList(),
    val addState: AddState = AddState()
)
