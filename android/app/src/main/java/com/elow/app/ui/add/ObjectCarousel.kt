package com.elow.app.ui.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elow.app.core.catalog.ItemCatalog
import com.elow.app.core.model.ItemDefinition
import com.elow.app.core.model.ItemType
import com.elow.app.ui.text.ElowStrings
import com.elow.app.ui.theme.ElowColors
import com.elow.app.ui.theme.SoftCard

@Composable
fun ObjectCarousel(
    selected: ItemType,
    catalog: List<ItemDefinition>,
    strings: ElowStrings,
    onSelect: (ItemType) -> Unit,
    modifier: Modifier = Modifier
) {
    SoftCard(
        modifier = modifier
            .height(94.dp)
            .padding(horizontal = 20.dp, vertical = 5.dp),
        radius = 16.dp,
        elevation = 13.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.97f))
                .padding(horizontal = 6.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemCatalog.visibleItems(catalog).forEach { item ->
                CarouselItem(
                    item = item,
                    strings = strings,
                    selected = item.type == selected,
                    onClick = { onSelect(item.type) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CarouselItem(
    item: ItemDefinition,
    strings: ElowStrings,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(shape)
            .background(if (selected) Color(0xFFF7FBFF) else Color.Transparent)
            .border(
                BorderStroke(if (selected) 1.6.dp else 0.dp, if (selected) ElowColors.PrimaryBlue else Color.Transparent),
                shape
            )
            .clickable { onClick() }
            .padding(top = 2.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            ToyDrinkObject(
                itemType = item.type,
                amountFraction = 0.86f,
                compact = true,
                modifier = Modifier.fillMaxSize().padding(horizontal = 3.dp)
            )
        }
        Text(
            text = strings.itemName(item.type, item.displayName),
            color = ElowColors.Ink,
            fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold,
            fontSize = 10.sp,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}
