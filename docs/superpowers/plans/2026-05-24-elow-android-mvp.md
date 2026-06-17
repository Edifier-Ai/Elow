# Elow Android MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the first native Android version of Elow: a three-entry toy-collectible intake recorder with full-screen object interaction, local records, goals, and honor wall progress.

**Architecture:** Use native Android with Kotlin and Jetpack Compose. Keep domain logic in pure Kotlin files with unit tests so future iOS can copy the same contracts and calculations while building native Swift UI separately. Use Compose Canvas, pointer input, and animation APIs for the highest-quality Android interaction.

**Tech Stack:** Android Kotlin, Jetpack Compose, Compose Canvas, Kotlin coroutines/Flow, AndroidX DataStore Preferences, kotlinx.serialization JSON, JUnit, Compose UI tests.

---

## Source Decisions

Use native Android rather than React Native or Compose Multiplatform for this first version. Elow's core value is interaction quality: full-screen objects, drag-based liquid levels, haptic thresholds, spring settling, animated object switching, and a collectible honor wall. Native Compose gives direct control over Canvas drawing, gestures, and animation timing.

Use these current references when implementing:

- Android is now "Compose First": https://android-developers.googleblog.com/2026/05/android-ui-development-is-compose-first.html
- Android Gradle Plugin 9.2.0 compatibility: https://developer.android.com/build/releases/gradle-plugin
- Jetpack Compose April 2026 release and Compose BOM `2026.04.01`: https://android-developers.googleblog.com/2026/04/jetpack-compose-april-2026-updates.html
- Compose animation API selection: https://developer.android.com/develop/ui/compose/animation/choose-api
- Kotlin current stable update instructions for `2.3.21`: https://kotlinlang.org/docs/releases.html
- DataStore stable release `1.2.1`: https://developer.android.com/jetpack/androidx/releases/datastore

If Android Studio's current Empty Activity template rejects a pinned build version during sync, keep the architecture and use the closest stable Android Studio-generated compatible version. Record the final versions in `android/gradle/libs.versions.toml`.

## File Structure

Create the Android app under `/Users/summer/Desktop/ELOW/android`.

Top-level files:

- `android/settings.gradle.kts`: Gradle plugin management and app module inclusion.
- `android/build.gradle.kts`: Root plugin aliases.
- `android/gradle/libs.versions.toml`: Version catalog.
- `android/.gitignore`: Android build artifacts and local files.
- `android/app/build.gradle.kts`: App module build config.
- `android/app/src/main/AndroidManifest.xml`: Single-activity manifest.

Domain files:

- `android/app/src/main/java/com/elow/app/core/model/ElowModels.kt`: Records, items, goals, metrics, and honor rewards.
- `android/app/src/main/java/com/elow/app/core/catalog/ItemCatalog.kt`: First-version item definitions and base serving estimates.
- `android/app/src/main/java/com/elow/app/core/metrics/MetricEstimator.kt`: Fraction-to-metrics estimates.
- `android/app/src/main/java/com/elow/app/core/goals/GoalEvaluator.kt`: Today's target status.
- `android/app/src/main/java/com/elow/app/core/honor/HonorWallEngine.kt`: Honor wall progress and reward decisions.
- `android/app/src/test/java/com/elow/app/core/metrics/MetricEstimatorTest.kt`
- `android/app/src/test/java/com/elow/app/core/goals/GoalEvaluatorTest.kt`
- `android/app/src/test/java/com/elow/app/core/honor/HonorWallEngineTest.kt`

Data files:

- `android/app/src/main/java/com/elow/app/data/ElowRepository.kt`: Repository interface.
- `android/app/src/main/java/com/elow/app/data/FakeElowRepository.kt`: In-memory repository for previews and tests.
- `android/app/src/main/java/com/elow/app/data/LocalElowRepository.kt`: DataStore-backed repository.
- `android/app/src/main/java/com/elow/app/data/ElowDataStore.kt`: DataStore keys and JSON adapters.
- `android/app/src/test/java/com/elow/app/data/FakeElowRepositoryTest.kt`

App state files:

- `android/app/src/main/java/com/elow/app/state/ElowAppState.kt`: UI state models.
- `android/app/src/main/java/com/elow/app/state/ElowViewModel.kt`: Main state holder and event reducer.
- `android/app/src/test/java/com/elow/app/state/ElowViewModelTest.kt`

UI files:

- `android/app/src/main/java/com/elow/app/MainActivity.kt`
- `android/app/src/main/java/com/elow/app/ui/ElowApp.kt`: App shell and bottom navigation.
- `android/app/src/main/java/com/elow/app/ui/theme/ElowTheme.kt`
- `android/app/src/main/java/com/elow/app/ui/theme/ElowTokens.kt`
- `android/app/src/main/java/com/elow/app/ui/home/HomeScreen.kt`
- `android/app/src/main/java/com/elow/app/ui/add/AddScreen.kt`
- `android/app/src/main/java/com/elow/app/ui/add/ObjectCarousel.kt`
- `android/app/src/main/java/com/elow/app/ui/add/ObjectRecorderStage.kt`
- `android/app/src/main/java/com/elow/app/ui/add/ToyObjects.kt`
- `android/app/src/main/java/com/elow/app/ui/me/MeScreen.kt`
- `android/app/src/main/java/com/elow/app/ui/me/HonorWall.kt`
- `android/app/src/main/java/com/elow/app/ui/onboarding/OnboardingScreen.kt`
- `android/app/src/androidTest/java/com/elow/app/ElowSmokeTest.kt`

Shared contract docs:

- `docs/contracts/elow-data-contract.md`: Cross-platform data and calculation contract for future iOS.

## Task 0: Initialize Project Guardrails

**Files:**
- Create: `android/.gitignore`
- Modify: repository git state if the user approves during execution

- [ ] **Step 1: Confirm repository state**

Run:

```bash
cd /Users/summer/Desktop/ELOW
git rev-parse --is-inside-work-tree
```

Expected if unchanged from planning: command exits non-zero with `fatal: not a git repository`.

- [ ] **Step 2: Initialize git only if this folder is still not a repository**

Run:

```bash
cd /Users/summer/Desktop/ELOW
git init
git add AGENTS.md docs/superpowers/specs/2026-05-24-elow-product-design.md docs/superpowers/plans/2026-05-24-elow-android-mvp.md
git commit -m "docs: define Elow Android MVP"
```

Expected: a first commit containing the existing docs.

- [ ] **Step 3: Create Android ignore rules**

Create `android/.gitignore`:

```gitignore
.gradle/
build/
local.properties
*.iml
.idea/
captures/
.cxx/
app/build/
```

- [ ] **Step 4: Commit guardrails**

Run:

```bash
cd /Users/summer/Desktop/ELOW
git add android/.gitignore
git commit -m "chore: add Android project ignore rules"
```

Expected: commit succeeds.

## Task 1: Scaffold Native Android Compose App

**Files:**
- Create: `android/settings.gradle.kts`
- Create: `android/build.gradle.kts`
- Create: `android/gradle/libs.versions.toml`
- Create: `android/app/build.gradle.kts`
- Create: `android/app/src/main/AndroidManifest.xml`
- Create: `android/app/src/main/res/values/styles.xml`
- Create: `android/app/src/main/java/com/elow/app/MainActivity.kt`

- [ ] **Step 1: Create the Gradle version catalog**

Create `android/gradle/libs.versions.toml`:

