package com.lmt.global.base.view.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.lmt.global.base.R

/** Keyboard panel shadow: x=0, y=-6dp, blur=24dp, spread=0, black at 8%. */
class TopShadowLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val inset = resources.getDimensionPixelSize(R.dimen._48dp)
    private val offsetY = -resources.getDimension(R.dimen._6dp)
    private val bitmapPaint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.keyboard_panel_shadow)
        val sigma = resources.getDimension(R.dimen._24dp) / 2f
        maskFilter = BlurMaskFilter((sigma - 0.5f) / 0.57735f, BlurMaskFilter.Blur.NORMAL)
    }
    private var shadow: Bitmap? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        shadow = null
    }

    override fun draw(canvas: Canvas) {
        if (width > 0 && height > 0) {
            // Cache only the top strip instead of a bitmap of the entire keyboard.
            val bitmap = shadow ?: Bitmap.createBitmap(
                width + 2 * inset, 2 * inset, Bitmap.Config.ARGB_8888,
            ).also {
                Canvas(it).drawRect(
                    inset.toFloat(), inset + offsetY,
                    inset + width.toFloat(), inset + height + offsetY, shadowPaint,
                )
                shadow = it
            }
            val checkpoint = canvas.save()
            canvas.clipRect(0f, -inset.toFloat(), width.toFloat(), 0f)
            canvas.drawBitmap(bitmap, -inset.toFloat(), -inset.toFloat(), bitmapPaint)
            canvas.restoreToCount(checkpoint)
        }
        super.draw(canvas)
    }

    override fun onDetachedFromWindow() {
        shadow = null
        super.onDetachedFromWindow()
    }
}
