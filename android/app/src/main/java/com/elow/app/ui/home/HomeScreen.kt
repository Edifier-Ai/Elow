package com.elow.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elow.app.core.catalog.ItemCatalog
import com.elow.app.core.model.IntakeFamily
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemDefinition
import com.elow.app.core.model.ItemType
import com.elow.app.state.ElowUiState
import com.elow.app.ui.add.ToyDrinkObject
import com.elow.app.ui.components.BlueStar
import com.elow.app.ui.components.LineIcon
import com.elow.app.ui.components.RoundIconButton
import com.elow.app.ui.components.SugarJarObject
import com.elow.app.ui.gl.ElowStageScene
import com.elow.app.ui.gl.GlVisualStage
import com.elow.app.ui.reference.DesignReferenceContent
import com.elow.app.ui.text.ElowStrings
import com.elow.app.ui.theme.ElowColors
import com.elow.app.ui.theme.ElowDimens
import com.elow.app.ui.theme.SoftCard
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    state: ElowUiState,
    strings: ElowStrings,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todayRecords = state.records.filter { it.isToday() }
    val usesReferenceContent = todayRecords.isEmpty()
    val displayRecords = if (usesReferenceContent) {
        DesignReferenceContent.homeRecentRecords(System.currentTimeMillis())
    } else {
        todayRecords
    }
    val sugarGrams = if (usesReferenceContent) {
        DesignReferenceContent.homeSugarGrams
    } else {
        displayRecords.sumOf { it.metrics.sugarGrams }
    }
    val alcoholOccasions = if (usesReferenceContent) {
        DesignReferenceContent.homeAlcoholOccasions
    } else {
        displayRecords.count { ItemCatalog.definitionFor(it.itemType, state.catalog).family == IntakeFamily.ALCOHOL }
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .statusBarsPadding()
            .padding(top = 18.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = strings.todayTitle,
                modifier = Modifier.weight(1f),
                color = ElowColors.Ink,
                fontSize = 29.sp,
                fontWeight = FontWeight.Black
            )
            RoundIconButton(icon = LineIcon.Calendar, onClick = {}, size = 44.dp)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            TodayMetricCard(
                title = strings.sugarTitle,
                value = sugarGrams.roundToInt().toString(),
                unit = "g",
                detail = strings.sugarGoalDetail(state.goals.dailySugarGramTarget),
                progress = (sugarGrams / state.goals.dailySugarGramTarget).toFloat().coerceIn(0f, 1f),
                tint = ElowColors.SugarBlue,
                progressColor = ElowColors.PrimaryBlue,
                highlightTitle = true,
                content = { GlVisualStage(scene = ElowStageScene.HomeSugar, modifier = Modifier.fillMaxSize()) },
                modifier = Modifier.weight(1f)
            )
            TodayMetricCard(
                title = strings.alcoholTitle,
                value = alcoholOccasions.toString(),
                unit = "",
                detail = strings.alcoholGoalDetail(state.goals.weeklyAlcoholOccasionLimit),
                progress = (alcoholOccasions.toDouble() / state.goals.weeklyAlcoholOccasionLimit).toFloat().coerceIn(0f, 1f),
                tint = ElowColors.AlcoholCream,
                progressColor = ElowColors.Green,
                highlightTitle = false,
                content = { GlVisualStage(scene = ElowStageScene.HomeAlcohol, modifier = Modifier.fillMaxSize()) },
                modifier = Modifier.weight(1f)
            )
        }

        RecentRecords(records = displayRecords.take(4), catalog = state.catalog, strings = strings, onAdd = onAdd)

        SoftCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            radius = 14.dp,
            color = Color(0xFFE8F8FF),
            elevation = 9.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(Color(0xFFE8F8FF), Color(0xFFD8F1FF))))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(strings.goodChoicesTitle, color = ElowColors.Ink, fontWeight = FontWeight.Black, fontSize = 17.sp)
                    Text(strings.goodChoicesDetail, color = ElowColors.Muted, fontSize = 14.sp)
                }
                BlueStar(modifier = Modifier.size(56.dp))
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
private fun TodayMetricCard(
    title: String,
    value: String,
    unit: String,
    detail: String,
    progress: Float,
    tint: Color,
    progressColor: Color,
    highlightTitle: Boolean,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
        SoftCard(modifier = modifier.height(256.dp), radius = 18.dp, elevation = 16.dp) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.White, Color(0xFFF9FAF9))))
                .padding(15.dp)
        ) {
            Text(title, color = if (highlightTitle) ElowColors.PrimaryBlue else ElowColors.Ink, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, color = ElowColors.Ink, fontWeight = FontWeight.Black, fontSize = 34.sp)
                if (unit.isNotBlank()) {
                    Text(" $unit", color = ElowColors.Ink, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 4.dp))
                }
            }
            Text(detail, color = ElowColors.Muted, fontSize = 12.sp)

            MetricObjectStage(
                tint = tint,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 9.dp, bottom = 8.dp),
                content = content
            )

            ProgressBar(progress = progress, color = progressColor)
        }
    }
}