```toml
[versions]
agp = "9.2.0"
kotlin = "2.3.21"
composeBom = "2026.04.01"
activityCompose = "1.13.0"
lifecycle = "2.10.0"
datastore = "1.2.1"
serialization = "1.9.0"
coroutines = "1.11.0"
junit = "4.13.2"
androidxTestExt = "1.3.0"
espresso = "3.7.0"

[libraries]
androidx-activity-compose = { module = "androidx.activity:activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { module = "androidx.compose:compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { module = "androidx.compose.ui:ui" }
androidx-compose-ui-tooling = { module = "androidx.compose.ui:ui-tooling" }
androidx-compose-ui-tooling-preview = { module = "androidx.compose.ui:ui-tooling-preview" }
androidx-compose-ui-test-junit4 = { module = "androidx.compose.ui:ui-test-junit4" }
androidx-compose-ui-test-manifest = { module = "androidx.compose.ui:ui-test-manifest" }
androidx-compose-material3 = { module = "androidx.compose.material3:material3" }
androidx-datastore-preferences = { module = "androidx.datastore:datastore-preferences", version.ref = "datastore" }
androidx-lifecycle-viewmodel-compose = { module = "androidx.lifecycle:lifecycle-viewmodel-compose", version.ref = "lifecycle" }
kotlinx-coroutines-android = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-android", version.ref = "coroutines" }
kotlinx-coroutines-test = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-test", version.ref = "coroutines" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "serialization" }
junit = { module = "junit:junit", version.ref = "junit" }
androidx-test-ext-junit = { module = "androidx.test.ext:junit", version.ref = "androidxTestExt" }
espresso-core = { module = "androidx.test.espresso:espresso-core", version.ref = "espresso" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

- [ ] **Step 2: Create root Gradle files**

Create `android/settings.gradle.kts`:

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Elow"
include(":app")
```

Create `android/build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
```

- [ ] **Step 3: Create app module build file**

Create `android/app/build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.elow.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.elow.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
```

- [ ] **Step 4: Create manifest and empty activity**

Create `android/app/src/main/AndroidManifest.xml`:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application
        android:allowBackup="true"
        android:label="Elow"
        android:supportsRtl="true"
        android:theme="@style/Theme.Elow">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

Create `android/app/src/main/res/values/styles.xml`:

```xml
<resources>
    <style name="Theme.Elow" parent="android:style/Theme.Material.Light.NoActionBar">
        <item name="android:windowNoTitle">true</item>
        <item name="android:windowActionBar">false</item>
        <item name="android:windowLightStatusBar">true</item>
        <item name="android:statusBarColor">#FFFCF4</item>
        <item name="android:navigationBarColor">#FFFFFF</item>
    </style>
</resources>
```

Create `android/app/src/main/java/com/elow/app/MainActivity.kt`:

```kotlin
package com.elow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text("Elow")
        }
    }
}
```

- [ ] **Step 5: Run Gradle sync/build**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:assembleDebug
```

Expected: `BUILD SUCCESSFUL`. If `./gradlew` does not exist, create the project once with Android Studio's Empty Activity template in `/Users/summer/Desktop/ELOW/android`, then reapply the files above and rerun.

- [ ] **Step 6: Commit scaffold**

Run:

```bash
cd /Users/summer/Desktop/ELOW
git add android
git commit -m "feat(android): scaffold native Compose app"
```

## Task 2: Implement Pure Kotlin Domain Model

**Files:**
- Create: `android/app/src/main/java/com/elow/app/core/model/ElowModels.kt`
- Create: `android/app/src/main/java/com/elow/app/core/catalog/ItemCatalog.kt`
- Create: `android/app/src/test/java/com/elow/app/core/catalog/ItemCatalogTest.kt`

- [ ] **Step 1: Write the catalog test first**

Create `android/app/src/test/java/com/elow/app/core/catalog/ItemCatalogTest.kt`:

```kotlin
package com.elow.app.core.catalog

import com.elow.app.core.model.ItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ItemCatalogTest {
    @Test
    fun firstVersionContainsExactlyFourItems() {
        assertEquals(
            listOf(ItemType.COLA, ItemType.MILK_TEA, ItemType.BEER, ItemType.WINE),
            ItemCatalog.firstVersionItems.map { it.type }
        )
    }

    @Test
    fun sugarItemsHaveSugarAndAlcoholItemsHaveAlcohol() {
        val cola = ItemCatalog.definitionFor(ItemType.COLA)
        val milkTea = ItemCatalog.definitionFor(ItemType.MILK_TEA)
        val beer = ItemCatalog.definitionFor(ItemType.BEER)
        val wine = ItemCatalog.definitionFor(ItemType.WINE)

        assertTrue(cola.fullServingSugarGrams > 0.0)
        assertTrue(milkTea.fullServingSugarGrams > 0.0)
        assertEquals(0.0, cola.fullServingAlcoholGrams, 0.0)
        assertEquals(0.0, milkTea.fullServingAlcoholGrams, 0.0)
        assertTrue(beer.fullServingAlcoholGrams > 0.0)
        assertTrue(wine.fullServingAlcoholGrams > 0.0)
    }
}
```

- [ ] **Step 2: Run the failing catalog test**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:testDebugUnitTest --tests com.elow.app.core.catalog.ItemCatalogTest
```

Expected: fail because `ItemCatalog` and models do not exist.

- [ ] **Step 3: Implement domain models and item catalog**

Create `android/app/src/main/java/com/elow/app/core/model/ElowModels.kt`:

```kotlin
package com.elow.app.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class ItemType {
    COLA,
    MILK_TEA,
    BEER,
    WINE
}

@Serializable
enum class IntakeFamily {
    SUGAR,
    ALCOHOL
}

@Serializable
data class IntakeRecord(
    val id: String,
    val itemType: ItemType,
    val amountFraction: Double,
    val timestampEpochMillis: Long,
    val metrics: MetricsEstimate,
    val note: String = ""
)

@Serializable
data class MetricsEstimate(
    val sugarGrams: Double,
    val alcoholGrams: Double,
    val calories: Double,
    val money: Double
)

@Serializable
data class ItemDefinition(
    val type: ItemType,
    val displayName: String,
    val family: IntakeFamily,
    val fullServingLabel: String,
    val fullServingSugarGrams: Double,
    val fullServingAlcoholGrams: Double,
    val fullServingCalories: Double,
    val fullServingMoney: Double
)

@Serializable
data class GoalSettings(
    val weeklySweetDrinkLimit: Int = 7,
    val weeklyAlcoholOccasionLimit: Int = 2
)

@Serializable
data class GoalStatus(
    val sweetDrinksThisWeek: Int,
    val alcoholOccasionsThisWeek: Int,
    val sweetDrinkLimit: Int,
    val alcoholOccasionLimit: Int
)

@Serializable
data class HonorReward(
    val id: String,
    val title: String,
    val description: String,
    val itemType: ItemType?,
    val tier: Int
)
```

Create `android/app/src/main/java/com/elow/app/core/catalog/ItemCatalog.kt`:

```kotlin
package com.elow.app.core.catalog

import com.elow.app.core.model.IntakeFamily
import com.elow.app.core.model.ItemDefinition
import com.elow.app.core.model.ItemType

object ItemCatalog {
    val firstVersionItems: List<ItemDefinition> = listOf(
        ItemDefinition(
            type = ItemType.COLA,
            displayName = "Cola",
            family = IntakeFamily.SUGAR,
            fullServingLabel = "1 bottle",
            fullServingSugarGrams = 35.0,
            fullServingAlcoholGrams = 0.0,
            fullServingCalories = 140.0,
            fullServingMoney = 1.50
        ),
        ItemDefinition(
            type = ItemType.MILK_TEA,
            displayName = "Milk tea",
            family = IntakeFamily.SUGAR,
            fullServingLabel = "1 cup",
            fullServingSugarGrams = 45.0,
            fullServingAlcoholGrams = 0.0,
            fullServingCalories = 280.0,
            fullServingMoney = 5.50
        ),
        ItemDefinition(
            type = ItemType.BEER,
            displayName = "Beer",
            family = IntakeFamily.ALCOHOL,
            fullServingLabel = "1 can",
            fullServingSugarGrams = 0.0,
            fullServingAlcoholGrams = 14.0,
            fullServingCalories = 153.0,
            fullServingMoney = 4.00
        ),
        ItemDefinition(
            type = ItemType.WINE,
            displayName = "Wine",
            family = IntakeFamily.ALCOHOL,
            fullServingLabel = "1 glass",
            fullServingSugarGrams = 0.0,
            fullServingAlcoholGrams = 14.0,
            fullServingCalories = 125.0,
            fullServingMoney = 6.00
        )
    )

    fun definitionFor(type: ItemType): ItemDefinition =
        firstVersionItems.first { it.type == type }
}
```

