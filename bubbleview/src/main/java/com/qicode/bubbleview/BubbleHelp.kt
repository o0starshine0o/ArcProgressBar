package com.qicode.bubbleview

import android.graphics.Path

interface BubbleHelp {
    fun getPath(path: Path, excludeRadius: Float = 0f):Path
}