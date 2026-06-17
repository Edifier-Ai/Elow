package com.elow.app.data

import com.elow.app.core.catalog.ItemCatalog
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemDefinition
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RemoteElowRepository(
    private val baseUrl: String,
    private val userId: String
) : ElowRepository {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val catalogState = MutableStateFlow(ItemCatalog.firstVersionItems)
    private val recordsState = MutableStateFlow<List<IntakeRecord>>(emptyList())
    private val goalsState = MutableStateFlow(GoalSettings())
    private val onboardingState = MutableStateFlow(false)

    override val catalog: Flow<List<ItemDefinition>> = catalogState.asStateFlow()
    override val records: Flow<List<IntakeRecord>> = recordsState.asStateFlow()
    override val goals: Flow<GoalSettings> = goalsState.asStateFlow()
    override val onboardingComplete: Flow<Boolean> = onboardingState.asStateFlow()

    override suspend fun refresh() {
        val catalog = get<CatalogResponse>("/catalog").items
        val profile = get<ProfileResponse>("/users/$userId/profile")
        val records = get<RecordsResponse>("/users/$userId/records").records

        catalogState.value = ItemCatalog.visibleItems(catalog)
        goalsState.value = profile.goals
        onboardingState.value = profile.onboardingComplete
        recordsState.value = records
    }

    override suspend fun addRecord(record: IntakeRecord) {
        recordsState.value = recordsState.value + record
        runCatching {
            post<RecordCreateRequest, IntakeRecord>(
                path = "/users/$userId/records",
                body = RecordCreateRequest(
                    id = record.id,
                    itemType = record.itemType.name,
                    amountFraction = record.amountFraction,
                    timestampEpochMillis = record.timestampEpochMillis,
                    note = record.note
                )
            )
            refresh()
        }
    }

    override suspend fun updateGoals(settings: GoalSettings) {
        goalsState.value = settings
        runCatching {
            put<ProfileUpdateRequest, ProfileResponse>(
                path = "/users/$userId/profile",
                body = ProfileUpdateRequest(
                    onboardingComplete = onboardingState.value,
                    goals = settings
                )
            )
            refresh()
        }
    }

    override suspend fun setOnboardingComplete(complete: Boolean) {
        onboardingState.value = complete
        runCatching {
            val profile = put<ProfileUpdateRequest, ProfileResponse>(
                path = "/users/$userId/profile",
                body = ProfileUpdateRequest(
                    onboardingComplete = complete,
                    goals = goalsState.value
                )
            )
            onboardingState.value = profile.onboardingComplete
            goalsState.value = profile.goals
            refresh()
        }
    }

    private suspend inline fun <reified T> get(path: String): T =
        request(path = path, method = "GET", body = null)

    private suspend inline fun <reified B, reified T> post(path: String, body: B): T =
        request(path = path, method = "POST", body = json.encodeToString(body))

    private suspend inline fun <reified B, reified T> put(path: String, body: B): T =
        request(path = path, method = "PUT", body = json.encodeToString(body))

    private suspend inline fun <reified T> request(path: String, method: String, body: String?): T =
        withContext(Dispatchers.IO) {
            val url = URL(baseUrl.trimEnd('/') + path)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = method
                connectTimeout = 2500
                readTimeout = 3500
                setRequestProperty("Accept", "application/json")
                if (body != null) {
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                }
            }

            try {
                if (body != null) {
                    connection.outputStream.use { output ->
                        output.write(body.toByteArray(Charsets.UTF_8))
                    }
                }
                val code = connection.responseCode
                val stream = if (code in 200..299) connection.inputStream else connection.errorStream
                val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
                if (code !in 200..299) {
                    throw IOException("Backend request failed $code: $text")
                }
                json.decodeFromString<T>(text)
            } finally {
                connection.disconnect()
            }
        }
}

@Serializable
private data class CatalogResponse(
    val items: List<ItemDefinition>
)

@Serializable
private data class ProfileResponse(
    val userId: String,
    val onboardingComplete: Boolean,
    val goals: GoalSettings
)

@Serializable
private data class RecordsResponse(
    val records: List<IntakeRecord>
)

@Serializable
private data class RecordCreateRequest(
    val id: String,
    val itemType: String,
    val amountFraction: Double,
    val timestampEpochMillis: Long,
    val note: String
)

@Serializable
private data class ProfileUpdateRequest(
    val onboardingComplete: Boolean,
    val goals: GoalSettings
)