- [ ] **Step 4: Run catalog test**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:testDebugUnitTest --tests com.elow.app.core.catalog.ItemCatalogTest
```

Expected: pass.

- [ ] **Step 5: Commit domain model**

Run:

```bash
cd /Users/summer/Desktop/ELOW
git add android/app/src/main/java/com/elow/app/core android/app/src/test/java/com/elow/app/core
git commit -m "feat(android): add Elow domain model"
```

## Task 3: Implement Metrics, Goals, And Honor Rules

**Files:**
- Create: `android/app/src/main/java/com/elow/app/core/metrics/MetricEstimator.kt`
- Create: `android/app/src/main/java/com/elow/app/core/goals/GoalEvaluator.kt`
- Create: `android/app/src/main/java/com/elow/app/core/honor/HonorWallEngine.kt`
- Create: `android/app/src/test/java/com/elow/app/core/metrics/MetricEstimatorTest.kt`
- Create: `android/app/src/test/java/com/elow/app/core/goals/GoalEvaluatorTest.kt`
- Create: `android/app/src/test/java/com/elow/app/core/honor/HonorWallEngineTest.kt`

- [ ] **Step 1: Write failing tests**

Create `android/app/src/test/java/com/elow/app/core/metrics/MetricEstimatorTest.kt`:

```kotlin
package com.elow.app.core.metrics

import com.elow.app.core.model.ItemType
import org.junit.Assert.assertEquals
import org.junit.Test

class MetricEstimatorTest {
    @Test
    fun halfColaEstimatesHalfTheFullServing() {
        val estimate = MetricEstimator.estimate(ItemType.COLA, 0.5)

        assertEquals(17.5, estimate.sugarGrams, 0.01)
        assertEquals(0.0, estimate.alcoholGrams, 0.01)
        assertEquals(70.0, estimate.calories, 0.01)
        assertEquals(0.75, estimate.money, 0.01)
    }

    @Test
    fun amountFractionIsClamped() {
        val low = MetricEstimator.estimate(ItemType.BEER, -1.0)
        val high = MetricEstimator.estimate(ItemType.BEER, 2.0)

        assertEquals(0.0, low.alcoholGrams, 0.01)
        assertEquals(14.0, high.alcoholGrams, 0.01)
    }
}
```

Create `android/app/src/test/java/com/elow/app/core/goals/GoalEvaluatorTest.kt`:

```kotlin
package com.elow.app.core.goals

import com.elow.app.core.metrics.MetricEstimator
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemType
import org.junit.Assert.assertEquals
import org.junit.Test

class GoalEvaluatorTest {
    @Test
    fun countsSweetDrinksAndAlcoholOccasionsForCurrentWeek() {
        val now = 1_777_000_000_000L
        val records = listOf(
            record("1", ItemType.COLA, now),
            record("2", ItemType.MILK_TEA, now),
            record("3", ItemType.BEER, now),
            record("old", ItemType.WINE, now - 10L * 24L * 60L * 60L * 1000L)
        )

        val status = GoalEvaluator.evaluateWeek(
            records = records,
            nowEpochMillis = now,
            settings = GoalSettings(weeklySweetDrinkLimit = 4, weeklyAlcoholOccasionLimit = 2)
        )

        assertEquals(2, status.sweetDrinksThisWeek)
        assertEquals(1, status.alcoholOccasionsThisWeek)
        assertEquals(4, status.sweetDrinkLimit)
        assertEquals(2, status.alcoholOccasionLimit)
    }

    private fun record(id: String, type: ItemType, time: Long): IntakeRecord =
        IntakeRecord(
            id = id,
            itemType = type,
            amountFraction = 1.0,
            timestampEpochMillis = time,
            metrics = MetricEstimator.estimate(type, 1.0)
        )
}
```

Create `android/app/src/test/java/com/elow/app/core/honor/HonorWallEngineTest.kt`:

```kotlin
package com.elow.app.core.honor

import com.elow.app.core.metrics.MetricEstimator
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HonorWallEngineTest {
    @Test
    fun awardsSweetDrinkShelfWhenUserStaysBelowWeeklyGoal() {
        val now = 1_777_000_000_000L
        val records = listOf(record("1", ItemType.COLA, now))

        val rewards = HonorWallEngine.rewardsForWeek(
            records = records,
            nowEpochMillis = now,
            settings = GoalSettings(weeklySweetDrinkLimit = 3, weeklyAlcoholOccasionLimit = 2)
        )

        assertTrue(rewards.any { it.id == "sweet-drink-shelf-1" })
    }

    @Test
    fun doesNotAwardAlcoholShelfWhenAlcoholGoalIsExceeded() {
        val now = 1_777_000_000_000L
        val records = listOf(
            record("1", ItemType.BEER, now),
            record("2", ItemType.WINE, now)
        )

        val rewards = HonorWallEngine.rewardsForWeek(
            records = records,
            nowEpochMillis = now,
            settings = GoalSettings(weeklySweetDrinkLimit = 3, weeklyAlcoholOccasionLimit = 1)
        )

        assertEquals(false, rewards.any { it.id == "alcohol-shelf-1" })
    }

    private fun record(id: String, type: ItemType, time: Long): IntakeRecord =
        IntakeRecord(
            id = id,
            itemType = type,
            amountFraction = 1.0,
            timestampEpochMillis = time,
            metrics = MetricEstimator.estimate(type, 1.0)
        )
}
```

- [ ] **Step 2: Run tests and verify they fail**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:testDebugUnitTest --tests "com.elow.app.core.*"
```

Expected: fail because the rule classes do not exist.

- [ ] **Step 3: Implement rule classes**

Create `android/app/src/main/java/com/elow/app/core/metrics/MetricEstimator.kt`:

```kotlin
package com.elow.app.core.metrics

import com.elow.app.core.catalog.ItemCatalog
import com.elow.app.core.model.ItemType
import com.elow.app.core.model.MetricsEstimate

object MetricEstimator {
    fun estimate(itemType: ItemType, amountFraction: Double): MetricsEstimate {
        val fraction = amountFraction.coerceIn(0.0, 1.0)
        val definition = ItemCatalog.definitionFor(itemType)
        return MetricsEstimate(
            sugarGrams = definition.fullServingSugarGrams * fraction,
            alcoholGrams = definition.fullServingAlcoholGrams * fraction,
            calories = definition.fullServingCalories * fraction,
            money = definition.fullServingMoney * fraction
        )
    }
}
```

Create `android/app/src/main/java/com/elow/app/core/goals/GoalEvaluator.kt`:

```kotlin
package com.elow.app.core.goals

import com.elow.app.core.catalog.ItemCatalog
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.GoalStatus
import com.elow.app.core.model.IntakeFamily
import com.elow.app.core.model.IntakeRecord
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object GoalEvaluator {
    fun evaluateWeek(
        records: List<IntakeRecord>,
        nowEpochMillis: Long,
        settings: GoalSettings,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): GoalStatus {
        val startOfWeek = Instant.ofEpochMilli(nowEpochMillis)
            .atZone(zoneId)
            .truncatedTo(ChronoUnit.DAYS)
            .minusDays(6)
            .toInstant()
            .toEpochMilli()

        val weekRecords = records.filter { it.timestampEpochMillis >= startOfWeek }
        val sweetDrinks = weekRecords.count {
            ItemCatalog.definitionFor(it.itemType).family == IntakeFamily.SUGAR
        }
        val alcoholOccasions = weekRecords.count {
            ItemCatalog.definitionFor(it.itemType).family == IntakeFamily.ALCOHOL
        }

        return GoalStatus(
            sweetDrinksThisWeek = sweetDrinks,
            alcoholOccasionsThisWeek = alcoholOccasions,
            sweetDrinkLimit = settings.weeklySweetDrinkLimit,
            alcoholOccasionLimit = settings.weeklyAlcoholOccasionLimit
        )
    }
}
```

Create `android/app/src/main/java/com/elow/app/core/honor/HonorWallEngine.kt`:

