package com.elow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.elow.app.data.RemoteElowRepository
import com.elow.app.data.SharedPreferencesLanguagePreferenceStore
import com.elow.app.data.UserIdentityStore
import com.elow.app.state.ElowViewModel
import com.elow.app.ui.ElowApp
import com.elow.app.ui.theme.ElowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = RemoteElowRepository(
            baseUrl = "http://10.0.2.2:8080/api",
            userId = UserIdentityStore(applicationContext).userId()
        )
        val viewModel = ElowViewModel(
            repository = repository,
            languageStore = SharedPreferencesLanguagePreferenceStore(applicationContext)
        )

        setContent {
            ElowTheme {
                val state by viewModel.uiState.collectAsState()
                ElowApp(state = state, viewModel = viewModel)
            }
        }
    }
}
