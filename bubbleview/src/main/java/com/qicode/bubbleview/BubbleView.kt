package com.qicode.bubbleview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

class BubbleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {
    var isDrawRegion = false
    var drawColor = Color.argb(100, 100, 100, 100)

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
        typedArray.recycle()
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
}