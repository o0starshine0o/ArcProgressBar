package com.qicode.arcprogressbar

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Canvas.ALL_SAVE_FLAG
import android.graphics.Color.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.*


class ArcProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    /**
     * 线条数
     */
    var scaleCount = 59
    /**
     * 刻度线进度最小值
     */
    var progressMin = 1
    /**
     * 刻度线进度最大值
     */
    var progressMax = 15000
    /**
     * 刻度线目前进度值
     */
    var progress = 6666
        set(value) {
            if (value != field) {
                field = value
                titleDesc?.apply { string = field.toString() }
                postInvalidate()
            }
        }
    /**
     * 刻度线目前进度的百分比
     */
    private val progressPercent
        get() = max(min((progress - progressMin).toFloat() / (progressMax - progressMin), 1f), 0f)
    /**
     * 进度条进度最小值
     */
    var subProgressMin = 1
    /**
     * 进度条进度最大值
     */
    var subProgressMax = 15000
    /**
     * 进度条目前进度值
     */
    var subProgress = 6363
        set(value) {
            if (value != field) {
                field = value
                subTitleDesc?.apply { string = field.toString() }
                postInvalidate()
            }
        }
    /**
     * 进度条目前进度的百分比
     */
    private val subProgressPercent
        get() = max(min((subProgress - subProgressMin).toFloat() / (subProgressMax - subProgressMin), 1f), 0f)
    /**
     * 画笔
     */
    private var paint = Paint()
    /**
     * 画笔宽度
     */
    var paintWidth = 10f
    /**
     * 画笔颜色
     */
    var paintColor = WHITE
    /**
     * 刻度线内侧半径百分比
     */
    var scaleInsidePercent = 0.8f
    /**
     * 刻度线外侧半径百分比
     */
    var scaleOutsidePercent = 0.88f
    /**
     * 刻度线外侧特殊半径百分比
     */
    var scaleOutsideSpecialPercent = 0.91f
    /**
     * 刻度线内侧半径
     */
    private var scaleInsideRadius = 0.0f
    /**
     * 刻度线外侧半径
     */
    private var scaleOutsideRadius = 0.0f
    /**
     * 刻度线外侧特殊半径
     */
    private var scaleOutsideSpecialRadius = 0.0f
    /**
     * 进度条半径百分比
     */
    var progressBarPercent = 0.75f
    /**
     * 进度条半径
     */
    private var progressBarRadius = 0.0f
    /**
     * 刻度线外侧特殊半径,指明那几个刻度线需要特殊化处理
     */
    var specialScales: List<Int>? = null

    /**
     * 刻度线图片
     */
    private var scaleBitmap: Bitmap? = null
    /**
     * 进度条图片
     */
    private var progressBarBitmap: Bitmap? = null
    /**
     * 渐变色图片
     */
    private var colorBitmap: Bitmap? = null
    /**
     * 刻度线画布
     */
    private var scaleCanvas: Canvas? = null
    /**
     * 刻度线画布
     */
    private var progressBarCanvas: Canvas? = null
    /**
     * 渐变色画布
     */
    private var colorCanvas: Canvas? = null
    /**
     * 可使用的矩形区域
     */
    private var rect = RectF()
    /**
     * 是否显示坐标
     */
    var showCoordinate = false
    /**
     * 是否显示渐变色
     */
    var showColorGradient = false
    /**
     * 是否显示刻度值
     */
    var showScaleValue = true
    /**
     * 刻度值文本大小
     */
    var scaleValueSize = 40f
    /**
     * 刻度值文本颜色
     */
    var scaleValueColor = WHITE
    /**
     * 刻度值文本距中心距离百分比
     */
    var scaleValuePercent = 0.62f
    /**
     * 主标题
     */
    var title: DrawString? = DrawString("主标题", BLACK, 180f, 0.5f, 40f)
    /**
     * 主标题描述
     */
    var titleDesc: DrawString? = DrawString("主标题描述", BLACK, 180f, 0.5f, 40f)
    /**
     * 副标题
     */
    var subTitle: DrawString? = DrawString("副标题", BLACK, 180f, 0.5f, 40f)
    /**
     * 副标题描述
     */
    var subTitleDesc: DrawString? = DrawString("副标题描述", BLACK, 180f, 0.5f, 40f)

    /**
     * 背景渐变色
     */
    var backColors: IntArray? = context.resources.getIntArray(R.array.color_gradient)
    /**
     * 背景渐变色位置分布
     */
    var backColorPositions: FloatArray? = context.resources.getStringArray(R.array.position_gradient).map { it.toFloat() }.toFloatArray()

    init {
        // 获取定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar)
        scaleCount = typedArray.getInteger(R.styleable.ArcProgressBar_scaleCount, scaleCount)
        scaleInsidePercent = typedArray.getFraction(R.styleable.ArcProgressBar_scaleInsidePercent, 1, 1, scaleInsidePercent)
        scaleOutsidePercent = typedArray.getFraction(R.styleable.ArcProgressBar_scaleOutsidePercent, 1, 1, scaleOutsidePercent)
        scaleOutsideSpecialPercent = typedArray.getFraction(R.styleable.ArcProgressBar_scaleOutsideSpecialPercent, 1, 1, scaleOutsideSpecialPercent)
        scaleValueSize = typedArray.getDimension(R.styleable.ArcProgressBar_scaleValueSize, scaleValueSize)
        scaleValueColor = typedArray.getColor(R.styleable.ArcProgressBar_scaleValueColor, scaleValueColor)
        scaleValuePercent = typedArray.getFraction(R.styleable.ArcProgressBar_scaleValuePercent, 1, 1, scaleValuePercent)
        progressBarPercent = typedArray.getFraction(R.styleable.ArcProgressBar_progressBarPercent, 1, 1, progressBarPercent)
        progressMax = typedArray.getInteger(R.styleable.ArcProgressBar_progressMax, progressMax)
        progressMin = typedArray.getInteger(R.styleable.ArcProgressBar_progressMin, progressMin)
        progress = typedArray.getInteger(R.styleable.ArcProgressBar_progress, progress)
        subProgressMax = typedArray.getInteger(R.styleable.ArcProgressBar_subProgressMax, subProgressMax)
        subProgressMin = typedArray.getInteger(R.styleable.ArcProgressBar_subProgressMin, subProgressMin)
        subProgress = typedArray.getInteger(R.styleable.ArcProgressBar_subProgress, subProgress)
        paintWidth = typedArray.getDimension(R.styleable.ArcProgressBar_paintWidth, paintWidth)
        paintColor = typedArray.getColor(R.styleable.ArcProgressBar_paintColor, paintColor)
        showCoordinate = typedArray.getBoolean(R.styleable.ArcProgressBar_showCoordinate, showCoordinate)
        showColorGradient = typedArray.getBoolean(R.styleable.ArcProgressBar_showColorGradient, showColorGradient)
        showScaleValue = typedArray.getBoolean(R.styleable.ArcProgressBar_showScaleValue, showScaleValue)
        // 设置画笔
        paint.isAntiAlias = true
        paint.isDither = true
        paint.isFilterBitmap = true
        paint.strokeWidth = paintWidth
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = paintColor
        paint.textAlign = Paint.Align.CENTER
        // 设置特殊的刻度线位置
        specialScales = typedArray.getString(R.styleable.ArcProgressBar_specialScales)?.split(Regex("[^0-9]+"))?.map { it.toInt() }
        // 设置标题
        title?.apply {
            string = typedArray.getString(R.styleable.ArcProgressBar_title) ?: ""
            color = typedArray.getColor(R.styleable.ArcProgressBar_titleColor, BLACK)
            size = typedArray.getDimension(R.styleable.ArcProgressBar_titleSize, 20.0f)
            angle = typedArray.getFloat(R.styleable.ArcProgressBar_titleAngle, 0f)
            percent = typedArray.getFraction(R.styleable.ArcProgressBar_titlePercent, 1, 1, 0.0f)
        }
        // 设置标题描述
        titleDesc?.apply {
            string = typedArray.getString(R.styleable.ArcProgressBar_titleDesc) ?: ""
            color = typedArray.getColor(R.styleable.ArcProgressBar_titleDescColor, BLACK)
            size = typedArray.getDimension(R.styleable.ArcProgressBar_titleDescSize, 20.0f)
            angle = typedArray.getFloat(R.styleable.ArcProgressBar_titleDescAngle, 0f)
            percent = typedArray.getFraction(R.styleable.ArcProgressBar_titleDescPercent, 1, 1, 0.0f)
        }
        // 设置副标题
        subTitle?.apply {
            string = typedArray.getString(R.styleable.ArcProgressBar_subTitle) ?: ""
            color = typedArray.getColor(R.styleable.ArcProgressBar_subTitleColor, BLACK)
            size = typedArray.getDimension(R.styleable.ArcProgressBar_subTitleSize, 20.0f)
            angle = typedArray.getFloat(R.styleable.ArcProgressBar_subTitleAngle, 0f)
            percent = typedArray.getFraction(R.styleable.ArcProgressBar_subTitlePercent, 1, 1, 0.0f)
        }
        // 设置副标题描述
        subTitleDesc?.apply {
            string = typedArray.getString(R.styleable.ArcProgressBar_subTitleDesc) ?: ""
            color = typedArray.getColor(R.styleable.ArcProgressBar_subTitleDescColor, BLACK)
            size = typedArray.getDimension(R.styleable.ArcProgressBar_subTitleDescSize, 20.0f)
            angle = typedArray.getFloat(R.styleable.ArcProgressBar_subTitleDescAngle, 0f)
            percent = typedArray.getFraction(R.styleable.ArcProgressBar_subTitleDescPercent, 1, 1, 0.0f)
        }
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = min(measuredWidth, measuredHeight)
        // 使用最大的内接正方形作为绘制有效区域
        rect.apply {
            left = (measuredWidth - size).toFloat() / 2
            top = (measuredHeight - size).toFloat() / 2
            right = left + size
            bottom = top + size
        }
        // 计算刻度线的半径
        scaleInsideRadius = (scaleInsidePercent * 0.5 * size).toFloat()
        scaleOutsideRadius = (scaleOutsidePercent * 0.5 * size).toFloat()
        scaleOutsideSpecialRadius = (scaleOutsideSpecialPercent * 0.5 * size).toFloat()
        // 计算进度条的半径
        progressBarRadius = (progressBarPercent * 0.5 * size).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 更新背景颜色bitmap
        updateColorBitmap()
        // 更新动态刻度线bitmap
        updateScaleBitmap()
        // 更新动态进度条bitmap
        updateProgressBarBitmap()
        // 打开一层新的图层
        val layer = canvas.saveLayer(rect, null, ALL_SAVE_FLAG)
        // 清除图层
        canvas.drawColor(TRANSPARENT, PorterDuff.Mode.CLEAR)
        // 绘制坐标
        if (showCoordinate) drawCoordinate(canvas)
        // 绘制静态刻度线
        drawStaticScale(canvas)
        // 绘制静态进度条
        drawStaticProgressBar(canvas)
        // 绘制刻度值
        if (showScaleValue) drawScaleValue(canvas)
        // 绘制动态刻度线
        drawScale(canvas)
        // 绘制动态进度条
        drawProgressBar(canvas)
        // 绘制标题
        title?.apply { drawText(canvas, this) }
        titleDesc?.apply { drawText(canvas, this) }
        subTitle?.apply { drawText(canvas, this) }
        subTitleDesc?.apply { drawText(canvas, this) }
        // 绘制渐变色
        if (showColorGradient) drawColorGradient(canvas)
        // 应用图层
        canvas.restoreToCount(layer)
    }

    /**
     * 更新渐变颜色的图片
     */
    private fun updateColorBitmap() {
        val size = min(rect.height(), rect.width())
        if (size <= 0) return
        // 绘制刻度的背景，刻度线背景只需要绘制一次，因为背景几乎不变动
        if (colorBitmap == null) {
            colorBitmap = Bitmap.createBitmap(size.toInt(), size.toInt(), Bitmap.Config.ARGB_8888)
            if (colorCanvas == null) {
                colorBitmap?.also { colorCanvas = Canvas(it) }
            }
            // 设置渐变色画笔
            backColors?.also { paint.shader = SweepGradient(size / 2, size / 2, it, backColorPositions) }
            // 旋转画布
            colorCanvas?.rotate(90f, size / 2, size / 2)
            // 绘制渐变色
            paint.also { colorCanvas?.drawCircle(size / 2, size / 2, size, it) }
            // 恢复画布的旋转
            colorCanvas?.rotate(-90f, size / 2, size / 2)
            // 恢复画笔
            paint.shader = null
        }
    }

    /**
     * 更新刻度线的bitmap
     */
    private fun updateScaleBitmap() {
        val size = min(measuredHeight, measuredWidth)
        if (size <= 0) return

        // 绘制刻度的前景，每次更新都需要绘制
        if (scaleBitmap == null || scaleBitmap?.width != size || scaleBitmap?.height != size) {
            scaleBitmap = Bitmap.createBitmap(rect.width().toInt(), rect.height().toInt(), Bitmap.Config.ARGB_8888)
        }
        if (scaleCanvas == null) {
            scaleBitmap?.also { scaleCanvas = Canvas(it) }
        }
        // 绘制前清空画布
        scaleCanvas?.drawColor(TRANSPARENT, PorterDuff.Mode.CLEAR)
        scaleCanvas?.also { drawScale(it, rect.width() / 2, rect.height() / 2, paint, progressPercent) }
    }

    /**
     * 更新进度条的bitmap
     */
    private fun updateProgressBarBitmap() {
        val size = min(measuredHeight, measuredWidth)
        if (size <= 0) return
        // 绘制进度条，每次更新都需要绘制
        if (progressBarBitmap == null || progressBarBitmap?.width != size || progressBarBitmap?.height != size) {
            progressBarBitmap = Bitmap.createBitmap(rect.width().toInt(), rect.height().toInt(), Bitmap.Config.ARGB_8888)
        }
        if (progressBarCanvas == null) {
            progressBarBitmap?.also { progressBarCanvas = Canvas(it) }
        }
        // 绘制进度条的边界
        val rectF = RectF().apply {
            left = rect.width() / 2 - rect.width() * progressBarPercent / 2
            top = rect.height() / 2 - rect.height() * progressBarPercent / 2
            bottom = rect.height() / 2 + rect.height() * progressBarPercent / 2
            right = rect.width() / 2 + rect.width() * progressBarPercent / 2
        }
        // 绘制前清空画布
        progressBarCanvas?.drawColor(TRANSPARENT, PorterDuff.Mode.CLEAR)
        progressBarCanvas?.also { drawProgressBar(it, rectF, paint, subProgressPercent) }
    }

    /**
     * 因为xfermode为DST_IN时只接受bitmap对象，所以先更新背景和前景的bitmap
     */
    @Suppress("DEPRECATION")
    private fun drawScale(canvas: Canvas) {
        // 判断绘制点
        val size = min(measuredHeight, measuredWidth)
        val top = ((measuredHeight - size) / 2).toFloat()
        val left = ((measuredWidth - size) / 2).toFloat()
        // 保存当前图层
        val layer = canvas.saveLayer(rect, null, ALL_SAVE_FLAG)
        // 绘制背景图片
        colorBitmap?.also { canvas.drawBitmap(it, left, top, paint) }
        // 设置画笔样式
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        // 绘制前景图片
        scaleBitmap?.also { canvas.drawBitmap(it, left, top, paint) }
        // 恢复图层
        canvas.restoreToCount(layer)
        // 恢复画笔
        paint.xfermode = null
    }

    /**
     * 绘制进度条
     */
    @Suppress("DEPRECATION")
    private fun drawProgressBar(canvas: Canvas) {
        // 判断绘制点
        val top = (measuredHeight - rect.height()) / 2
        val left = (measuredWidth - rect.width()) / 2
        // 保存当前图层
        val layer = canvas.saveLayer(rect, null, ALL_SAVE_FLAG)
        // 绘制背景图片
        colorBitmap?.also { canvas.drawBitmap(it, left, top, paint) }
        // 设置画笔样式
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        // 绘制前景图片
        progressBarBitmap?.also { canvas.drawBitmap(it, left, top, paint) }
        // 恢复图层
        canvas.restoreToCount(layer)
        // 恢复画笔
        paint.xfermode = null
    }

    /**
     * 绘制静态刻度线
     */
    private fun drawStaticScale(canvas: Canvas) {
        drawScale(canvas, rect.centerX(), rect.centerY(), paint)
    }

    /**
     * 绘制刻度线
     */
    private fun drawScale(canvas: Canvas?, centerX: Float, centerY: Float, paint: Paint?, percent: Float = 1f) {
        // 设置画笔为填充
        paint?.style = Paint.Style.FILL
        // 开始旋转的角度
        val startDegree = PI * 3 / 4
        // 角度结束3π/2
        val totalDegrees = PI * 3 / 2
        // 确定刻度之间的角度
        val gapDegrees = totalDegrees / (scaleCount - 1)
        // 绘制
        for (i in 0 until scaleCount) {
            // 到达绘制的上限，停止继续绘制
            if (i > (scaleCount - 1) * percent) break
            // 绘制角度
            val angle = i * gapDegrees + startDegree
            // 线段长度
            val scaleOutsideRadius = if (specialScales?.contains(i) == true) scaleOutsideSpecialRadius else scaleOutsideRadius
            // 坐标计算
            val startX = centerX + scaleInsideRadius * cos(angle).toFloat()
            val startY = centerY + scaleInsideRadius * sin(angle).toFloat()
            val stopX = centerX + scaleOutsideRadius * cos(angle).toFloat()
            val stopY = centerY + scaleOutsideRadius * sin(angle).toFloat()
            paint?.also { canvas?.drawLine(startX, startY, stopX, stopY, it) }
        }
    }

    /**
     * 静态进度条
     */
    private fun drawStaticProgressBar(canvas: Canvas) {
        // 绘制进度条的边界
        val rectF = RectF().apply {
            left = (rect.width() - rect.width() * progressBarPercent) / 2 + rect.left
            top = (rect.height() - rect.height() * progressBarPercent) / 2 + rect.top
            bottom = top + rect.height() * progressBarPercent
            right = left + rect.width() * progressBarPercent
        }
        drawProgressBar(canvas, rectF, paint)
    }

    /**
     * 绘制进度条
     */
    private fun drawProgressBar(canvas: Canvas?, rect: RectF, paint: Paint?, percent: Float = 1f) {
        // 设置画笔为描边
        paint?.style = Paint.Style.STROKE
        canvas?.rotate(180f * 3 / 4, rect.centerX(), rect.centerY())
        paint?.also { canvas?.drawArc(rect, 0f, 270f * percent, false, it) }
        canvas?.rotate(-180f * 3 / 4, rect.centerX(), rect.centerY())
    }

    /**
     * 绘制参考坐标
     */
    private fun drawCoordinate(canvas: Canvas) {
        paint.style = Paint.Style.FILL
        canvas.drawLine(measuredWidth.toFloat() / 2, 0f, measuredWidth.toFloat() / 2, measuredHeight.toFloat(), paint)
        canvas.drawLine(0f, measuredHeight.toFloat() / 2, measuredWidth.toFloat(), measuredHeight.toFloat() / 2, paint)
    }

    /**
     * 绘制刻度的文本
     */
    private fun drawScaleValue(canvas: Canvas) {
        drawText(canvas, DrawString(progressMin.toString(), scaleValueColor, 315.0f, scaleValuePercent, scaleValueSize))
        drawText(canvas, DrawString(((progressMin + progressMax) / 2).toString(), scaleValueColor, 180f, scaleValuePercent, scaleValueSize))
        drawText(canvas, DrawString(progressMax.toString(), scaleValueColor, 45f, scaleValuePercent, scaleValueSize))
    }

    /**
     * 绘制文本
     */
    private fun drawText(canvas: Canvas, text: DrawString) {
        paint.style = Paint.Style.FILL
        paint.apply {
            color = text.color
            textSize = text.size
            canvas.drawText(text.string, text.getPosition(rect).x, text.getPosition(rect).y, this)
        }
        // 恢复画笔的颜色
        paint.color = paintColor
    }

    private fun drawColorGradient(canvas: Canvas) {
        // 判断绘制点
        val size = min(measuredHeight, measuredWidth)
        val top = ((measuredHeight - size) / 2).toFloat()
        val left = ((measuredWidth - size) / 2).toFloat()
        // 绘制背景图片
        colorBitmap?.also { canvas.drawBitmap(it, left, top, paint) }
    }

    fun progress(progress: Int, toMax: Boolean = false, during: Long = 500) {
        val animator = if (toMax) {
            ObjectAnimator.ofInt(this, "progress", this.progress, progressMax, progress).setDuration(during * 2)
        } else {
            ObjectAnimator.ofInt(this, "progress", this.progress, progress).setDuration(during)
        }
        animator.start()
    }

    fun subProgress(progress: Int, toMax: Boolean = false, during: Long = 500) {
        val animator = if (toMax) {
            ObjectAnimator.ofInt(this, "subProgress", this.subProgress, subProgressMax, progress).setDuration(during * 2)
        } else {
            ObjectAnimator.ofInt(this, "subProgress", this.subProgress, progress).setDuration(during)
        }
        animator.start()
    }

    /**
     * 需要获取的是在父控件中裁剪的区域，所以这个path更改为在父控件中的坐标
     */
    fun getPath(path: Path, excludeRadius: Float = 0f): Path {
        path.addCircle((left + right).toFloat() / 2, (top + bottom).toFloat() / 2, scaleOutsideSpecialRadius + excludeRadius, Path.Direction.CW)
        return path
    }
}
