package com.example.watermark

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.hypot

/** 水印参数。sizePct / spacing / alpha 都是相对值，保证预览与导出效果一致。 */
data class WatermarkParams(
    val text: String = "仅供办理业务使用",
    val sizePct: Int = 5,      // 字号 = 图片短边 * sizePct%
    val alpha: Int = 40,       // 0..100
    val angle: Int = -30,      // -90..90 度
    val spacing: Int = 30,     // 0..100，间距
    val color: Int = Color.WHITE
)

object WatermarkRenderer {

    /** 在 (0,0)-(w,h) 的区域内铺满水印。 */
    fun draw(canvas: Canvas, w: Float, h: Float, p: WatermarkParams) {
        if (p.text.isBlank() || w <= 0f || h <= 0f) return

        val textSize = (minOf(w, h) * p.sizePct / 100f).coerceAtLeast(8f)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.textSize = textSize
            color = p.color
            alpha = (p.alpha.coerceIn(0, 100) * 255 / 100)
            // 轻微阴影，让白色水印在浅色图上也能看清
            val shadow = if (p.color == Color.BLACK) Color.WHITE else Color.BLACK
            setShadowLayer(textSize * 0.04f, 0f, 0f, shadow)
        }

        val fm = paint.fontMetrics
        val textW = paint.measureText(p.text)
        val textH = fm.descent - fm.ascent
        val gap = textSize * (0.5f + p.spacing / 25f)
        val stepX = textW + gap
        val stepY = textH + gap
        if (stepX <= 0f || stepY <= 0f) return

        val cx = w / 2f
        val cy = h / 2f
        val half = hypot(w, h) / 2f + stepX // 旋转后依然铺满四角

        canvas.save()
        canvas.rotate(p.angle.toFloat(), cx, cy)

        var row = 0
        var y = cy - half
        while (y < cy + half) {
            // 奇数行错开半格，更像常见的满屏水印
            var x = cx - half - if (row % 2 == 1) stepX / 2f else 0f
            while (x < cx + half) {
                canvas.drawText(p.text, x, y - fm.ascent, paint)
                x += stepX
            }
            y += stepY
            row++
        }
        canvas.restore()
    }
}
