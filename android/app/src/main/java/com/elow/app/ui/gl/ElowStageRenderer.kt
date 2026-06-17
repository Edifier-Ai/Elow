package com.elow.app.ui.gl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import com.elow.app.R
import com.elow.app.core.model.ItemType
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

private const val StageWidth = 420f
private const val StageHeight = 260f
private const val StageTextureScale = 2f
private const val StageAspectRatio = StageWidth / StageHeight

internal data class StageQuadScale(
    val x: Float,
    val y: Float
)

internal enum class StageAssetScale {
    Fit,
    FillBounds
}

internal fun stageQuadScaleForViewport(width: Int, height: Int): StageQuadScale {
    if (width <= 0 || height <= 0) return StageQuadScale(1f, 1f)

    val viewportAspectRatio = width.toFloat() / height.toFloat()
    return if (viewportAspectRatio > StageAspectRatio) {
        StageQuadScale(x = StageAspectRatio / viewportAspectRatio, y = 1f)
    } else {
        StageQuadScale(x = 1f, y = viewportAspectRatio / StageAspectRatio)
    }
}

internal fun stageAssetScaleFor(resId: Int): StageAssetScale =
    when (resId) {
        R.drawable.elow_shelf_plank,
        R.drawable.elow_pedestal_oval,
        R.drawable.elow_contact_shadow -> StageAssetScale.FillBounds
        else -> StageAssetScale.Fit
    }

class ElowStageRenderer(context: Context) : GLSurfaceView.Renderer {
    private val painter = StageBitmapPainter(context)
    private var scene: ElowStageScene = ElowStageScene.HomeSugar
    private var dirty = true
    private var program = 0
    private var textureId = 0
    private var bitmap: Bitmap? = null
    private val vertices: FloatBuffer = ByteBuffer.allocateDirect(16 * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply {
            put(floatArrayOf(-1f, -1f, 0f, 1f, 1f, -1f, 1f, 1f, -1f, 1f, 0f, 0f, 1f, 1f, 1f, 0f))
            position(0)
        }

    fun update(scene: ElowStageScene) {
        if (this.scene != scene) {
            this.scene = scene
            dirty = true
        }
    }

    override fun onSurfaceCreated(gl: javax.microedition.khronos.opengles.GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(1f, 1f, 1f, 1f)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        program = createProgram()
        textureId = createTexture()
        dirty = true
    }

    override fun onSurfaceChanged(gl: javax.microedition.khronos.opengles.GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width.coerceAtLeast(1), height.coerceAtLeast(1))
        updateStageQuad(stageQuadScaleForViewport(width, height))
    }

    override fun onDrawFrame(gl: javax.microedition.khronos.opengles.GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        if (dirty || bitmap == null) {
            val target = bitmap ?: Bitmap.createBitmap(
                (StageWidth * StageTextureScale).toInt(),
                (StageHeight * StageTextureScale).toInt(),
                Bitmap.Config.ARGB_8888
            ).also { bitmap = it }
            painter.draw(target, scene)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, target, 0)
            dirty = false
        }
        drawTexture()
    }

    private fun drawTexture() {
        GLES20.glUseProgram(program)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        val position = GLES20.glGetAttribLocation(program, "aPosition")
        val texCoord = GLES20.glGetAttribLocation(program, "aTexCoord")
        val sampler = GLES20.glGetUniformLocation(program, "uTexture")

        vertices.position(0)
        GLES20.glVertexAttribPointer(position, 2, GLES20.GL_FLOAT, false, 16, vertices)
        GLES20.glEnableVertexAttribArray(position)
        vertices.position(2)
        GLES20.glVertexAttribPointer(texCoord, 2, GLES20.GL_FLOAT, false, 16, vertices)
        GLES20.glEnableVertexAttribArray(texCoord)
        GLES20.glUniform1i(sampler, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(position)
        GLES20.glDisableVertexAttribArray(texCoord)
    }

    private fun updateStageQuad(scale: StageQuadScale) {
        vertices.position(0)
        vertices.put(
            floatArrayOf(
                -scale.x, -scale.y, 0f, 1f,
                scale.x, -scale.y, 1f, 1f,
                -scale.x, scale.y, 0f, 0f,
                scale.x, scale.y, 1f, 0f
            )
        )
        vertices.position(0)
    }

    private fun createTexture(): Int {
        val ids = IntArray(1)
        GLES20.glGenTextures(1, ids, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ids[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        return ids[0]
    }

    private fun createProgram(): Int {
        val vertex = shader(
            GLES20.GL_VERTEX_SHADER,
            """
            attribute vec2 aPosition;
            attribute vec2 aTexCoord;
            varying vec2 vTexCoord;
            void main() {
                gl_Position = vec4(aPosition, 0.0, 1.0);
                vTexCoord = aTexCoord;
            }
            """.trimIndent()
        )
        val fragment = shader(
            GLES20.GL_FRAGMENT_SHADER,
            """
            precision mediump float;
            uniform sampler2D uTexture;
            varying vec2 vTexCoord;
            void main() {
                gl_FragColor = texture2D(uTexture, vTexCoord);
            }
            """.trimIndent()
        )
        return GLES20.glCreateProgram().also { programId ->
            GLES20.glAttachShader(programId, vertex)
            GLES20.glAttachShader(programId, fragment)
            GLES20.glLinkProgram(programId)
        }
    }

    private fun shader(type: Int, code: String): Int =
        GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, code)
            GLES20.glCompileShader(shader)
        }
}

