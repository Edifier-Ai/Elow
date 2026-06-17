package com.elow.app.ui.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.elow.app.R
import com.elow.app.core.model.ItemType
import com.elow.app.ui.components.ContactShadowAsset

@Composable
fun ToyDrinkObject(
    itemType: ItemType,
    amountFraction: Float,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    alignment: Alignment = Alignment.Center
) {
    Box(modifier = modifier) {
        ContactShadowAsset(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(if (compact) 0.74f else 0.86f)
                .height(if (compact) 12.dp else 24.dp)
        )
        Image(
            painter = painterResource(id = itemType.assetResId()),
            contentDescription = itemType.name,
            contentScale = ContentScale.Fit,
            alignment = alignment,
            alpha = 0.94f + amountFraction.coerceIn(0f, 1f) * 0.06f,
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun ItemType.assetResId(): Int =
    when (this) {
        ItemType.COLA -> R.drawable.elow_drink_cola
        ItemType.MILK_TEA -> R.drawable.elow_drink_milk_tea
        ItemType.BEER -> R.drawable.elow_drink_beer
        ItemType.WINE -> R.drawable.elow_drink_wine
    }
