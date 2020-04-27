package com.qicode.bubbleview

import android.graphics.Rect

interface BubbleLayoutProxy {
    /**
     * 返回被缓存的气泡的位置，保持气泡的位置不变
     */
    fun getRect(index:Int): Rect?
    /**
     * 需要记录气泡的位置，用于保持气泡的位置不变
     */
    fun setRect(index:Int, rect: Rect)
    /**
     * 清除气泡的位置信息，为了重新进行气泡位置的放置
     */
    fun clear()
}