private class StageBitmapPainter(context: Context) {
    private val resources = context.resources
    private val assets = mutableMapOf<Int, Bitmap>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val rect = RectF()

    fun draw(bitmap: Bitmap, scene: ElowStageScene) {
        bitmap.eraseColor(Color.TRANSPARENT)
        val canvas = Canvas(bitmap)
        canvas.save()
        canvas.scale(StageTextureScale, StageTextureScale)
        stageBackground(canvas)
        when (scene) {
            ElowStageScene.OnboardingShelf -> onboardingShelf(canvas)
            ElowStageScene.HomeSugar -> homeSugar(canvas)
            ElowStageScene.HomeAlcohol -> homeAlcohol(canvas)
            is ElowStageScene.AddObject -> addObject(canvas, scene)
            ElowStageScene.HonorCollectibles -> honorCollectibles(canvas)
        }
        canvas.restore()
    }

    private fun stageBackground(canvas: Canvas) {
        paint.shader = LinearGradient(0f, 0f, 0f, StageHeight, Color.WHITE, 0xFFFAFBFA.toInt(), Shader.TileMode.CLAMP)
        rect.set(0f, 0f, StageWidth, StageHeight)
        canvas.drawRect(rect, paint)
        paint.shader = null
    }

    private fun onboardingShelf(canvas: Canvas) {
        shelf(canvas, 20f, 130f, 380f, 36f)
        drink(canvas, ItemType.COLA, 38f, 28f, 62f, 126f)
        drink(canvas, ItemType.MILK_TEA, 132f, 26f, 78f, 128f)
        drink(canvas, ItemType.BEER, 224f, 32f, 64f, 120f)
        drink(canvas, ItemType.WINE, 318f, 24f, 58f, 132f)
        shelf(canvas, 20f, 226f, 380f, 32f)
        image(canvas, R.drawable.elow_sugar_jar, 82f, 164f, 72f, 86f)
        image(canvas, R.drawable.elow_coin_stack, 248f, 178f, 112f, 70f)
    }

    private fun homeSugar(canvas: Canvas) {
        ambientShadow(canvas, 98f, 220f, 224f, 26f, 0.16f)
        image(canvas, R.drawable.elow_pedestal_oval, 104f, 202f, 210f, 46f)
        image(canvas, R.drawable.elow_sugar_jar, 126f, 34f, 166f, 186f)
    }

    private fun homeAlcohol(canvas: Canvas) {
        ambientShadow(canvas, 90f, 222f, 240f, 26f, 0.16f)
        image(canvas, R.drawable.elow_pedestal_oval, 96f, 204f, 228f, 46f)
        drink(canvas, ItemType.WINE, 112f, 42f, 106f, 172f)
        drink(canvas, ItemType.BEER, 208f, 76f, 98f, 136f)
    }

