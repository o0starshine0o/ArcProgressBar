package com.qicode.bubbleview

import android.graphics.Path
import android.graphics.Rect
import android.graphics.Region
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import java.util.*
import kotlin.math.max

class BubbleLayoutManager(private val maxRandomTimes: Int = 100) : RecyclerView.LayoutManager() {
    private var availableRegion: Region = Region()
    private var excludeRegions: MutableList<Region> = MutableList(0) { Region() }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        if (recycler == null || itemCount <= 0 || state?.isPreLayout == true) return
        // 缓存所有view
        detachAndScrapAttachedViews(recycler)
        // 重置绘制区域
        availableRegion.set(0, 0, width, height)
        // 减去所有不可用区域
        excludeAllRegions()
        // 遍历所有的item
        for (i in 0 until itemCount) {
            // 获取本次的childView
            val childView = recycler.getViewForPosition(i)
            // 获取放置区域
            getAvailableRect(childView)?.apply {
                addView(childView)
                layoutDecoratedWithMargins(childView, left, top, right, bottom)
                updateExcludeRegion(this, max(childView.measuredHeight, childView.measuredWidth))
            }
        }
    }

    /**
     * 需要排除的区域
     * 为了提高效率，在所有区域都排除完成之后需要调用requestLayout()
     */
    fun exclude(view: View?, radius: Int = 0) {
        view?.apply {
            val region = Region(left -radius, top-radius, right+radius, bottom+radius
            )
            exclude(region)
        }
    }

    /**
     * 需要排除的区域
     * 为了提高效率，在所有区域都排除完成之后需要调用requestLayout()
     */
    fun exclude(region: Region) {
        excludeRegions.add(region)
    }

    private fun getAvailableRect(view: View): Rect? {
        measureChild(view, 0, 0)
        // 获取view的宽高
        val viewWidth = getDecoratedMeasuredWidth(view)
        val viewHeight = getDecoratedMeasuredHeight(view)
        for (i in 0..maxRandomTimes) {
            // 获取随机坐标,作为view的锚点(中心)
            val randomX = Random().nextInt(width - viewWidth + 1 ) + viewWidth / 2
            val randomY = Random().nextInt(height - viewHeight + 1 ) + viewHeight / 2
            // 如果获取到可用的坐标, 返回可用区域
            if (availableRegion.contains(randomX, randomY)) {
                Log.i("BubbleLayoutManager", "try $i times, find a available region to put child view")
                return Rect().apply {
                    left = randomX - viewWidth / 2
                    top = randomY - viewHeight / 2
                    right = randomX + viewWidth / 2
                    bottom = randomY + viewHeight /2
                }
            }
        }
        Log.e("BubbleLayoutManager", "try $maxRandomTimes times, can not find a available region to put child view")
        return null
    }

    /**
     * 排除所有 {@code excludeRegions} 设置的区域
     */
    private fun excludeAllRegions() {
        for (region in excludeRegions) {
            availableRegion.op(region, Region.Op.DIFFERENCE)
        }
    }

    /**
     * 用于在layoutChildren时每当添加一个child，就需要把这个child的区域设置为不可使用
     * 由于只能测量点是否在区域内，所以需要额外的半径参数来扩大被排除的区域
     *
     * @param rect 目前目标区域
     * @param radius 额外需要添加的半径
     */
    private fun updateExcludeRegion(rect: Rect, radius: Int) {
        val region = Region(Rect(rect.left - radius / 2, rect.top - radius / 2, rect.right + radius / 2, rect.bottom + radius / 2))
        val path = Path()
        path.addCircle(rect.centerX().toFloat(), rect.centerY().toFloat(), max(rect.width(), rect.height()).toFloat() + radius, Path.Direction.CW)
        region.setPath(path, region)
        availableRegion.op(region, Region.Op.DIFFERENCE)
    }
}