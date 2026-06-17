package com.elow.app.ui.add

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elow.app.core.catalog.ItemCatalog
import com.elow.app.core.model.IntakeFamily
import com.elow.app.core.model.ItemDefinition
import com.elow.app.core.model.ItemType
import com.elow.app.state.AddState
import com.elow.app.ui.components.GlossyActionButton
import com.elow.app.ui.components.LineIcon
import com.elow.app.ui.components.LineIconView
import com.elow.app.ui.text.ElowStrings
import com.elow.app.ui.theme.ElowColors
import com.elow.app.ui.theme.SoftCard

@Composable
fun AddScreen(
    addState: AddState,
    catalog: List<ItemDefinition>,
    strings: ElowStrings,
    onBack: () -> Unit,
    onSelectItem: (ItemType) -> Unit,
    onAmountChange: (Double) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selected = ItemCatalog.definitionFor(addState.selectedItem, catalog)
    val accent = if (selected.family == IntakeFamily.ALCOHOL && addState.selectedItem == ItemType.WINE) {
        ElowColors.WineRed
    } else {
        ElowColors.PrimaryBlue
    }

    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(ElowColors.BackgroundTop, ElowColors.Background, ElowColors.BackgroundBottom)
                )
            )
            .statusBarsPadding()
            .padding(top = 14.dp, bottom = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(horizontal = 20.dp)
        ) {
            IconHitArea(
                icon = LineIcon.Back,
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = strings.addRecord,
                modifier = Modifier.align(Alignment.Center),
                color = ElowColors.Ink,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
            IconHitArea(
                icon = LineIcon.Info,
                onClick = {},
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        ObjectCarousel(
            selected = addState.selectedItem,
            catalog = catalog,
            strings = strings,
            onSelect = onSelectItem,
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = addState.selectedItem,
                label = "object-switch"
            ) { item ->
                ObjectRecorderStage(
                    itemType = item,
                    itemDefinition = ItemCatalog.definitionFor(item, catalog),
                    amountFraction = addState.amountFraction,
                    strings = strings,
                    onAmountChange = onAmountChange,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        AddNoteRow(
            strings = strings,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 20.dp)
        )

        GlossyActionButton(
            text = strings.saveRecord,
            accent = accent,
            trailingIcon = LineIcon.Check,
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .padding(start = 20.dp, end = 20.dp, top = 6.dp)
        )
    }
}

@Composable
private fun IconHitArea(icon: LineIcon, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        LineIconView(icon = icon, color = ElowColors.Ink, modifier = Modifier.size(24.dp), strokeWidth = 3.1f)
    }
}

@Composable
private fun AddNoteRow(strings: ElowStrings, modifier: Modifier = Modifier) {
    SoftCard(modifier = modifier, radius = 12.dp, elevation = 6.dp) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LineIconView(icon = LineIcon.Pencil, color = ElowColors.Muted, modifier = Modifier.size(18.dp), strokeWidth = 2.5f)
            Text(
                strings.addNote,
                color = ElowColors.Muted,
                fontSize = 13.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
            LineIconView(icon = LineIcon.ArrowRight, color = ElowColors.Muted, modifier = Modifier.size(20.dp), strokeWidth = 2.4f)
        }
    }
}