@Composable
private fun MetricObjectStage(
    tint: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(17.dp))
            .background(Brush.verticalGradient(listOf(Color.White, tint.copy(alpha = 0.42f)))),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
private fun AlcoholMiniStage() {
    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 3.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        ToyDrinkObject(
            itemType = ItemType.WINE,
            amountFraction = 0.72f,
            compact = true,
            modifier = Modifier.width(58.dp).fillMaxHeight()
        )
        ToyDrinkObject(
            itemType = ItemType.BEER,
            amountFraction = 0.8f,
            compact = true,
            modifier = Modifier.width(54.dp).fillMaxHeight()
        )
    }
}

@Composable
private fun ProgressBar(progress: Float, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(99.dp))
            .background(ElowColors.Hairline)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(99.dp))
                .background(color)
        )
    }
}

@Composable
private fun RecentRecords(
    records: List<IntakeRecord>,
    catalog: List<ItemDefinition>,
    strings: ElowStrings,
    onAdd: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                strings.recentRecords,
                modifier = Modifier.weight(1f),
                color = ElowColors.Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            Text(strings.seeAll, color = ElowColors.PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        if (records.isEmpty()) {
            SoftCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clickable { onAdd() },
                radius = 14.dp,
                elevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ToyDrinkObject(
                        itemType = ItemType.COLA,
                        amountFraction = 0.65f,
                        compact = true,
                        modifier = Modifier.width(42.dp).fillMaxHeight()
                    )
                    Text(
                        text = strings.emptyRecords,
                        color = ElowColors.Muted,
                        fontSize = 17.sp,
                        lineHeight = 27.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier.padding(start = 18.dp, end = 40.dp)
                    )
                }
            }
        } else {
            records.forEach { record ->
                RecentRecordRow(record, catalog, strings)
            }
        }
    }
}

@Composable
private fun RecentRecordRow(record: IntakeRecord, catalog: List<ItemDefinition>, strings: ElowStrings) {
    val definition = ItemCatalog.definitionFor(record.itemType, catalog)
    val timeText = DateTimeFormatter.ofPattern("h:mm a")
        .format(Instant.ofEpochMilli(record.timestampEpochMillis).atZone(ZoneId.systemDefault()))
    val detail = if (definition.family == IntakeFamily.SUGAR) {
        strings.sugarRecordDetail(record.metrics.sugarGrams.roundToInt())
    } else {
        strings.alcoholRecordDetail(record.metrics.alcoholGrams / 14.0)
    }

    SoftCard(modifier = Modifier.fillMaxWidth().height(68.dp), radius = 14.dp, elevation = 6.dp) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToyDrinkObject(
                itemType = record.itemType,
                amountFraction = record.amountFraction.toFloat(),
                compact = true,
                modifier = Modifier
                    .width(44.dp)
                    .fillMaxHeight()
            )
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(strings.itemName(record.itemType, definition.displayName), color = ElowColors.Ink, fontWeight = FontWeight.Black)
                Text(
                    "${(definition.fullServingMl * record.amountFraction).roundToInt()} ml  -  $detail",
                    color = ElowColors.Muted,
                    fontSize = 12.sp
                )
            }
            Text(timeText, color = ElowColors.Muted, fontSize = 12.sp)
        }
    }
}

private fun IntakeRecord.isToday(): Boolean {
    val zone = ZoneId.systemDefault()
    val date = Instant.ofEpochMilli(timestampEpochMillis).atZone(zone).toLocalDate()
    return date == LocalDate.now(zone)
}
