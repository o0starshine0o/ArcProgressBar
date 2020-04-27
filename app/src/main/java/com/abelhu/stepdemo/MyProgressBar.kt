package com.abelhu.stepdemo

import android.content.Context
import android.graphics.Path
import android.util.AttributeSet
import com.qicode.arcprogressbar.ArcProgressBar
import com.qicode.bubbleview.BubbleHelp

class MyProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ArcProgressBar(context, attrs, defStyleAttr), BubbleHelp {
    /**
     * 需要获取的是在父控件中裁剪的区域，所以这个path更改为在父控件中的坐标
     */
    override fun getPath(path: Path, excludeRadius: Float) = Path().apply {
        addCircle((left + right).toFloat() / 2, (top + bottom).toFloat() / 2, scaleOutsideSpecialRadius + excludeRadius, Path.Direction.CW)
    }
}