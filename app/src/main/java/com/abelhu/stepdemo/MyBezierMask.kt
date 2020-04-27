package com.abelhu.stepdemo

import android.content.Context
import android.graphics.Path
import android.util.AttributeSet
import com.qicode.beziermask.BezierMaskView
import com.qicode.bubbleview.BubbleHelp

class MyBezierMask(context: Context, attrs: AttributeSet?, defAttr: Int) : BezierMaskView(context, attrs, defAttr), BubbleHelp {
    override fun getPath(path: Path, excludeRadius: Float) = updatePath(path, excludeRadius)
}