```kotlin
package com.elow.app.core.honor

import com.elow.app.core.goals.GoalEvaluator
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.HonorReward
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemType

object HonorWallEngine {
    fun rewardsForWeek(
        records: List<IntakeRecord>,
        nowEpochMillis: Long,
        settings: GoalSettings
    ): List<HonorReward> {
        val status = GoalEvaluator.evaluateWeek(records, nowEpochMillis, settings)
        val rewards = mutableListOf<HonorReward>()

        if (status.sweetDrinksThisWeek <= status.sweetDrinkLimit) {
            rewards += HonorReward(
                id = "sweet-drink-shelf-1",
                title = "Sweet shelf",
                description = "Stayed within your sweet drink target this week.",
                itemType = ItemType.COLA,
                tier = 1
            )
        }

        if (status.alcoholOccasionsThisWeek <= status.alcoholOccasionLimit) {
            rewards += HonorReward(
                id = "alcohol-shelf-1",
                title = "Clear shelf",
                description = "Stayed within your alcohol target this week.",
                itemType = ItemType.BEER,
                tier = 1
            )
        }

        return rewards
    }
}
```

- [ ] **Step 4: Run unit tests**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:testDebugUnitTest --tests "com.elow.app.core.*"
```

Expected: pass.

- [ ] **Step 5: Commit rules**

Run:

```bash
cd /Users/summer/Desktop/ELOW
git add android/app/src/main/java/com/elow/app/core android/app/src/test/java/com/elow/app/core
git commit -m "feat(android): calculate intake metrics and honor progress"
```

## Task 4: Implement Local Repository

**Files:**
- Create: `android/app/src/main/java/com/elow/app/data/ElowRepository.kt`
- Create: `android/app/src/main/java/com/elow/app/data/FakeElowRepository.kt`
- Create: `android/app/src/main/java/com/elow/app/data/ElowDataStore.kt`
- Create: `android/app/src/main/java/com/elow/app/data/LocalElowRepository.kt`
- Create: `android/app/src/test/java/com/elow/app/data/FakeElowRepositoryTest.kt`

- [ ] **Step 1: Write fake repository tests**

Create `android/app/src/test/java/com/elow/app/data/FakeElowRepositoryTest.kt`:

```kotlin
package com.elow.app.data

import com.elow.app.core.metrics.MetricEstimator
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FakeElowRepositoryTest {
    @Test
    fun addRecordPublishesRecords() = runTest {
        val repository = FakeElowRepository()
        val record = IntakeRecord(
            id = "record-1",
            itemType = ItemType.COLA,
            amountFraction = 0.5,
            timestampEpochMillis = 1_777_000_000_000L,
            metrics = MetricEstimator.estimate(ItemType.COLA, 0.5)
        )

        repository.addRecord(record)

        assertEquals(listOf(record), repository.records.first())
    }

    @Test
    fun updateGoalsPublishesSettings() = runTest {
        val repository = FakeElowRepository()
        val goals = GoalSettings(weeklySweetDrinkLimit = 4, weeklyAlcoholOccasionLimit = 1)

        repository.updateGoals(goals)

        assertEquals(goals, repository.goals.first())
    }
}
```

- [ ] **Step 2: Run failing repository tests**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:testDebugUnitTest --tests com.elow.app.data.FakeElowRepositoryTest
```

Expected: fail because repository classes do not exist.

- [ ] **Step 3: Implement repository interface and fake**

Create `android/app/src/main/java/com/elow/app/data/ElowRepository.kt`:

```kotlin
package com.elow.app.data

import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.IntakeRecord
import kotlinx.coroutines.flow.Flow

interface ElowRepository {
    val records: Flow<List<IntakeRecord>>
    val goals: Flow<GoalSettings>
    val onboardingComplete: Flow<Boolean>

    suspend fun addRecord(record: IntakeRecord)
    suspend fun updateGoals(settings: GoalSettings)
    suspend fun setOnboardingComplete(complete: Boolean)
}
```

Create `android/app/src/main/java/com/elow/app/data/FakeElowRepository.kt`:

```kotlin
package com.elow.app.data

import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.IntakeRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeElowRepository : ElowRepository {
    private val recordsState = MutableStateFlow<List<IntakeRecord>>(emptyList())
    private val goalsState = MutableStateFlow(GoalSettings())
    private val onboardingState = MutableStateFlow(false)

    override val records: Flow<List<IntakeRecord>> = recordsState.asStateFlow()
    override val goals: Flow<GoalSettings> = goalsState.asStateFlow()
    override val onboardingComplete: Flow<Boolean> = onboardingState.asStateFlow()

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
```

- [ ] **Step 4: Implement DataStore adapter**

Create `android/app/src/main/java/com/elow/app/data/ElowDataStore.kt`:

```kotlin
package com.elow.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.IntakeRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.elowPreferences by preferencesDataStore(name = "elow")

class ElowDataStore(private val context: Context) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    val records: Flow<List<IntakeRecord>> = context.elowPreferences.data.map { preferences ->
        preferences[Keys.records]?.let { json.decodeFromString<List<IntakeRecord>>(it) }.orEmpty()
    }

    val goals: Flow<GoalSettings> = context.elowPreferences.data.map { preferences ->
        preferences[Keys.goals]?.let { json.decodeFromString<GoalSettings>(it) } ?: GoalSettings()
    }

    val onboardingComplete: Flow<Boolean> = context.elowPreferences.data.map { preferences ->
        preferences[Keys.onboardingComplete] ?: false
    }

    suspend fun saveRecords(records: List<IntakeRecord>) {
        context.elowPreferences.edit { preferences ->
            preferences[Keys.records] = json.encodeToString(records)
        }
    }

    suspend fun saveGoals(settings: GoalSettings) {
        context.elowPreferences.edit { preferences ->
            preferences[Keys.goals] = json.encodeToString(settings)
        }
    }

    suspend fun saveOnboardingComplete(complete: Boolean) {
        context.elowPreferences.edit { preferences ->
            preferences[Keys.onboardingComplete] = complete
        }
    }

    private object Keys {
        val records = stringPreferencesKey("records_json")
        val goals = stringPreferencesKey("goals_json")
        val onboardingComplete = booleanPreferencesKey("onboarding_complete")
    }
}
```

Create `android/app/src/main/java/com/elow/app/data/LocalElowRepository.kt`:

```kotlin
package com.elow.app.data

import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.IntakeRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class LocalElowRepository(private val dataStore: ElowDataStore) : ElowRepository {
    override val records: Flow<List<IntakeRecord>> = dataStore.records
    override val goals: Flow<GoalSettings> = dataStore.goals
    override val onboardingComplete: Flow<Boolean> = dataStore.onboardingComplete

    override suspend fun addRecord(record: IntakeRecord) {
        val next = dataStore.records.first() + record
        dataStore.saveRecords(next)
    }

    override suspend fun updateGoals(settings: GoalSettings) {
        dataStore.saveGoals(settings)
    }

    override suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.saveOnboardingComplete(complete)
    }
}
```

- [ ] **Step 5: Run repository tests**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:testDebugUnitTest --tests com.elow.app.data.FakeElowRepositoryTest
```

Expected: pass.

- [ ] **Step 6: Commit repository**

Run:

```bash
cd /Users/summer/Desktop/ELOW
git add android/app/src/main/java/com/elow/app/data android/app/src/test/java/com/elow/app/data
git commit -m "feat(android): add local Elow repository"
```

## Task 5: Implement App State And Events

**Files:**
- Create: `android/app/src/main/java/com/elow/app/state/ElowAppState.kt`
- Create: `android/app/src/main/java/com/elow/app/state/ElowViewModel.kt`
- Create: `android/app/src/test/java/com/elow/app/state/ElowViewModelTest.kt`

- [ ] **Step 1: Write ViewModel behavior test**

Create `android/app/src/test/java/com/elow/app/state/ElowViewModelTest.kt`:

```kotlin
package com.elow.app.state

import com.elow.app.core.model.ItemType
import com.elow.app.data.FakeElowRepository
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
}
```

- [ ] **Step 2: Run failing ViewModel test**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:testDebugUnitTest --tests com.elow.app.state.ElowViewModelTest
```

Expected: fail because app state files do not exist.

- [ ] **Step 3: Implement app state**

Create `android/app/src/main/java/com/elow/app/state/ElowAppState.kt`:

