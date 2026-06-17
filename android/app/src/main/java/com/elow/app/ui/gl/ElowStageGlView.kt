package com.elow.app.ui.gl

import android.content.Context
import android.opengl.GLSurfaceView

class ElowStageGlView(context: Context) : GLSurfaceView(context) {
    private val stageRenderer = ElowStageRenderer(context.applicationContext)

    init {
        setEGLContextClientVersion(2)
        preserveEGLContextOnPause = true
        setRenderer(stageRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
        isClickable = false
        isFocusable = false
    }

    fun update(scene: ElowStageScene) {
        stageRenderer.update(scene)
        requestRender()
    }
}
