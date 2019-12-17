package com.qicode.arcprogressbar

import android.graphics.PointF
import android.graphics.RectF
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class DrawString(var string: String, var color: Int, var angle: Float, var percent: Float, var size: Float) {
    fun getPosition(rect: RectF): PointF {
        return PointF().apply {
            x = percent * rect.width() * 0.5f * sin(angle * PI.toFloat() / 180) + rect.centerX()
            y = percent * rect.height() * 0.5f * cos(angle * PI.toFloat() / 180) + rect.centerY()
        }
    }
}