```kotlin
package com.elow.app.state

import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.GoalStatus
import com.elow.app.core.model.HonorReward
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemType

enum class MainTab {
    HOME,
    ADD,
    ME
}

data class AddState(
    val selectedItem: ItemType = ItemType.COLA,
    val amountFraction: Double = 0.5
)

data class ElowUiState(
    val selectedTab: MainTab = MainTab.HOME,
    val onboardingComplete: Boolean = false,
    val records: List<IntakeRecord> = emptyList(),
    val goals: GoalSettings = GoalSettings(),
    val goalStatus: GoalStatus? = null,
    val honorRewards: List<HonorReward> = emptyList(),
    val addState: AddState = AddState()
)
```

Create `android/app/src/main/java/com/elow/app/state/ElowViewModel.kt`:

```kotlin
package com.elow.app.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elow.app.core.goals.GoalEvaluator
import com.elow.app.core.honor.HonorWallEngine
import com.elow.app.core.metrics.MetricEstimator
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemType
import com.elow.app.data.ElowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ElowViewModel(
    private val repository: ElowRepository
) : ViewModel() {
    private val selectedTab = MutableStateFlow(MainTab.HOME)
    private val addState = MutableStateFlow(AddState())

    val uiState: StateFlow<ElowUiState> = combine(
        repository.records,
        repository.goals,
        repository.onboardingComplete,
        selectedTab,
        addState
    ) { records, goals, onboardingComplete, tab, add ->
        val now = System.currentTimeMillis()
        ElowUiState(
            selectedTab = tab,
            onboardingComplete = onboardingComplete,
            records = records.sortedByDescending { it.timestampEpochMillis },
            goals = goals,
            goalStatus = GoalEvaluator.evaluateWeek(records, now, goals),
            honorRewards = HonorWallEngine.rewardsForWeek(records, now, goals),
            addState = add
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ElowUiState()
    )

    fun selectTab(tab: MainTab) {
        selectedTab.value = tab
    }

    fun openAdd() {
        selectedTab.value = MainTab.ADD
    }

    fun selectAddItem(itemType: ItemType) {
        addState.value = addState.value.copy(selectedItem = itemType)
    }

    fun updateAddFraction(fraction: Double) {
        addState.value = addState.value.copy(amountFraction = fraction.coerceIn(0.0, 1.0))
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.setOnboardingComplete(true)
        }
    }

    fun saveCurrentRecord(nowEpochMillis: Long = System.currentTimeMillis()) {
        val current = addState.value
        val record = IntakeRecord(
            id = UUID.randomUUID().toString(),
            itemType = current.selectedItem,
            amountFraction = current.amountFraction,
            timestampEpochMillis = nowEpochMillis,
            metrics = MetricEstimator.estimate(current.selectedItem, current.amountFraction)
        )
        viewModelScope.launch {
            repository.addRecord(record)
            selectedTab.value = MainTab.HOME
        }
    }
}
```

- [ ] **Step 4: Run ViewModel tests**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:testDebugUnitTest --tests com.elow.app.state.ElowViewModelTest
```

Expected: pass.

- [ ] **Step 5: Commit state**

Run:

```bash
cd /Users/summer/Desktop/ELOW
git add android/app/src/main/java/com/elow/app/state android/app/src/test/java/com/elow/app/state
git commit -m "feat(android): add Elow app state"
```

## Task 6: Build Theme And App Shell

**Files:**
- Modify: `android/app/src/main/java/com/elow/app/MainActivity.kt`
- Create: `android/app/src/main/java/com/elow/app/ui/ElowApp.kt`
- Create: `android/app/src/main/java/com/elow/app/ui/theme/ElowTheme.kt`
- Create: `android/app/src/main/java/com/elow/app/ui/theme/ElowTokens.kt`

- [ ] **Step 1: Add toy-collectible design tokens**

Create `android/app/src/main/java/com/elow/app/ui/theme/ElowTokens.kt`:

```kotlin
package com.elow.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object ElowColors {
    val Ink = Color(0xFF1F1F1F)
    val Paper = Color(0xFFFFFCF4)
    val PopGreen = Color(0xFFD7FF6B)
    val ColaRed = Color(0xFFFF4D42)
    val ToyPurple = Color(0xFF8B5CF6)
    val Mint = Color(0xFF00C2A8)
    val Butter = Color(0xFFFFF3A3)
}

object ElowDimens {
    val Stroke = 4.dp
    val CardRadius = 22.dp
    val NavHeight = 76.dp
}
```

Create `android/app/src/main/java/com/elow/app/ui/theme/ElowTheme.kt`:

```kotlin
package com.elow.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ElowColorScheme = lightColorScheme(
    primary = ElowColors.Ink,
    secondary = ElowColors.PopGreen,
    surface = ElowColors.Paper,
    background = ElowColors.Paper
)

@Composable
fun ElowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ElowColorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
```

- [ ] **Step 2: Create app shell**

Create `android/app/src/main/java/com/elow/app/ui/ElowApp.kt`:

```kotlin
package com.elow.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elow.app.state.ElowUiState
import com.elow.app.state.ElowViewModel
import com.elow.app.state.MainTab
import com.elow.app.ui.theme.ElowColors
import com.elow.app.ui.theme.ElowDimens

@Composable
fun ElowApp(
    state: ElowUiState,
    viewModel: ElowViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ElowColors.Paper)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Text(
                text = when (state.selectedTab) {
                    MainTab.HOME -> "Home"
                    MainTab.ADD -> "Add"
                    MainTab.ME -> "Me"
                },
                modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Black
            )
        }
        ElowBottomBar(
            selectedTab = state.selectedTab,
            onHome = { viewModel.selectTab(MainTab.HOME) },
            onAdd = { viewModel.openAdd() },
            onMe = { viewModel.selectTab(MainTab.ME) }
        )
    }
}

@Composable
private fun ElowBottomBar(
    selectedTab: MainTab,
    onHome: () -> Unit,
    onAdd: () -> Unit,
    onMe: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ElowDimens.NavHeight)
            .background(Color.White)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavText("Home", selectedTab == MainTab.HOME, onHome, Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(ElowColors.PopGreen)
                .clickable { onAdd() },
            contentAlignment = Alignment.Center
        ) {
            Text("+", fontWeight = FontWeight.Black)
        }
        NavText("Me", selectedTab == MainTab.ME, onMe, Modifier.weight(1f))
    }
}

@Composable
private fun NavText(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        modifier = modifier
            .clickable { onClick() }
            .padding(16.dp),
        color = if (selected) ElowColors.Ink else Color(0xFF777777),
        fontWeight = FontWeight.Black
    )
}
```

- [ ] **Step 3: Wire activity**

Replace `android/app/src/main/java/com/elow/app/MainActivity.kt`:

```kotlin
package com.elow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.elow.app.data.ElowDataStore
import com.elow.app.data.LocalElowRepository
import com.elow.app.state.ElowViewModel
import com.elow.app.ui.ElowApp
import com.elow.app.ui.theme.ElowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = LocalElowRepository(ElowDataStore(applicationContext))
        val viewModel = ElowViewModel(repository)
        setContent {
            ElowTheme {
                val state by viewModel.uiState.collectAsState()
                ElowApp(state = state, viewModel = viewModel)
            }
        }
    }
}
```

- [ ] **Step 4: Build**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:assembleDebug
```

Expected: pass.

- [ ] **Step 5: Commit shell**

Run:

```bash
cd /Users/summer/Desktop/ELOW
git add android/app/src/main/java/com/elow/app
git commit -m "feat(android): add Elow app shell"
```

## Task 7: Build Home Screen

**Files:**
- Modify: `android/app/src/main/java/com/elow/app/ui/ElowApp.kt`
- Create: `android/app/src/main/java/com/elow/app/ui/home/HomeScreen.kt`

- [ ] **Step 1: Create Home screen**

Create `android/app/src/main/java/com/elow/app/ui/home/HomeScreen.kt`:

