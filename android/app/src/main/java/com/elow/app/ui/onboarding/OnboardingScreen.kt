package com.elow.app.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elow.app.R
import com.elow.app.core.model.ItemType
import com.elow.app.ui.add.ToyDrinkObject
import com.elow.app.ui.components.BlueStar
import com.elow.app.ui.components.ContactShadowAsset
import com.elow.app.ui.components.CutoutAsset
import com.elow.app.ui.components.GlossyActionButton
import com.elow.app.ui.components.LineIcon
import com.elow.app.ui.components.ShelfPlankAsset
import com.elow.app.ui.gl.ElowStageScene
import com.elow.app.ui.gl.GlVisualStage
import com.elow.app.ui.text.ElowStrings
import com.elow.app.ui.theme.ElowColors
import com.elow.app.ui.theme.SoftCard

@Composable
fun OnboardingScreen(
    strings: ElowStrings,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.background(
            Brush.verticalGradient(
                listOf(ElowColors.BackgroundTop, ElowColors.Background, ElowColors.BackgroundBottom)
            )
        )
    ) {
        val compact = maxHeight < 760.dp
        val horizontalPadding = if (compact) 20.dp else 24.dp
        val topPadding = if (compact) 14.dp else 18.dp
        val bottomPadding = if (compact) 16.dp else 20.dp
        val logoBoxHeight = if (compact) 104.dp else 118.dp
        val logoWidth = if (compact) 260.dp else 278.dp
        val shelfHeight = if (compact) 258.dp else 290.dp
        val dockHeight = if (compact) 78.dp else 86.dp
        val buttonHeight = if (compact) 54.dp else 56.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = 0.dp)
                .padding(top = topPadding, bottom = bottomPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(if (compact) 8.dp else 14.dp))

            IntroLogo(
                logoWidth = logoWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(logoBoxHeight)
            )

            Column(
                modifier = Modifier.padding(top = if (compact) 0.dp else 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(strings.onboardingLineOne, color = ElowColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Medium, lineHeight = 22.sp)
                Text(strings.onboardingLineTwo, color = ElowColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Medium, lineHeight = 22.sp)
                Text(strings.onboardingLineThree, color = ElowColors.PrimaryBlue, fontSize = 19.sp, fontWeight = FontWeight.Black, lineHeight = 23.sp)
            }

            Spacer(Modifier.height(if (compact) 12.dp else 18.dp))

            IntroDisplayShelf(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(shelfHeight)
            )

            Spacer(Modifier.weight(1f))

            IntroFeatureDock(
                strings = strings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dockHeight)
            )

            Spacer(Modifier.height(if (compact) 8.dp else 10.dp))

            GlossyActionButton(
                text = strings.onboardingButton,
                accent = ElowColors.PrimaryBlue,
                trailingIcon = LineIcon.ArrowRight,
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight)
            )
        }
    }
}

@Composable
private fun IntroLogo(
    logoWidth: Dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.elow_intro_wordmark),
            contentDescription = "Elow",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(logoWidth)
                .aspectRatio(1530f / 665f)
        )
        BlueStar(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 16.dp)
                .size(38.dp)
        )
    }
}

@Composable
private fun IntroDisplayShelf(
    modifier: Modifier = Modifier
) {
    GlVisualStage(
        scene = ElowStageScene.OnboardingShelf,
        modifier = modifier
    )
}

@Composable
private fun TopShelfTier(
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    val shelfHeight = if (compact) 44.dp else 50.dp
    val rowHeight = if (compact) 146.dp else 170.dp
    val shelfOverlap = if (compact) 18.dp else 20.dp

    BoxWithConstraints(modifier = modifier) {
        val designWidth = if (compact) 326.dp else 350.dp
        val start = (maxWidth - designWidth) / 2f
        ShelfPlank(
            height = shelfHeight,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        ShelfDrink(
            itemType = ItemType.COLA,
            width = if (compact) 58.dp else 63.dp,
            slotHeight = rowHeight,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = start + if (compact) 0.dp else 2.dp, y = -(shelfHeight - shelfOverlap))
        )
        ShelfDrink(
            itemType = ItemType.MILK_TEA,
            width = if (compact) 64.dp else 70.dp,
            slotHeight = rowHeight,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = start + if (compact) 88.dp else 95.dp, y = -(shelfHeight - shelfOverlap))
        )
        ShelfDrink(
            itemType = ItemType.BEER,
            width = if (compact) 58.dp else 64.dp,
            slotHeight = rowHeight,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = start + if (compact) 178.dp else 192.dp, y = -(shelfHeight - shelfOverlap))
        )
        ShelfDrink(
            itemType = ItemType.WINE,
            width = if (compact) 60.dp else 66.dp,
            slotHeight = rowHeight,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = start + if (compact) 270.dp else 292.dp, y = -(shelfHeight - shelfOverlap))
        )
    }
}

