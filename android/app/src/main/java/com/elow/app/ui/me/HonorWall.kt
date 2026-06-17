package com.elow.app.ui.me

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elow.app.R
import com.elow.app.core.model.HonorReward
import com.elow.app.core.model.IntakeRecord
import com.elow.app.core.model.ItemType
import com.elow.app.ui.add.ToyDrinkObject
import com.elow.app.ui.components.ContactShadowAsset
import com.elow.app.ui.components.CoinStackObject
import com.elow.app.ui.components.CutoutAsset
import com.elow.app.ui.components.LockedBottleAsset
import com.elow.app.ui.components.ShelfPlankAsset
import com.elow.app.ui.components.SugarJarObject
import com.elow.app.ui.gl.ElowStageScene
import com.elow.app.ui.gl.GlVisualStage
import com.elow.app.ui.text.ElowStrings
import com.elow.app.ui.theme.ElowColors
import com.elow.app.ui.theme.SoftCard
import kotlin.math.roundToInt

@Composable
fun HonorWall(
    records: List<IntakeRecord>,
    rewards: List<HonorReward>,
    strings: ElowStrings,
    modifier: Modifier = Modifier
) {
    val showReferenceUnlocks = records.isEmpty()

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SoftCard(modifier = Modifier.fillMaxWidth().height(178.dp), radius = 18.dp, elevation = 16.dp) {
            Column(modifier = Modifier.fillMaxSize().padding(start = 13.dp, end = 13.dp, top = 10.dp, bottom = 9.dp)) {
                Text(strings.collectibles, color = ElowColors.Ink, fontWeight = FontWeight.Black, fontSize = 15.sp)
                CollectiblesShelf(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 3.dp)
                )
            }
        }

        SoftCard(modifier = Modifier.fillMaxWidth().height(124.dp), radius = 18.dp, elevation = 14.dp) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 13.dp, vertical = 9.dp)) {
                Text(strings.badges, color = ElowColors.Ink, fontWeight = FontWeight.Black, fontSize = 15.sp)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Badge(strings.firstRecord, BadgeAsset.First, showReferenceUnlocks || records.isNotEmpty(), ElowColors.PrimaryBlue, Modifier.weight(1f))
                    Badge(strings.sevenDays, BadgeAsset.Seven, showReferenceUnlocks || records.size >= 7, ElowColors.Green, Modifier.weight(1f))
                    Badge(strings.thirtyDays, BadgeAsset.Thirty, showReferenceUnlocks || records.size >= 30, ElowColors.Gold, Modifier.weight(1f))
                    Badge(
                        strings.consistent,
                        BadgeAsset.Cup,
                        showReferenceUnlocks || (records.size >= 3 && rewards.any { it.id == "sweet-drink-shelf-1" || it.id == "alcohol-shelf-1" }),
                        ElowColors.Lavender,
                        Modifier.weight(1f)
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            ImpactCard(
                title = strings.sugarCubesSaved,
                value = sugarCubeProof(records).coerceAtLeast(128.takeIf { records.isEmpty() } ?: 0).toString(),
                caption = strings.cubes,
                visual = { SugarJarObject(modifier = Modifier.fillMaxSize(), cubeCount = 9) },
                modifier = Modifier.weight(1f)
            )
            ImpactCard(
                title = strings.moneySaved,
                value = "$${moneyProof(records)}",
                caption = strings.saved,
                visual = { CoinStackObject(modifier = Modifier.fillMaxSize()) },
                modifier = Modifier.weight(1f)
            )
        }

        SoftCard(modifier = Modifier.fillMaxWidth().height(152.dp), radius = 18.dp, elevation = 14.dp) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 9.dp)) {
                Text(strings.stageMemories, color = ElowColors.Ink, fontWeight = FontWeight.Black, fontSize = 15.sp)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MemoryTile(MemoryKind.Camp, Modifier.weight(1f))
                    MemoryTile(MemoryKind.Plane, Modifier.weight(1f))
                    MemoryTile(MemoryKind.Camera, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CollectiblesShelf(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(Brush.verticalGradient(listOf(Color.White, Color(0xFFF4F6F5))))
    ) {
        GlVisualStage(
            scene = ElowStageScene.HonorCollectibles,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun CollectibleSlot(itemType: ItemType, unlocked: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(itemType.collectibleWidth() + 18.dp)
            .height(itemType.collectibleHeight() + 18.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (unlocked) {
            ToyDrinkObject(
                itemType = itemType,
                amountFraction = 0.9f,
                compact = true,
                modifier = Modifier
                    .width(itemType.collectibleWidth())
                    .height(itemType.collectibleHeight())
            )
        } else {
            LockedBottleFigure(modifier = Modifier.width(itemType.collectibleWidth()).height(itemType.collectibleHeight()))
        }
    }
}

@Composable
private fun LockedSlot(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(58.dp)
            .height(92.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        LockedBottleFigure(modifier = Modifier.width(41.dp).height(74.dp))
    }
}

@Composable
private fun LockedBottleFigure(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        ContactShadowAsset(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(54.dp)
                .height(12.dp)
        )
        LockedBottleAsset(modifier = Modifier.fillMaxSize())
        Text("?", color = Color(0xFF8F918B), fontWeight = FontWeight.Black, fontSize = 20.sp)
    }
}

@Composable
private fun ShelfPlank(modifier: Modifier = Modifier) {
    ShelfPlankAsset(modifier = modifier)
}

private fun ItemType.collectibleWidth(): Dp =
    when (this) {
        ItemType.COLA -> 39.dp
        ItemType.MILK_TEA -> 50.dp
        ItemType.BEER -> 47.dp
        ItemType.WINE -> 42.dp
    }

private fun ItemType.collectibleHeight(): Dp =
    when (this) {
        ItemType.COLA -> 90.dp
        ItemType.MILK_TEA -> 87.dp
        ItemType.BEER -> 87.dp
        ItemType.WINE -> 90.dp
    }

private enum class BadgeAsset {
    First,
    Seven,
    Thirty,
    Cup
}

@Composable
@Suppress("UNUSED_PARAMETER")
private fun Badge(
    caption: String,
    asset: BadgeAsset,
    unlocked: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    val grayscaleFilter = if (unlocked) {
        null
    } else {
        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(54.dp), contentAlignment = Alignment.Center) {
            if (!unlocked) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF0F0ED))
                        .border(BorderStroke(1.dp, Color(0xFFE1E1DC)), CircleShape)
                )
            }
            CutoutAsset(
                drawableResId = asset.resId(),
                contentDescription = caption,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (unlocked) 1f else 0.38f),
                colorFilter = grayscaleFilter
            )
        }
        Text(
            caption,
            color = if (unlocked) ElowColors.Muted else Color(0xFF9B9D98),
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

private fun BadgeAsset.resId(): Int =
    when (this) {
        BadgeAsset.First -> R.drawable.elow_badge_first
        BadgeAsset.Seven -> R.drawable.elow_badge_7
        BadgeAsset.Thirty -> R.drawable.elow_badge_30
        BadgeAsset.Cup -> R.drawable.elow_badge_cup
    }

@Composable
private fun ImpactCard(
    title: String,
    value: String,
    caption: String,
    visual: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    SoftCard(modifier = modifier.height(124.dp), radius = 18.dp, elevation = 14.dp) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 11.dp, vertical = 10.dp)) {
            Text(title, color = ElowColors.Ink, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    visual()
                }
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(value, color = ElowColors.Ink, fontWeight = FontWeight.Black, fontSize = 27.sp)
                    Text(caption, color = ElowColors.Muted, fontSize = 11.sp)
                }
            }
        }
    }
}

private enum class MemoryKind {
    Camp,
    Plane,
    Camera
}

@Composable
private fun MemoryTile(kind: MemoryKind, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF3F4EF))))
            .border(BorderStroke(1.dp, Color(0xFFE7E8E3)), RoundedCornerShape(12.dp))
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        CutoutAsset(
            drawableResId = kind.assetResId(),
            contentDescription = kind.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

private fun MemoryKind.assetResId(): Int =
    when (this) {
        MemoryKind.Camp -> R.drawable.elow_memory_camp
        MemoryKind.Plane -> R.drawable.elow_memory_plane
        MemoryKind.Camera -> R.drawable.elow_memory_camera
    }

private fun sugarCubeProof(records: List<IntakeRecord>): Int {
    val sugarGrams = records.sumOf { it.metrics.sugarGrams }
    return (sugarGrams / 4.0).roundToInt()
}

private fun moneyProof(records: List<IntakeRecord>): Int {
    val money = records.sumOf { it.metrics.money }.roundToInt()
    return if (records.isEmpty()) 86 else money
}