```kotlin
package com.elow.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elow.app.core.catalog.ItemCatalog
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemType
import com.elow.app.state.ElowUiState
import com.elow.app.ui.theme.ElowColors

@Composable
fun HomeScreen(
    state: ElowUiState,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Elow", fontWeight = FontWeight.Black)
        Text("Record clearly. Lower gently.", color = Color(0xFF555555))
        TodayShelf(records = state.records.take(4), onAdd = onAdd)
        GoalCard(state = state)
        RecentRecords(records = state.records.take(3))
    }
}

@Composable
private fun TodayShelf(records: List<IntakeRecord>, onAdd: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(4.dp, ElowColors.Ink, RoundedCornerShape(24.dp))
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Text("Today shelf", fontWeight = FontWeight.Black)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 12.dp)) {
            listOf(ItemType.COLA, ItemType.MILK_TEA, ItemType.BEER, ItemType.WINE).forEach { type ->
                val used = records.any { it.itemType == type }
                Text(
                    text = ItemCatalog.definitionFor(type).displayName,
                    modifier = Modifier
                        .weight(1f)
                        .border(3.dp, ElowColors.Ink, RoundedCornerShape(16.dp))
                        .background(if (used) ElowColors.PopGreen else ElowColors.Paper, RoundedCornerShape(16.dp))
                        .padding(10.dp),
                    fontWeight = FontWeight.Black
                )
            }
        }
        Text(
            text = "Tap + to add a record",
            modifier = Modifier.padding(top = 12.dp),
            color = Color(0xFF555555)
        )
    }
}

@Composable
private fun GoalCard(state: ElowUiState) {
    val status = state.goalStatus
    val sugarText = if (status == null) "Sugar target loading" else
        "Sweet drinks ${status.sweetDrinksThisWeek}/${status.sweetDrinkLimit}"
    val alcoholText = if (status == null) "Alcohol target loading" else
        "Alcohol ${status.alcoholOccasionsThisWeek}/${status.alcoholOccasionLimit}"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(4.dp, ElowColors.Ink, RoundedCornerShape(24.dp))
            .background(ElowColors.Butter, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Text("Today target", fontWeight = FontWeight.Black)
        Text(sugarText, modifier = Modifier.padding(top = 8.dp))
        Text(alcoholText, modifier = Modifier.padding(top = 4.dp))
        Text("You recorded clearly.", modifier = Modifier.padding(top = 8.dp), color = Color(0xFF555555))
    }
}

@Composable
private fun RecentRecords(records: List<IntakeRecord>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(4.dp, ElowColors.Ink, RoundedCornerShape(24.dp))
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Text("Recent", fontWeight = FontWeight.Black)
        if (records.isEmpty()) {
            Text("No records yet.", modifier = Modifier.padding(top = 8.dp), color = Color(0xFF555555))
        } else {
            records.forEach { record ->
                Text(
                    text = "${ItemCatalog.definitionFor(record.itemType).displayName} ${(record.amountFraction * 100).toInt()}%",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
```

- [ ] **Step 2: Wire Home into shell**

Modify the content Box in `android/app/src/main/java/com/elow/app/ui/ElowApp.kt` so the `MainTab.HOME` branch calls:

```kotlin
HomeScreen(
    state = state,
    onAdd = { viewModel.openAdd() },
    modifier = Modifier.fillMaxSize()
)
```

Add import:

```kotlin
import com.elow.app.ui.home.HomeScreen
```

- [ ] **Step 3: Build**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:assembleDebug
```

Expected: pass.

- [ ] **Step 4: Commit Home**

Run:

```bash
cd /Users/summer/Desktop/ELOW
git add android/app/src/main/java/com/elow/app/ui
git commit -m "feat(android): build Home screen"
```

## Task 8: Build Full-Screen Add Object Stage

**Files:**
- Modify: `android/app/src/main/java/com/elow/app/ui/ElowApp.kt`
- Create: `android/app/src/main/java/com/elow/app/ui/add/AddScreen.kt`
- Create: `android/app/src/main/java/com/elow/app/ui/add/ObjectCarousel.kt`
- Create: `android/app/src/main/java/com/elow/app/ui/add/ObjectRecorderStage.kt`
- Create: `android/app/src/main/java/com/elow/app/ui/add/ToyObjects.kt`

- [ ] **Step 1: Implement object drawings**

Create `android/app/src/main/java/com/elow/app/ui/add/ToyObjects.kt`:

```kotlin
package com.elow.app.ui.add

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.elow.app.core.model.ItemType
import com.elow.app.ui.theme.ElowColors

@Composable
fun ToyObjectCanvas(
    itemType: ItemType,
    amountFraction: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width * 0.42f
        val height = size.height * 0.72f
        val left = (size.width - width) / 2f
        val top = size.height * 0.12f
        val radius = 42f
        val fillColor = when (itemType) {
            ItemType.COLA -> Color(0xFF3A211B)
            ItemType.MILK_TEA -> Color(0xFFC98B53)
            ItemType.BEER -> Color(0xFFF2B84B)
            ItemType.WINE -> Color(0xFF7A1D3A)
        }
        val labelColor = when (itemType) {
            ItemType.COLA -> ElowColors.ColaRed
            ItemType.MILK_TEA -> ElowColors.PopGreen
            ItemType.BEER -> ElowColors.ToyPurple
            ItemType.WINE -> ElowColors.Mint
        }

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(left, top),
            size = Size(width, height),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawRoundRect(
            color = fillColor,
            topLeft = Offset(left, top + height * (1f - amountFraction.coerceIn(0f, 1f))),
            size = Size(width, height * amountFraction.coerceIn(0f, 1f)),
            cornerRadius = CornerRadius(radius, radius)
        )
        drawRoundRect(
            color = ElowColors.Ink,
            topLeft = Offset(left, top),
            size = Size(width, height),
            cornerRadius = CornerRadius(radius, radius),
            style = Stroke(width = 10f)
        )
        drawRoundRect(
            color = labelColor,
            topLeft = Offset(left + width * 0.18f, top + height * 0.38f),
            size = Size(width * 0.64f, height * 0.18f),
            cornerRadius = CornerRadius(22f, 22f)
        )
    }
}
```

- [ ] **Step 2: Implement top carousel**

Create `android/app/src/main/java/com/elow/app/ui/add/ObjectCarousel.kt`:

```kotlin
package com.elow.app.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elow.app.core.catalog.ItemCatalog
import com.elow.app.core.model.ItemType
import com.elow.app.ui.theme.ElowColors

@Composable
fun ObjectCarousel(
    selected: ItemType,
    onSelect: (ItemType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemCatalog.firstVersionItems.forEach { item ->
            Text(
                text = item.displayName,
                modifier = Modifier
                    .size(width = 82.dp, height = 52.dp)
                    .border(4.dp, ElowColors.Ink, RoundedCornerShape(16.dp))
                    .background(
                        color = if (item.type == selected) ElowColors.PopGreen else ElowColors.Paper,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelect(item.type) }
                    .padding(8.dp),
                fontWeight = FontWeight.Black
            )
        }
    }
}
```

- [ ] **Step 3: Implement draggable object stage**

Create `android/app/src/main/java/com/elow/app/ui/add/ObjectRecorderStage.kt`:

```kotlin
package com.elow.app.ui.add

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elow.app.core.model.ItemType
import com.elow.app.ui.theme.ElowColors
import kotlinx.coroutines.launch

@Composable
fun ObjectRecorderStage(
    itemType: ItemType,
    amountFraction: Double,
    onAmountChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val animatedFraction = remember(itemType) { Animatable(amountFraction.toFloat()) }

    LaunchedEffect(itemType) {
        animatedFraction.snapTo(amountFraction.toFloat())
    }

    Box(
        modifier = modifier
            .border(5.dp, ElowColors.Ink, RoundedCornerShape(30.dp))
            .background(ElowColors.Paper, RoundedCornerShape(30.dp))
            .pointerInput(itemType) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val next = (animatedFraction.value - dragAmount.y / size.height).coerceIn(0f, 1f)
                    scope.launch { animatedFraction.snapTo(next) }
                    onAmountChange(next.toDouble())
                }
            }
            .padding(12.dp)
    ) {
        ToyObjectCanvas(
            itemType = itemType,
            amountFraction = animatedFraction.value,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = "Slide level",
            modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
            fontWeight = FontWeight.Black
        )
    }
}
```

- [ ] **Step 4: Implement Add screen**

Create `android/app/src/main/java/com/elow/app/ui/add/AddScreen.kt`:

```kotlin
package com.elow.app.ui.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elow.app.core.catalog.ItemCatalog
import com.elow.app.core.model.ItemType
import com.elow.app.state.AddState

