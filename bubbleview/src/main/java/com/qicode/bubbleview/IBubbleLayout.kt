package com.qicode.bubbleview

import android.graphics.Rect

interface IBubbleLayout {
    fun getRect(index:Int): Rect?
    fun setRect(index:Int, rect: Rect)
}