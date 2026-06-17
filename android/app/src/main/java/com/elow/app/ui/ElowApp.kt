package com.elow.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elow.app.state.ElowUiState
import com.elow.app.state.ElowViewModel
import com.elow.app.state.MainTab
import com.elow.app.ui.add.AddScreen
import com.elow.app.ui.components.LineIcon
import com.elow.app.ui.components.LineIconView
import com.elow.app.ui.home.HomeScreen
import com.elow.app.ui.me.MeScreen
import com.elow.app.ui.onboarding.OnboardingScreen
import com.elow.app.ui.text.ElowStrings
import com.elow.app.ui.text.stringsFor
import com.elow.app.ui.theme.ElowColors
import com.elow.app.ui.theme.ElowDimens
import com.elow.app.ui.theme.LightShadowBackground

@Composable
fun ElowApp(
    state: ElowUiState,
    viewModel: ElowViewModel,
    modifier: Modifier = Modifier
) {
    val strings = remember(state.language) { stringsFor(state.language) }

    if (!state.onboardingComplete) {
        OnboardingScreen(
            strings = strings,
            onFinish = { viewModel.completeOnboarding() },
            modifier = modifier.fillMaxSize()
        )
        return
    }

    if (state.selectedTab == MainTab.ADD) {
        BackHandler {
            viewModel.closeAdd()
        }
        AddScreen(
            addState = state.addState,
            catalog = state.catalog,
            strings = strings,
            onBack = { viewModel.closeAdd() },
            onSelectItem = { viewModel.selectAddItem(it) },
            onAmountChange = { viewModel.updateAddFraction(it) },
            onSave = { viewModel.saveCurrentRecord() },
            modifier = modifier.fillMaxSize()
        )
        return
    }

    LightShadowBackground(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (state.selectedTab) {
                    MainTab.HOME -> HomeScreen(
                        state = state,
                        strings = strings,
                        onAdd = { viewModel.openAdd() },
                        modifier = Modifier.fillMaxSize()
                    )

                    MainTab.ME -> MeScreen(
                        state = state,
                        strings = strings,
                        onGoalsChange = { viewModel.updateGoals(it) },
                        onLanguageChange = { viewModel.updateLanguage(it) },
                        modifier = Modifier.fillMaxSize()
                    )

                    MainTab.ADD -> Unit
                }
            }

            ElowBottomBar(
                strings = strings,
                selectedTab = state.selectedTab,
                onHome = { viewModel.selectTab(MainTab.HOME) },
                onAdd = { viewModel.openAdd() },
                onMe = { viewModel.selectTab(MainTab.ME) }
            )
        }
    }
}

@Composable
private fun ElowBottomBar(
    strings: ElowStrings,
    selectedTab: MainTab,
    onHome: () -> Unit,
    onAdd: () -> Unit,
    onMe: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ElowDimens.BottomBarHeight)
            .background(Color.Transparent)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                DockTabPill(modifier = Modifier.weight(1f)) {
                    DockTabItem(
                        label = strings.navHome,
                        icon = LineIcon.Home,
                        selected = selectedTab == MainTab.HOME,
                        onClick = onHome,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Box(modifier = Modifier.width(76.dp))
                DockTabPill(modifier = Modifier.weight(1f)) {
                    DockTabItem(
                        label = strings.navMe,
                        icon = LineIcon.Profile,
                        selected = selectedTab == MainTab.ME,
                        onClick = onMe,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            DockAddButton(
                onClick = onAdd,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(70.dp)
            )
        }
    }
}

@Composable
private fun DockTabPill(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val dockShape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .height(56.dp)
            .shadow(14.dp, dockShape, ambientColor = ElowColors.Shadow, spotColor = ElowColors.Shadow)
            .clip(dockShape)
            .background(
                Brush.verticalGradient(
                    listOf(Color.White, Color(0xFFFCFDFD), Color(0xFFF6F7F7))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun DockTabItem(
    label: String,
    icon: LineIcon,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = if (selected) ElowColors.PrimaryBlue else ElowColors.Ink
    val labelColor = if (selected) ElowColors.PrimaryBlue else ElowColors.Muted

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(top = 5.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LineIconView(
            icon = icon,
            color = iconColor,
            modifier = Modifier.size(21.dp),
            strokeWidth = 3.0f
        )
        Text(
            text = label,
            color = labelColor,
            fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
            fontSize = 9.5.sp
        )
    }
}

@Composable
private fun DockAddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 18.dp,
                shape = CircleShape,
                ambientColor = ElowColors.PrimaryBlue.copy(alpha = 0.24f),
                spotColor = ElowColors.PrimaryBlue.copy(alpha = 0.34f)
            )
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.96f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    ambientColor = ElowColors.PrimaryBlue.copy(alpha = 0.24f),
                    spotColor = ElowColors.PrimaryBlue.copy(alpha = 0.36f)
                )
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF63B4FF), Color(0xFF2788EA), Color(0xFF106ED2))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 7.dp)
                    .width(39.dp)
                    .height(13.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.23f))
            )
            PlusGlyph(modifier = Modifier.size(35.dp))
        }
    }
}

@Composable
private fun PlusGlyph(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.18f
        val center = Offset(size.width / 2f, size.height / 2f)
        drawLine(
            color = Color.White,
            start = Offset(center.x, size.height * 0.16f),
            end = Offset(center.x, size.height * 0.84f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Square
        )
        drawLine(
            color = Color.White,
            start = Offset(size.width * 0.16f, center.y),
            end = Offset(size.width * 0.84f, center.y),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Square
        )
    }
}
