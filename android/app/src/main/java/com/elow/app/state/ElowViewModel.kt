package com.elow.app.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elow.app.core.catalog.ItemCatalog
import com.elow.app.core.goals.GoalEvaluator
import com.elow.app.core.honor.HonorWallEngine
import com.elow.app.core.metrics.MetricEstimator
import com.elow.app.core.model.AppLanguage
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemType
import com.elow.app.data.ElowRepository
import com.elow.app.data.InMemoryLanguagePreferenceStore
import com.elow.app.data.LanguagePreferenceStore
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ElowViewModel(
    private val repository: ElowRepository,
    private val languageStore: LanguagePreferenceStore = InMemoryLanguagePreferenceStore()
) : ViewModel() {
    private val selectedTab = MutableStateFlow(MainTab.HOME)
    private val addState = MutableStateFlow(AddState())

    private val backendState = combine(
        repository.catalog,
        repository.records,
        repository.goals,
        repository.onboardingComplete,
        languageStore.language
    ) { catalog, records, goals, onboardingComplete, language ->
        BackendState(
            catalog = catalog,
            records = records,
            goals = goals,
            onboardingComplete = onboardingComplete,
            language = language
        )
    }

    val uiState: StateFlow<ElowUiState> = combine(
        backendState,
        selectedTab,
        addState
    ) { backend, tab, add ->
        val now = System.currentTimeMillis()
        val sortedRecords = backend.records.sortedByDescending { it.timestampEpochMillis }
        ElowUiState(
            selectedTab = tab,
            onboardingComplete = backend.onboardingComplete,
            language = backend.language,
            catalog = backend.catalog,
            records = sortedRecords,
            goals = backend.goals,
            goalStatus = GoalEvaluator.evaluateWeek(sortedRecords, now, backend.goals),
            honorRewards = HonorWallEngine.rewardsForWeek(sortedRecords, now, backend.goals),
            addState = add
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ElowUiState()
    )

    init {
        viewModelScope.launch {
            runCatching { repository.refresh() }
        }
    }

    fun selectTab(tab: MainTab) {
        selectedTab.value = tab
    }

    fun openAdd() {
        selectedTab.value = MainTab.ADD
    }

    fun closeAdd() {
        selectedTab.value = MainTab.HOME
    }

    fun selectAddItem(itemType: ItemType) {
        addState.value = addState.value.copy(
            selectedItem = itemType,
            amountFraction = itemType.defaultDisplayFraction()
        )
    }

    fun updateAddFraction(fraction: Double) {
        addState.value = addState.value.copy(amountFraction = fraction.coerceIn(0.0, 1.0))
    }

    fun updateNote(note: String) {
        addState.value = addState.value.copy(note = note.take(160))
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.setOnboardingComplete(true)
        }
    }

    fun updateGoals(settings: GoalSettings) {
        viewModelScope.launch {
            repository.updateGoals(settings)
        }
    }

    fun updateLanguage(language: AppLanguage) {
        viewModelScope.launch {
            languageStore.setLanguage(language)
        }
    }

    fun saveCurrentRecord(nowEpochMillis: Long = System.currentTimeMillis()) {
        val current = addState.value
        val definition = ItemCatalog.definitionFor(current.selectedItem, uiState.value.catalog)
        val recordFraction = current.selectedItem.recordFraction(current.amountFraction, definition)
        val record = IntakeRecord(
            id = UUID.randomUUID().toString(),
            itemType = current.selectedItem,
            amountFraction = recordFraction,
            timestampEpochMillis = nowEpochMillis,
            metrics = MetricEstimator.estimate(definition, recordFraction),
            note = current.note
        )
        viewModelScope.launch {
            runCatching {
                repository.addRecord(record)
                addState.value = current.copy(note = "")
                selectedTab.value = MainTab.HOME
            }
        }
    }
}

private fun ItemType.defaultDisplayFraction(): Double =
    when (this) {
        ItemType.WINE -> 0.60
        else -> 0.66
    }

private fun ItemType.recordFraction(displayFraction: Double, definition: com.elow.app.core.model.ItemDefinition): Double {
    val displayMaxMl = if (this == ItemType.WINE) 250.0 else definition.fullServingMl.toDouble()
    return (displayMaxMl * displayFraction.coerceAtLeast(0.0)) / definition.fullServingMl.toDouble()
}

private data class BackendState(
    val catalog: List<com.elow.app.core.model.ItemDefinition>,
    val records: List<IntakeRecord>,
    val goals: GoalSettings,
    val onboardingComplete: Boolean,
    val language: AppLanguage
)