@Composable
fun AddScreen(
    addState: AddState,
    onSelectItem: (ItemType) -> Unit,
    onAmountChange: (Double) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Add", fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
            Text(ItemCatalog.definitionFor(addState.selectedItem).displayName, fontWeight = FontWeight.Black)
        }
        ObjectCarousel(
            selected = addState.selectedItem,
            onSelect = onSelectItem,
            modifier = Modifier.fillMaxWidth()
        )
        ObjectRecorderStage(
            itemType = addState.selectedItem,
            amountFraction = addState.amountFraction,
            onAmountChange = onAmountChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${(addState.amountFraction * 100).toInt()}%",
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = onSave) {
                Text("Save")
            }
        }
    }
}
```

- [ ] **Step 5: Wire Add into shell**

Modify `android/app/src/main/java/com/elow/app/ui/ElowApp.kt` so the `MainTab.ADD` branch calls:

```kotlin
AddScreen(
    addState = state.addState,
    onSelectItem = { viewModel.selectAddItem(it) },
    onAmountChange = { viewModel.updateAddFraction(it) },
    onSave = { viewModel.saveCurrentRecord() },
    modifier = Modifier.fillMaxSize()
)
```

Add import:

```kotlin
import com.elow.app.ui.add.AddScreen
```

- [ ] **Step 6: Build and manually test drag**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:assembleDebug
```

Expected: pass. On emulator, tapping `+` opens Add; dragging the large object changes the percentage; Save returns Home.

- [ ] **Step 7: Commit Add stage**

Run:

```bash
cd /Users/summer/Desktop/ELOW
git add android/app/src/main/java/com/elow/app/ui
git commit -m "feat(android): add full-screen object recorder"
```

## Task 9: Build Me Screen With Honor Wall And Goals

**Files:**
- Modify: `android/app/src/main/java/com/elow/app/ui/ElowApp.kt`
- Create: `android/app/src/main/java/com/elow/app/ui/me/MeScreen.kt`
- Create: `android/app/src/main/java/com/elow/app/ui/me/HonorWall.kt`

- [ ] **Step 1: Implement Honor wall**

Create `android/app/src/main/java/com/elow/app/ui/me/HonorWall.kt`:

```kotlin
package com.elow.app.ui.me

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elow.app.core.model.HonorReward
import com.elow.app.ui.theme.ElowColors

@Composable
fun HonorWall(
    rewards: List<HonorReward>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(5.dp, ElowColors.Ink, RoundedCornerShape(26.dp))
            .background(ElowColors.Butter, RoundedCornerShape(26.dp))
            .padding(16.dp)
    ) {
        Text("Honor wall", fontWeight = FontWeight.Black)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (rewards.isEmpty()) {
                Text("Your first shelf is waiting.")
            } else {
                rewards.forEach { reward ->
                    Text(
                        text = reward.title,
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .border(4.dp, ElowColors.Ink, RoundedCornerShape(18.dp))
                            .background(ElowColors.PopGreen, RoundedCornerShape(18.dp))
                            .padding(10.dp),
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
```

- [ ] **Step 2: Implement Me screen**

Create `android/app/src/main/java/com/elow/app/ui/me/MeScreen.kt`:

```kotlin
package com.elow.app.ui.me

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elow.app.state.ElowUiState
import com.elow.app.ui.theme.ElowColors

@Composable
fun MeScreen(
    state: ElowUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Me", fontWeight = FontWeight.Black)
        HonorWall(rewards = state.honorRewards, modifier = Modifier.fillMaxWidth())
        MeCard("Goals", "Sweet drinks ${state.goals.weeklySweetDrinkLimit}/week · Alcohol ${state.goals.weeklyAlcoholOccasionLimit}/week")
        MeCard("Weekly review", "This week is lower when your shelf grows.")
        MeCard("Reference advice", "Add body data only when you want a closer reference.")
        MeCard("Settings", "Units, reminders, data.")
    }
}

@Composable
private fun MeCard(title: String, detail: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(4.dp, ElowColors.Ink, RoundedCornerShape(22.dp))
            .background(Color.White, RoundedCornerShape(22.dp))
            .padding(16.dp)
    ) {
        Text(title, fontWeight = FontWeight.Black)
        Text(detail, color = Color(0xFF555555), modifier = Modifier.padding(top = 6.dp))
    }
}
```

- [ ] **Step 3: Wire Me into shell**

Modify `android/app/src/main/java/com/elow/app/ui/ElowApp.kt` so the `MainTab.ME` branch calls:

```kotlin
MeScreen(
    state = state,
    modifier = Modifier.fillMaxSize()
)
```

Add import:

```kotlin
import com.elow.app.ui.me.MeScreen
```

- [ ] **Step 4: Build**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:assembleDebug
```

Expected: pass.

- [ ] **Step 5: Commit Me**

Run:

```bash
cd /Users/summer/Desktop/ELOW
git add android/app/src/main/java/com/elow/app/ui
git commit -m "feat(android): add Me screen and honor wall"
```

## Task 10: Add First-Use Flow

**Files:**
- Modify: `android/app/src/main/java/com/elow/app/ui/ElowApp.kt`
- Create: `android/app/src/main/java/com/elow/app/ui/onboarding/OnboardingScreen.kt`

- [ ] **Step 1: Create onboarding screen**

Create `android/app/src/main/java/com/elow/app/ui/onboarding/OnboardingScreen.kt`:

```kotlin
package com.elow.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elow.app.ui.theme.ElowColors

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ElowColors.Paper)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Elow", fontWeight = FontWeight.Black)
        Text(
            "Track sugar and alcohol like collectibles.",
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(
            "No login. No body data first. Start with one clear record.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp)
                .border(4.dp, ElowColors.Ink, RoundedCornerShape(22.dp))
                .background(ElowColors.Butter, RoundedCornerShape(22.dp))
                .padding(16.dp),
            fontWeight = FontWeight.Bold
        )
        Button(
            onClick = onFinish,
            modifier = Modifier.padding(top = 22.dp)
        ) {
            Text("Enter Elow")
        }
    }
}
```

- [ ] **Step 2: Gate app shell behind onboarding**

Modify `android/app/src/main/java/com/elow/app/ui/ElowApp.kt` so `ElowApp` starts with:

```kotlin
if (!state.onboardingComplete) {
    OnboardingScreen(
        onFinish = { viewModel.completeOnboarding() },
        modifier = Modifier.fillMaxSize()
    )
    return
}
```

Add import:

```kotlin
import com.elow.app.ui.onboarding.OnboardingScreen
```

- [ ] **Step 3: Build**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:assembleDebug
```

Expected: pass.

- [ ] **Step 4: Commit onboarding**

Run:

```bash
cd /Users/summer/Desktop/ELOW
git add android/app/src/main/java/com/elow/app/ui
git commit -m "feat(android): add lightweight onboarding"
```

## Task 11: Add Android Smoke Test

**Files:**
- Create: `android/app/src/androidTest/java/com/elow/app/ElowSmokeTest.kt`

- [ ] **Step 1: Add Compose smoke test**

Create `android/app/src/androidTest/java/com/elow/app/ElowSmokeTest.kt`:

```kotlin
package com.elow.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ElowSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun firstLaunchCanEnterAppAndOpenAdd() {
        composeRule.onNodeWithText("Elow").assertIsDisplayed()
        if (composeRule.onAllNodesWithText("Enter Elow").fetchSemanticsNodes().isNotEmpty()) {
            composeRule.onNodeWithText("Enter Elow").performClick()
        }
        composeRule.onNodeWithText("Home").assertIsDisplayed()
        composeRule.onNodeWithText("+").performClick()
        composeRule.onNodeWithText("Add").assertIsDisplayed()
    }
}
```

