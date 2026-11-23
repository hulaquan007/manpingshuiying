package com.example.watermark

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.view.View

/** 居中显示图片，并在其上实时预览水印（与导出使用同一个渲染器）。 */
class PreviewView(context: Context) : View(context) {

    var bitmap: Bitmap? = null
        set(value) {
            field = value
            invalidate()
        }

    var params: WatermarkParams = WatermarkParams()
        set(value) {
            field = value
            invalidate()
        }

    private val dest = RectF()

    init {
        setBackgroundColor(Color.parseColor("#EEEEEE"))
    }

    override fun onDraw(canvas: Canvas) {
        val bmp = bitmap ?: return
        val scale = minOf(width.toFloat() / bmp.width, height.toFloat() / bmp.height)
        val dw = bmp.width * scale
        val dh = bmp.height * scale
        val left = (width - dw) / 2f
        val top = (height - dh) / 2f
        dest.set(left, top, left + dw, top + dh)

        canvas.drawBitmap(bmp, null, dest, null)

        canvas.save()
        canvas.clipRect(dest)
        canvas.translate(left, top)
        canvas.scale(scale, scale)
        WatermarkRenderer.draw(canvas, bmp.width.toFloat(), bmp.height.toFloat(), params)
        canvas.restore()
    }
}