    private fun addObject(canvas: Canvas, scene: ElowStageScene.AddObject) {
        val frame = when (scene.itemType) {
            ItemType.COLA -> RectF(134f, 16f, 286f, 248f)
            ItemType.MILK_TEA -> RectF(112f, 16f, 308f, 248f)
            ItemType.BEER -> RectF(132f, 24f, 288f, 244f)
            ItemType.WINE -> RectF(84f, 8f, 336f, 252f)
        }
        ambientShadow(canvas, 98f, 232f, 224f, 26f, 0.18f)
        image(canvas, R.drawable.elow_pedestal_oval, 104f, 214f, 212f, 42f)
        drink(canvas, scene.itemType, frame.left, frame.top, frame.width(), frame.height())
    }

    private fun honorCollectibles(canvas: Canvas) {
        shelf(canvas, 24f, 204f, 372f, 42f)
        drink(canvas, ItemType.COLA, 36f, 44f, 62f, 170f)
        drink(canvas, ItemType.MILK_TEA, 116f, 36f, 82f, 176f)
        drink(canvas, ItemType.BEER, 204f, 54f, 72f, 158f)
        drink(canvas, ItemType.WINE, 286f, 36f, 62f, 176f)
        image(canvas, R.drawable.elow_locked_bottle, 360f, 66f, 42f, 138f)
        questionMark(canvas, 381f, 151f)
    }

    private fun drink(canvas: Canvas, itemType: ItemType, x: Float, y: Float, w: Float, h: Float) {
        image(canvas, R.drawable.elow_contact_shadow, x + w * 0.04f, y + h - 16f, w * 0.92f, 18f)
        image(canvas, itemType.assetResId(), x, y, w, h)
    }

    private fun shelf(canvas: Canvas, x: Float, y: Float, w: Float, h: Float) {
        ambientShadow(canvas, x + 20f, y + h - 2f, w - 40f, h * 0.28f, 0.08f)
        image(canvas, R.drawable.elow_shelf_plank, x, y, w, h)
    }

    private fun ambientShadow(canvas: Canvas, x: Float, y: Float, w: Float, h: Float, alpha: Float) {
        paint.shader = null
        paint.color = Color.argb((255 * alpha).toInt(), 40, 48, 55)
        rect.set(x, y, x + w, y + h)
        canvas.drawOval(rect, paint)
        paint.alpha = 255
    }

    private fun questionMark(canvas: Canvas, x: Float, baseline: Float) {
        paint.shader = null
        paint.color = 0xFF8F918B.toInt()
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 48f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("?", x, baseline, paint)
        paint.typeface = null
        paint.textAlign = Paint.Align.LEFT
    }

    private fun image(canvas: Canvas, resId: Int, x: Float, y: Float, w: Float, h: Float) {
        val bitmap = assets.getOrPut(resId) { BitmapFactory.decodeResource(resources, resId) }
        paint.alpha = 255
        paint.shader = null
        paint.colorFilter = null
        if (stageAssetScaleFor(resId) == StageAssetScale.FillBounds) {
            rect.set(x, y, x + w, y + h)
        } else {
            val scale = minOf(w / bitmap.width, h / bitmap.height)
            val drawW = bitmap.width * scale
            val drawH = bitmap.height * scale
            rect.set(
                x + (w - drawW) / 2f,
                y + (h - drawH) / 2f,
                x + (w + drawW) / 2f,
                y + (h + drawH) / 2f
            )
        }
        canvas.drawBitmap(bitmap, null, rect, paint)
    }
}

private fun ItemType.assetResId(): Int =
    when (this) {
        ItemType.COLA -> R.drawable.elow_drink_cola
        ItemType.MILK_TEA -> R.drawable.elow_drink_milk_tea
        ItemType.BEER -> R.drawable.elow_drink_beer
        ItemType.WINE -> R.drawable.elow_drink_wine
    }
