package com.elow.app.state

import com.elow.app.core.model.AppLanguage
import com.elow.app.core.model.ItemType
import com.elow.app.data.FakeElowRepository
import com.elow.app.data.InMemoryLanguagePreferenceStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ElowViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun saveCurrentAddRecordStoresRecordAndReturnsHome() = runTest {
        val repository = FakeElowRepository()
        val viewModel = ElowViewModel(repository)

        viewModel.openAdd()
        viewModel.selectAddItem(ItemType.COLA)
        viewModel.updateAddFraction(0.5)
        viewModel.saveCurrentRecord(nowEpochMillis = 1_777_000_000_000L)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(MainTab.HOME, viewModel.uiState.value.selectedTab)
        assertEquals(1, viewModel.uiState.value.records.size)
        assertEquals(0.5, viewModel.uiState.value.records.first().amountFraction, 0.01)
    }

    @Test
    fun updateLanguagePublishesLanguageState() = runTest {
        val repository = FakeElowRepository()
        val languageStore = InMemoryLanguagePreferenceStore()
        val viewModel = ElowViewModel(repository, languageStore)

        viewModel.updateLanguage(AppLanguage.CHINESE)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(AppLanguage.CHINESE, viewModel.uiState.value.language)
    }
}
