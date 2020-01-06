package com.qicode.beziermask

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.WHITE
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Region
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import com.qicode.arcprogressbar.ArcProgressBar
import com.qicode.bubbleview.BubbleLayoutManager
import com.qicode.extension.TAG
import com.qicode.extension.dp

class BezierMaskView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defAttr: Int = 0) : ConstraintLayout(context, attrs, defAttr) {

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

    /**
     * 需要排除的气泡半径
     */
    var excludeRadius = 20.dp
        set(value) {
            field = value
            updateBubble()
        }

    private val path = Path()
    private val paint = Paint()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BezierMaskView)
        smooth = typedArray.getFloat(R.styleable.BezierMaskView_smooth, smooth)
        groundHeight = typedArray.getDimension(R.styleable.BezierMaskView_groundHeight, groundHeight)
        groundSize = typedArray.getDimension(R.styleable.BezierMaskView_groundSize, groundSize)
        groundColor = typedArray.getColor(R.styleable.BezierMaskView_groundColor, groundColor)
        excludeRadius = typedArray.getDimension(R.styleable.BezierMaskView_excludeRadius, excludeRadius)
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

    /**
     * 在所有布局反正之后，通知BubbleLayoutManager更新布局
     */
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) updateBubble()
    }

    private fun drawGround(canvas: Canvas?) {
        path.reset()
        getPath(path)
        canvas?.drawPath(path, paint)
    }

    private fun getPath(path: Path, excludeRadius: Float = 0f): Path {
        val ground = measuredHeight - groundHeight - excludeRadius
        val top = ground - groundSize
        val percent = 0.5f
        path.reset()
        path.moveTo(0f, measuredHeight.toFloat())
        path.lineTo(0f, ground)
        path.quadTo(measuredWidth * (percent - smooth), top, measuredWidth * 0.5f, top)
        path.quadTo(measuredWidth * (percent + smooth), top, measuredWidth.toFloat(), ground)
        path.lineTo(measuredWidth.toFloat(), measuredHeight.toFloat())
        path.close()
        return path
    }

    private fun updateBubble() {
        // 优先获取manager
        var manager: BubbleLayoutManager? = null
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            if (child is RecyclerView && child.layoutManager is BubbleLayoutManager) {
                manager = child.layoutManager as BubbleLayoutManager
                Log.i(TAG(), "find BubbleLayoutManager")
                break
            }
        }
        // 如果存在BubbleLayoutManager， 排除所有子View的path
        manager?.also {
            for (index in 0 until childCount) {
                val child = getChildAt(index)
                // 排除带有BubbleLayoutManager的child
                if (child is RecyclerView && child.layoutManager is BubbleLayoutManager) continue
                // 判断是否有getPath方法，如果有直接调用getPath获取排除区域，否则按照矩形处理
                if (child is ArcProgressBar) {
                    val region = Region()
                    path.reset()
                    region.set(0, 0, measuredWidth, measuredHeight)
                    region.setPath(child.getPath(path, excludeRadius), region)
                    it.exclude(region)
                    Log.i(TAG(), "child contains getPath method, use it's getPath region")
                } else {
                    it.exclude(child, excludeRadius.toInt())
                    Log.i(TAG(), "child doesn't contains getPath method, use it's default rect region")
                }
            }
            // 最后加上自己的path
            val region = Region()
            path.reset()
            region.set(0, 0, measuredWidth, measuredHeight)
            region.setPath(getPath(path, excludeRadius), region)
            it.exclude(region)
            Log.i(TAG(), "set self's region")
            // 通知BubbleLayoutManager更新布局
            it.requestLayout()
        }
    }
}