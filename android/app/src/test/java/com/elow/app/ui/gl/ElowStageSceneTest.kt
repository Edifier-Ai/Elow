package com.elow.app.ui.gl

import com.elow.app.core.model.ItemType
import com.elow.app.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ElowStageSceneTest {
    @Test
    fun addObjectSceneUsesItemSpecificDisplayAmount() {
        val cola = ElowStageScene.AddObject(ItemType.COLA, 0.66)
        val wine = ElowStageScene.AddObject(ItemType.WINE, 0.60)

        assertEquals(330, cola.displayAmountMl)
        assertEquals(150, wine.displayAmountMl)
    }

    @Test
    fun homeAlcoholSceneContainsWineAndBeer() {
        val scene = ElowStageScene.HomeAlcohol

        assertTrue(scene.items.contains(ItemType.WINE))
        assertTrue(scene.items.contains(ItemType.BEER))
    }

    @Test
    fun stageQuadScalePreservesArtworkRatioOnTallS25UltraViewport() {
        val scale = stageQuadScaleForViewport(width = 1440, height = 3120)
        val drawnRatio = (1440f * scale.x) / (3120f * scale.y)

        assertEquals(1f, scale.x, 0.0001f)
        assertTrue(scale.y < 1f)
        assertEquals(420f / 260f, drawnRatio, 0.0001f)
    }

    @Test
    fun stageQuadScalePreservesArtworkRatioInsideWideCards() {
        val scale = stageQuadScaleForViewport(width = 860, height = 300)
        val drawnRatio = (860f * scale.x) / (300f * scale.y)

        assertTrue(scale.x < 1f)
        assertEquals(1f, scale.y, 0.0001f)
        assertEquals(420f / 260f, drawnRatio, 0.0001f)
    }

    @Test
    fun stageSurfaceAssetsFillTheirTargetBounds() {
        assertEquals(StageAssetScale.FillBounds, stageAssetScaleFor(R.drawable.elow_shelf_plank))
        assertEquals(StageAssetScale.FillBounds, stageAssetScaleFor(R.drawable.elow_pedestal_oval))
        assertEquals(StageAssetScale.FillBounds, stageAssetScaleFor(R.drawable.elow_contact_shadow))
    }

    @Test
    fun stageObjectAssetsPreserveTheirCutoutRatio() {
        assertEquals(StageAssetScale.Fit, stageAssetScaleFor(R.drawable.elow_drink_cola))
        assertEquals(StageAssetScale.Fit, stageAssetScaleFor(R.drawable.elow_drink_wine))
        assertEquals(StageAssetScale.Fit, stageAssetScaleFor(R.drawable.elow_sugar_jar))
    }
}