- [ ] **Step 2: Run unit and instrumentation tests**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:testDebugUnitTest
./gradlew :app:connectedDebugAndroidTest
```

Expected: unit tests pass. Instrumentation tests pass on a running emulator.

- [ ] **Step 3: Commit tests**

Run:

```bash
cd /Users/summer/Desktop/ELOW
git add android/app/src/androidTest
git commit -m "test(android): add Elow smoke flow"
```

## Task 12: Animation And Interaction Polish Pass

**Files:**
- Modify: `android/app/src/main/java/com/elow/app/ui/add/ObjectRecorderStage.kt`
- Modify: `android/app/src/main/java/com/elow/app/ui/add/AddScreen.kt`
- Modify: `android/app/src/main/java/com/elow/app/ui/me/HonorWall.kt`

- [ ] **Step 1: Add threshold snapping and haptic feedback to object drag**

Update `ObjectRecorderStage` imports with:

```kotlin
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
```

Add this helper below the composable:

```kotlin
private fun snapFraction(value: Float): Float {
    val stops = listOf(0f, 0.25f, 0.5f, 0.75f, 1f)
    return stops.minBy { kotlin.math.abs(it - value) }
}
```

Inside `ObjectRecorderStage`, add:

```kotlin
val haptics = LocalHapticFeedback.current
var lastSnap by remember(itemType) { mutableStateOf(snapFraction(amountFraction.toFloat())) }
```

Replace `detectDragGestures { change, dragAmount -> ... }` with:

```kotlin
detectDragGestures(
    onDrag = { change, dragAmount ->
        change.consume()
        val next = (animatedFraction.value - dragAmount.y / size.height).coerceIn(0f, 1f)
        val nearest = snapFraction(next)
        if (nearest != lastSnap) {
            lastSnap = nearest
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        scope.launch { animatedFraction.snapTo(next) }
        onAmountChange(next.toDouble())
    },
    onDragEnd = {
        val snapped = snapFraction(animatedFraction.value)
        lastSnap = snapped
        scope.launch {
            animatedFraction.animateTo(
                targetValue = snapped,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.82f)
            )
        }
        onAmountChange(snapped.toDouble())
    }
)
```

- [ ] **Step 2: Add object-switch animation**

Wrap `ObjectRecorderStage` in `AddScreen` with `AnimatedContent`:

```kotlin
AnimatedContent(
    targetState = addState.selectedItem,
    label = "object-switch"
) { item ->
    ObjectRecorderStage(
        itemType = item,
        amountFraction = addState.amountFraction,
        onAmountChange = onAmountChange,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    )
}
```

Use default `AnimatedContent` first; tune transitions only after the default motion is stable.

- [ ] **Step 3: Animate honor rewards**

In `HonorWall`, wrap the reward row content in `AnimatedContent(targetState = rewards.size, label = "honor-count")` and keep the reward cards stable by `reward.id`. This creates a collectible "shelf changed" feeling without adding a heavy animation library.

- [ ] **Step 4: Build and manually verify motion**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:assembleDebug
```

Manual expected behavior:

- Add opens full screen.
- Top carousel changes the large object.
- Dragging feels direct and does not lag.
- Releasing the object settles to a clean fraction.
- Saving returns Home without a jarring transition.
- Me honor wall updates after records are saved.

- [ ] **Step 5: Commit polish**

Run:

```bash
cd /Users/summer/Desktop/ELOW
git add android/app/src/main/java/com/elow/app/ui
git commit -m "polish(android): improve object recorder motion"
```

## Task 13: Write Cross-Platform Contract For Future iOS

**Files:**
- Create: `docs/contracts/elow-data-contract.md`

- [ ] **Step 1: Create contract doc**

Create `docs/contracts/elow-data-contract.md`:

```markdown
# Elow Cross-Platform Data Contract

Date: 2026-05-24

## Purpose

This contract lets Android and future iOS share product logic without forcing a cross-platform UI framework.

## Items

The first version supports:

- `COLA`
- `MILK_TEA`
- `BEER`
- `WINE`

## Record

Each intake record has:

- `id`: stable string id.
- `itemType`: one of the first-version items.
- `amountFraction`: decimal from `0.0` to `1.0`.
- `timestampEpochMillis`: local creation time in milliseconds.
- `metrics.sugarGrams`
- `metrics.alcoholGrams`
- `metrics.calories`
- `metrics.money`
- `note`: optional string, empty by default.

## Goals

First-version goals:

- `weeklySweetDrinkLimit`
- `weeklyAlcoholOccasionLimit`

User-defined goals are primary. Reference advice is optional and appears only when the user asks for it.

## Metric Estimate Rules

The Android first-version defaults are:

- Cola full serving: 35g sugar, 0g alcohol, 140 kcal, 1.50 money units.
- Milk tea full serving: 45g sugar, 0g alcohol, 280 kcal, 5.50 money units.
- Beer full serving: 0g sugar, 14g alcohol, 153 kcal, 4.00 money units.
- Wine full serving: 0g sugar, 14g alcohol, 125 kcal, 6.00 money units.

Estimate formula:

`metric = fullServingMetric * clamp(amountFraction, 0.0, 1.0)`

## Language Guardrails

Avoid:

- Failed
- Broke the streak
- Over limit
- Bad day

Prefer:

- Close to your target
- A little high today
- You recorded clearly
- Tomorrow can be lighter
- This week is lower than last week
```

- [ ] **Step 2: Commit contract**

Run:

```bash
cd /Users/summer/Desktop/ELOW
git add docs/contracts/elow-data-contract.md
git commit -m "docs: add Elow cross-platform contract"
```

## Task 14: End-To-End Android Validation

**Files:**
- No source changes expected unless validation finds defects.

- [ ] **Step 1: Run all tests**

Run:

```bash
cd /Users/summer/Desktop/ELOW/android
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 2: Install and launch on emulator**

Run:

```bash
adb devices
cd /Users/summer/Desktop/ELOW/android
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell monkey -p com.elow.app 1
```

Expected: emulator launches Elow.

- [ ] **Step 3: Inspect UI tree**

Run:

```bash
adb shell uiautomator dump /sdcard/window.xml
adb pull /sdcard/window.xml /tmp/elow-window.xml
rg "Elow|Enter Elow|Home|Add|Me" /tmp/elow-window.xml
```

Expected: UI tree contains the current screen text.

- [ ] **Step 4: Manual product QA**

Verify:

- First launch shows lightweight onboarding.
- No login is required.
- No body data is requested.
- Bottom navigation has Home, center `+`, and Me.
- Add is a full-screen object stage, not a grid.
- Top object icons can switch cola, milk tea, beer, and wine.
- Dragging the object changes the amount.
- Saving a record returns to Home.
- Home reflects the new record.
- Me shows honor wall progress and goal/reference/review/settings entries.

- [ ] **Step 5: Commit validation fixes**

If validation required fixes, commit them:

```bash
cd /Users/summer/Desktop/ELOW
git add android docs
git commit -m "fix(android): pass Elow MVP validation"
```

If no fixes were needed, do not create an empty commit.

## Self-Review

Spec coverage:

- Three-entry navigation is covered by Tasks 6-10.
- Full-screen Add object stage is covered by Task 8 and polished in Task 12.
- First-version items are covered by Tasks 2 and 8.
- Actual intake records and estimates are covered by Tasks 3-5.
- User-defined goals are covered by Tasks 3, 5, and 9.
- Optional reference advice is represented in Me and documented in the contract.
- Honor wall is covered by Tasks 3 and 9.
- Low-pressure language is encoded in Home/Me copy and the contract.
- First-use flow is covered by Task 10.
- Future iOS readiness is covered by Task 13.

No implementation task adds a virtual character/avatar system, social feed, barcode scanning, strict abstinence program, or large catalog.

Plan risks:

- Dependency versions were checked against current official sources on 2026-05-24, but Android tooling can reject incompatible combinations. If that happens, use Android Studio's stable generated versions and keep this architecture.
- The first DataStore implementation stores JSON in preferences for speed. If records grow large, migrate to Room in a later version.
- The first Canvas objects are programmatic toy objects. Final art direction can replace drawing internals without changing the recorder stage API.
