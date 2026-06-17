package com.elow.app.ui.me

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elow.app.core.model.AppLanguage
import com.elow.app.core.model.GoalSettings
import com.elow.app.state.ElowUiState
import com.elow.app.ui.components.LineIcon
import com.elow.app.ui.components.RoundIconButton
import com.elow.app.ui.text.ElowStrings
import com.elow.app.ui.theme.ElowColors
import com.elow.app.ui.theme.ElowDimens
import com.elow.app.ui.theme.SoftCard

@Composable
@Suppress("UNUSED_PARAMETER")
fun MeScreen(
    state: ElowUiState,
    strings: ElowStrings,
    onGoalsChange: (GoalSettings) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                strings.honorWallTitle,
                modifier = Modifier.weight(1f),
                color = ElowColors.Ink,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black
            )
            RoundIconButton(icon = LineIcon.Share, onClick = {}, size = 40.dp)
        }

        HonorWall(records = state.records, rewards = state.honorRewards, strings = strings, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(96.dp))
        LanguageSettingsCard(
            language = state.language,
            strings = strings,
            onLanguageChange = onLanguageChange,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun LanguageSettingsCard(
    language: AppLanguage,
    strings: ElowStrings,
    onLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    SoftCard(modifier = modifier.height(112.dp), radius = 15.dp, elevation = 10.dp) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(strings.settingsTitle, color = ElowColors.Ink, fontWeight = FontWeight.Black, fontSize = 15.sp)
                Text(strings.languageDetail, color = ElowColors.Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    strings.languageTitle,
                    color = ElowColors.Ink,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
                LanguageSegmentedControl(
                    language = language,
                    strings = strings,
                    onLanguageChange = onLanguageChange
                )
            }
        }
    }
}

@Composable
private fun LanguageSegmentedControl(
    language: AppLanguage,
    strings: ElowStrings,
    onLanguageChange: (AppLanguage) -> Unit
) {
    Row(
        modifier = Modifier
            .height(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF2F4F5))
            .border(BorderStroke(1.dp, ElowColors.Hairline), RoundedCornerShape(10.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        LanguageOption(
            label = strings.english,
            selected = language == AppLanguage.ENGLISH,
            onClick = { onLanguageChange(AppLanguage.ENGLISH) }
        )
        LanguageOption(
            label = strings.chinese,
            selected = language == AppLanguage.CHINESE,
            onClick = { onLanguageChange(AppLanguage.CHINESE) }
        )
    }
}

@Composable
private fun LanguageOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .border(
                BorderStroke(if (selected) 1.dp else 0.dp, if (selected) ElowColors.PrimaryBlue.copy(alpha = 0.32f) else Color.Transparent),
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = if (selected) ElowColors.PrimaryBlue else ElowColors.Muted,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}