@Composable
private fun BottomShelfTier(
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    val shelfHeight = if (compact) 42.dp else 46.dp
    val rowHeight = if (compact) 82.dp else 94.dp
    val shelfOverlap = if (compact) 16.dp else 18.dp

    BoxWithConstraints(modifier = modifier) {
        val designWidth = if (compact) 326.dp else 350.dp
        val start = (maxWidth - designWidth) / 2f
        ShelfPlank(
            height = shelfHeight,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        ShelfCutout(
            drawableResId = R.drawable.elow_sugar_jar,
            contentDescription = "sugar jar",
            width = if (compact) 86.dp else 96.dp,
            slotHeight = rowHeight,
            shadowWidth = if (compact) 62.dp else 70.dp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = start + if (compact) 44.dp else 50.dp, y = -(shelfHeight - shelfOverlap))
        )
        ShelfCutout(
            drawableResId = R.drawable.elow_coin_stack,
            contentDescription = "coin stack",
            width = if (compact) 122.dp else 136.dp,
            slotHeight = rowHeight,
            shadowWidth = if (compact) 100.dp else 112.dp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = start + if (compact) 178.dp else 192.dp, y = -(shelfHeight - shelfOverlap))
        )
    }
}

@Composable
private fun ShelfDrink(
    itemType: ItemType,
    width: Dp,
    slotHeight: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(width)
            .height(slotHeight),
        contentAlignment = Alignment.BottomCenter
    ) {
        ContactShadow(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 1.dp)
                .width(width * 0.88f)
                .height(11.dp)
        )
        ToyDrinkObject(
            itemType = itemType,
            amountFraction = 0.88f,
            compact = true,
            alignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ShelfCutout(
    drawableResId: Int,
    contentDescription: String,
    width: Dp,
    slotHeight: Dp,
    shadowWidth: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(width)
            .height(slotHeight),
        contentAlignment = Alignment.BottomCenter
    ) {
        ContactShadow(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 1.dp)
                .width(shadowWidth)
                .height(12.dp)
        )
        CutoutAsset(
            drawableResId = drawableResId,
            contentDescription = contentDescription,
            alignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ContactShadow(
    modifier: Modifier = Modifier
) {
    ContactShadowAsset(modifier = modifier)
}

@Composable
private fun ShelfPlank(
    height: Dp,
    modifier: Modifier = Modifier
) {
    ShelfPlankAsset(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    )
}

@Composable
private fun IntroFeatureDock(
    strings: ElowStrings,
    modifier: Modifier = Modifier
) {
    SoftCard(modifier = modifier, radius = 16.dp, elevation = 12.dp) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IntroStep(R.drawable.elow_feature_record, strings.introRecordTitle, strings.introRecordDetail, Modifier.weight(1f))
            Divider()
            IntroStep(R.drawable.elow_feature_chart, strings.introReductionTitle, strings.introReductionDetail, Modifier.weight(1f))
            Divider()
            IntroStep(R.drawable.elow_feature_collect, strings.introCollectTitle, strings.introCollectDetail, Modifier.weight(1f))
        }
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .height(42.dp)
            .width(1.dp)
            .background(ElowColors.Hairline)
    )
}

@Composable
private fun IntroStep(
    iconResId: Int,
    title: String,
    detail: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(31.dp),
            contentAlignment = Alignment.Center
        ) {
            CutoutAsset(
                drawableResId = iconResId,
                contentDescription = title,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(title, color = ElowColors.Ink, fontWeight = FontWeight.Black, fontSize = 11.sp, textAlign = TextAlign.Center)
        Text(detail, color = ElowColors.Muted, fontSize = 9.sp, textAlign = TextAlign.Center, lineHeight = 10.sp)
    }
}
