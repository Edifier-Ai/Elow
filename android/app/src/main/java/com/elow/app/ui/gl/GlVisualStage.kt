package com.elow.app.ui.gl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun GlVisualStage(
    scene: ElowStageScene,
    modifier: Modifier = Modifier
) {
    val initialScene = remember { scene }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ElowStageGlView(context).apply {
                update(initialScene)
            }
        },
        update = { view ->
            view.update(scene)
        }
    )
}
