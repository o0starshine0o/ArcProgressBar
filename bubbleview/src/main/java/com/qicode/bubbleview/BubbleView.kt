package com.qicode.bubbleview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.qicode.extension.TAG

class BubbleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {
    /**
     * 是否绘制可以放置气泡的区域
     */
    var isDrawRegion = false
    /**
     * 绘制可以放置气泡的区域的颜色
     */
    var drawColor = Color.argb(100, 100, 100, 100)
    /**
     * 被排除气泡边缘半径
     */
    var excludeRadius = 20f
        set(value) {
            field = value
            updateBubble()
        }

    private val rect by lazy { Rect() }
    private val paint by lazy {
        Paint().apply {
            color = drawColor
            isAntiAlias = true
            isDither = true
            isFilterBitmap = true
        }
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BubbleView)
        isDrawRegion = typedArray.getBoolean(R.styleable.BubbleView_drawRegion, isDrawRegion)
        drawColor = typedArray.getColor(R.styleable.BubbleView_regionColor, drawColor)
        excludeRadius = typedArray.getDimension(R.styleable.BubbleView_excludeRadius, excludeRadius)
        typedArray.recycle()
        // 初始化完成后，更新一次气泡位置
        post { updateBubble() }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (isDrawRegion) (layoutManager as? BubbleLayoutManager)?.availableRegion?.apply {
            val regionIterator = RegionIterator(this)
            while (regionIterator.next(rect)) {
                canvas.drawRect(rect, paint)
            }
        }
        super.onDraw(canvas)
    }

    /**
     * 更新气泡的位置，重新计算可以放置气泡的区域包含：
     * 1、所有的同级view
     * 2、父view
     */
    fun updateBubble() {
        // 如果被BubbleLayoutManager支配children的位置， 排除所有同级View的path
        (layoutManager as? BubbleLayoutManager)?.also { manager ->
            manager.clearBubble()
            val path = Path()
            val group = parent as ViewGroup
            for (index in 0 until group.childCount) {
                val child = group.getChildAt(index)
                if (child?.visibility == View.GONE) continue
                updateBubble(manager, child, path)
            }
            if (group is BubbleHelp) updateBubble(manager, group, path)
            manager.requestLayout()
        }
    }

    private fun updateBubble(manager: BubbleLayoutManager, view: View, path: Path) {
        when (view) {
            is BubbleView -> Log.i(TAG(), "child contains is bubbleview, continue")
            is BubbleHelp -> manager.exclude(Region().apply {
                path.reset()
                set(0, 0, measuredWidth, measuredHeight)
                setPath(view.getPath(path, excludeRadius), this)
                Log.i(TAG(), "child contains getPath method, use it's getPath region")
            })
            else -> {
                manager.exclude(view, excludeRadius.toInt())
                Log.i(TAG(), "child doesn't contains getPath method, use it's default rect region")
            }
        }
    }
}