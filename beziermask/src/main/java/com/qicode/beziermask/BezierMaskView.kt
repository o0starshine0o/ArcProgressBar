package com.qicode.beziermask

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.WHITE
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

class BezierMaskView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    /**
     * 底部矩形区域的高度
     */
    var groundHeight = 20f
        set(value) {
            field = value
            postInvalidate()
        }
    /**
     * 底部弧形的高度（除去矩形的高度）
     */
    var groundSize = 60f
        set(value) {
            field = value
            postInvalidate()
        }
    /**
     * 底部的颜色
     */
    var groundColor = WHITE
        set(value) {
            field = value
            postInvalidate()
        }
    /**
     * 弧形的圆滑程度
     */
    var smooth = 0.2f
        set(value) {
            field = value
            postInvalidate()
        }

    private val path = Path()
    private val paint = Paint()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BezierMaskView)
        smooth = typedArray.getFloat(R.styleable.BezierMaskView_smooth, smooth)
        groundHeight = typedArray.getDimension(R.styleable.BezierMaskView_groundHeight, groundHeight)
        groundSize = typedArray.getDimension(R.styleable.BezierMaskView_groundSize, groundSize)
        groundColor = typedArray.getColor(R.styleable.BezierMaskView_groundColor, groundColor)
        typedArray.recycle()

        paint.color = groundColor
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.isAntiAlias = true
        paint.isDither = true
        paint.isFilterBitmap = true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawGround(canvas)
    }

    private fun drawGround(canvas: Canvas?) {
        val ground = measuredHeight - groundHeight
        val top = measuredHeight - groundHeight - groundSize
        val percent = 0.5f
        path.reset()
        path.moveTo(0f, measuredHeight.toFloat())
        path.lineTo(0f, ground)
        path.quadTo(measuredWidth * (percent - smooth), top, measuredWidth * 0.5f, top)
        path.quadTo(measuredWidth * (percent + smooth), top, measuredWidth.toFloat(), ground)
        path.lineTo(measuredWidth.toFloat(), measuredHeight.toFloat())
        path.close()
        canvas?.drawPath(path, paint)
    }
}