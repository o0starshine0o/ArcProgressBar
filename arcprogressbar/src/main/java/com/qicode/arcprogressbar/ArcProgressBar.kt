package com.qicode.arcprogressbar

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
    private var mScaleCount = 59
    /**
     * 进度最小值
     */
    private var mProgressMin = 100
    /**
     * 进度最大值
     */
    private var mProgressMax = 500
    /**
     * 目前进度值
     */
    private var mProgress = 400
    /**
     * 目前进度的百分比
     */
    private val mProgressPercent
        get() = max(min((mProgress - mProgressMin).toFloat() / (mProgressMax - mProgressMin), 1f), 0f)
    /**
     * 画笔
     */
    private var mPaint = Paint()
    /**
     * 画笔宽度
     */
    private var mPaintWidth = 10f
    /**
     * 画笔颜色
     */
    private var mPaintColor = WHITE
    /**
     * 刻度线内侧半径百分比
     */
    private var mScaleInsidePercent = 0.8f
    /**
     * 刻度线外侧半径百分比
     */
    private var mScaleOutsidePercent = 0.88f
    /**
     * 刻度线外侧特殊半径百分比
     */
    private var mScaleOutsideSpecialPercent = 0.91f
    /**
     * 刻度线内侧半径
     */
    private var mScaleInsideRadius = 0.0f
    /**
     * 刻度线外侧半径
     */
    private var mScaleOutsideRadius = 0.0f
    /**
     * 刻度线外侧特殊半径
     */
    private var mScaleOutsideSpecialRadius = 0.0f
    /**
     * 进度条半径百分比
     */
    private var mProgressBarPercent = 0.75f
    /**
     * 进度条半径
     */
    private var mProgressBarRadius = 0.0f
    /**
     * 刻度线外侧特殊半径,指明那几个刻度线需要特殊化处理
     */
    private var mSpecialScales: List<Int>? = null

    /**
     * 刻度线图片
     */
    private var mScaleBitmap: Bitmap? = null
    /**
     * 进度条图片
     */
    private var mProgressBarBitmap: Bitmap? = null
    /**
     * 渐变色图片
     */
    private var mColorBitmap: Bitmap? = null
    /**
     * 刻度线画布
     */
    private var mScaleCanvas: Canvas? = null
    /**
     * 刻度线画布
     */
    private var mProgressBarCanvas: Canvas? = null
    /**
     * 渐变色画布
     */
    private var mColorCanvas: Canvas? = null
    /**
     * 可使用的矩形区域
     */
    private var mRect = RectF()
    /**
     * 是否显示坐标
     */
    private var mShowCoordinate = false
    /**
     * 是否显示刻度值
     */
    private var mShowScaleValue = true
    /**
     * 刻度值文本大小
     */
    private var scaleValueSize = 40f
    /**
     * 刻度值文本颜色
     */
    private var scaleValueColor = WHITE
    /**
     * 刻度值文本距中心距离百分比
     */
    private var scaleValuePercent = 0.62f
    /**
     * 主标题
     */
    private var mTitle: DrawString? = DrawString("主标题", BLACK, 180f, 0.5f, 40f)
    /**
     * 主标题描述
     */
    private var mTitleDesc: DrawString? = DrawString("主标题描述", BLACK, 180f, 0.5f, 40f)
    /**
     * 副标题
     */
    private var mSubTitle: DrawString? = DrawString("副标题", BLACK, 180f, 0.5f, 40f)
    /**
     * 副标题描述
     */
    private var mSubTitleDesc: DrawString? = DrawString("副标题描述", BLACK, 180f, 0.5f, 40f)

    init {
        // 获取定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar)
        mScaleCount = typedArray.getInteger(R.styleable.ArcProgressBar_scaleCount, mScaleCount)
        mScaleInsidePercent = typedArray.getFraction(R.styleable.ArcProgressBar_scaleInsidePercent, 1, 1, mScaleInsidePercent)
        mScaleOutsidePercent = typedArray.getFraction(R.styleable.ArcProgressBar_scaleOutsidePercent, 1, 1, mScaleOutsidePercent)
        mScaleOutsideSpecialPercent = typedArray.getFraction(R.styleable.ArcProgressBar_scaleOutsideSpecialPercent, 1, 1, mScaleOutsideSpecialPercent)
        scaleValueSize = typedArray.getDimension(R.styleable.ArcProgressBar_scaleValueSize, scaleValueSize)
        scaleValueColor = typedArray.getColor(R.styleable.ArcProgressBar_scaleValueColor, scaleValueColor)
        scaleValuePercent = typedArray.getFraction(R.styleable.ArcProgressBar_scaleValuePercent, 1, 1, scaleValuePercent)
        mProgressMax = typedArray.getInteger(R.styleable.ArcProgressBar_progressMax, mProgressMax)
        mProgressMin = typedArray.getInteger(R.styleable.ArcProgressBar_progressMin, mProgressMin)
        mProgress = typedArray.getInteger(R.styleable.ArcProgressBar_progress, mProgress)
        mPaintWidth = typedArray.getDimension(R.styleable.ArcProgressBar_paintWidth, mPaintWidth)
        mPaintColor = typedArray.getColor(R.styleable.ArcProgressBar_paintColor, mPaintColor)
        mShowCoordinate = typedArray.getBoolean(R.styleable.ArcProgressBar_showCoordinate, mShowCoordinate)
        mShowScaleValue = typedArray.getBoolean(R.styleable.ArcProgressBar_showScaleValue, mShowScaleValue)
        // 设置画笔
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.isFilterBitmap = true
        mPaint.strokeWidth = mPaintWidth
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.color = mPaintColor
        mPaint.textAlign = Paint.Align.CENTER
        // 设置特殊的刻度线位置
        mSpecialScales = typedArray.getString(R.styleable.ArcProgressBar_specialScales)?.split(Regex("[^0-9]+"))?.map { it.toInt() }
        // 设置标题
        mTitle?.apply {
            string = typedArray.getString(R.styleable.ArcProgressBar_title) ?: ""
            color = typedArray.getColor(R.styleable.ArcProgressBar_titleColor, BLACK)
            size = typedArray.getDimension(R.styleable.ArcProgressBar_titleSize, 20.0f)
            angle = typedArray.getFloat(R.styleable.ArcProgressBar_titleAngle, 0f)
            percent = typedArray.getFraction(R.styleable.ArcProgressBar_titlePercent, 1, 1, 0.0f)
        }
        // 设置标题描述
        mTitleDesc?.apply {
            string = typedArray.getString(R.styleable.ArcProgressBar_titleDesc) ?: ""
            color = typedArray.getColor(R.styleable.ArcProgressBar_titleDescColor, BLACK)
            size = typedArray.getDimension(R.styleable.ArcProgressBar_titleDescSize, 20.0f)
            angle = typedArray.getFloat(R.styleable.ArcProgressBar_titleDescAngle, 0f)
            percent = typedArray.getFraction(R.styleable.ArcProgressBar_titleDescPercent, 1, 1, 0.0f)
        }
        // 设置副标题
        mSubTitle?.apply {
            string = typedArray.getString(R.styleable.ArcProgressBar_subTitle) ?: ""
            color = typedArray.getColor(R.styleable.ArcProgressBar_subTitleColor, BLACK)
            size = typedArray.getDimension(R.styleable.ArcProgressBar_subTitleSize, 20.0f)
            angle = typedArray.getFloat(R.styleable.ArcProgressBar_subTitleAngle, 0f)
            percent = typedArray.getFraction(R.styleable.ArcProgressBar_subTitlePercent, 1, 1, 0.0f)
        }
        // 设置副标题描述
        mSubTitleDesc?.apply {
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
        mRect.apply {
            left = (measuredWidth - size).toFloat() / 2
            top = (measuredHeight - size).toFloat() / 2
            right = left + size
            bottom = top + size
        }
        // 计算刻度线的半径
        mScaleInsideRadius = (mScaleInsidePercent * 0.5 * size).toFloat()
        mScaleOutsideRadius = (mScaleOutsidePercent * 0.5 * size).toFloat()
        mScaleOutsideSpecialRadius = (mScaleOutsideSpecialPercent * 0.5 * size).toFloat()
        // 计算进度条的半径
        mProgressBarRadius = (mProgressBarPercent * 0.5 * size).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 更新背景颜色bitmap
        updateColorBitmap()
        // 更新动态刻度线bitmap
        updateScaleBitmap()
        // 更新动态进度条bitmap
        updateProgressBarBitmap()
        // 绘制坐标
        if (mShowCoordinate) drawCoordinate(canvas)
        // 绘制静态刻度线
        drawStaticScale(canvas)
        // 绘制静态进度条
        drawStaticProgressBar(canvas)
        // 绘制刻度值
        if (mShowScaleValue) drawScaleValue(canvas)
        // 绘制动态刻度线
        drawScale(canvas)
        // 绘制动态进度条
        drawProgressBar(canvas)
        // 绘制标题
        mTitle?.apply { drawText(canvas, this) }
        mTitleDesc?.apply { drawText(canvas, this) }
        mSubTitle?.apply { drawText(canvas, this) }
        mSubTitleDesc?.apply { drawText(canvas, this) }
    }

    /**
     * 更新渐变颜色的图片
     */
    private fun updateColorBitmap() {
        val size = min(mRect.height(), mRect.width())
        if (size <= 0) return
        // 绘制刻度的背景，刻度线背景只需要绘制一次，因为背景几乎不变动
        if (mColorBitmap == null) {
            mColorBitmap = Bitmap.createBitmap(size.toInt(), size.toInt(), Bitmap.Config.ARGB_8888)
            if (mColorCanvas == null) {
                mColorBitmap?.also { mColorCanvas = Canvas(it) }
            }
            // 设置渐变色画笔
            mPaint.shader = SweepGradient(size / 2, size / 2, intArrayOf(BLUE, GREEN, RED), null)
            // 旋转画布
            mColorCanvas?.rotate(90f, size / 2, size / 2)
            // 绘制渐变色
            mPaint.also { mColorCanvas?.drawCircle(size / 2, size / 2, size, it) }
            // 恢复画布的旋转
            mColorCanvas?.rotate(-90f, size / 2, size / 2)
            // 恢复画笔
            mPaint.shader = null
        }
    }

    /**
     * 更新刻度线的bitmap
     */
    private fun updateScaleBitmap() {
        val size = min(measuredHeight, measuredWidth)
        if (size <= 0) return

        // 绘制刻度的前景，每次更新都需要绘制
        if (mScaleBitmap == null || mScaleBitmap?.width != size || mScaleBitmap?.height != size) {
            mScaleBitmap = Bitmap.createBitmap(mRect.width().toInt(), mRect.height().toInt(), Bitmap.Config.ARGB_8888)
        }
        if (mScaleCanvas == null) {
            mScaleBitmap?.also { mScaleCanvas = Canvas(it) }
        }
        mScaleCanvas?.also { drawScale(it, mRect.width() / 2, mRect.height() / 2, mPaint, mProgressPercent) }
    }

    /**
     * 更新进度条的bitmap
     */
    private fun updateProgressBarBitmap() {
        val size = min(measuredHeight, measuredWidth)
        if (size <= 0) return
        // 绘制进度条，每次更新都需要绘制
        if (mProgressBarBitmap == null || mProgressBarBitmap?.width != size || mProgressBarBitmap?.height != size) {
            mProgressBarBitmap = Bitmap.createBitmap(mRect.width().toInt(), mRect.height().toInt(), Bitmap.Config.ARGB_8888)
        }
        if (mProgressBarCanvas == null) {
            mProgressBarBitmap?.also { mProgressBarCanvas = Canvas(it) }
        }
        // 绘制进度条的边界
        val rectF = RectF().apply {
            left = mRect.width() / 2 - mRect.width() * mProgressBarPercent / 2
            top = mRect.height() / 2 - mRect.height() * mProgressBarPercent / 2
            bottom = mRect.height() / 2 + mRect.height() * mProgressBarPercent / 2
            right = mRect.width() / 2 + mRect.width() * mProgressBarPercent / 2
        }
        mProgressBarCanvas?.also { drawProgressBar(it, rectF, mPaint, mProgressPercent) }
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
        val layer = canvas.saveLayer(mRect, null, ALL_SAVE_FLAG)
        // 绘制背景图片
        mColorBitmap?.also { canvas.drawBitmap(it, left, top, mPaint) }
        // 设置画笔样式
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        // 绘制前景图片
        mScaleBitmap?.also { canvas.drawBitmap(it, left, top, mPaint) }
        // 恢复图层
        canvas.restoreToCount(layer)
        // 恢复画笔
        mPaint.xfermode = null
    }

    /**
     * 绘制进度条
     */
    @Suppress("DEPRECATION")
    private fun drawProgressBar(canvas: Canvas) {
        // 判断绘制点
        val top = (measuredHeight - mRect.height()) / 2
        val left = (measuredWidth - mRect.width()) / 2
        // 保存当前图层
        val layer = canvas.saveLayer(mRect, null, ALL_SAVE_FLAG)
        // 绘制背景图片
        mColorBitmap?.also { canvas.drawBitmap(it, left, top, mPaint) }
        // 设置画笔样式
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        // 绘制前景图片
        mProgressBarBitmap?.also { canvas.drawBitmap(it, left, top, mPaint) }
        // 恢复图层
        canvas.restoreToCount(layer)
        // 恢复画笔
        mPaint.xfermode = null
    }

    /**
     * 绘制静态刻度线
     */
    private fun drawStaticScale(canvas: Canvas) {
        drawScale(canvas, mRect.centerX(), mRect.centerY(), mPaint)
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
        val gapDegrees = totalDegrees / (mScaleCount - 1)
        // 绘制
        for (i in 0 until mScaleCount) {
            // 到达绘制的上限，停止继续绘制
            if (i > (mScaleCount - 1) * percent) break
            // 绘制角度
            val angle = i * gapDegrees + startDegree
            // 线段长度
            val scaleOutsideRadius = if (mSpecialScales?.contains(i) == true) mScaleOutsideSpecialRadius else mScaleOutsideRadius
            // 坐标计算
            val startX = centerX + mScaleInsideRadius * cos(angle).toFloat()
            val startY = centerY + mScaleInsideRadius * sin(angle).toFloat()
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
            left = (mRect.width() - mRect.width() * mProgressBarPercent) / 2 + mRect.left
            top = (mRect.height() - mRect.height() * mProgressBarPercent) / 2 + mRect.top
            bottom = top + mRect.height() * mProgressBarPercent
            right = left + mRect.width() * mProgressBarPercent
        }
        drawProgressBar(canvas, rectF, mPaint)
    }

    /**
     * 绘制进度条
     */
    private fun drawProgressBar(canvas: Canvas?, rect: RectF, paint: Paint?, percent: Float = 1f) {
        val angle = 270f * percent
        // 设置画笔为描边
        paint?.style = Paint.Style.STROKE
        canvas?.rotate(180f * 3 / 4, rect.centerX(), rect.centerY())
        paint?.also { canvas?.drawArc(rect, 0f, angle, false, it) }
        canvas?.rotate(-180f * 3 / 4, rect.centerX(), rect.centerY())
    }

    /**
     * 绘制参考坐标
     */
    private fun drawCoordinate(canvas: Canvas) {
        mPaint.style = Paint.Style.FILL
        canvas.drawLine(measuredWidth.toFloat() / 2, 0f, measuredWidth.toFloat() / 2, measuredHeight.toFloat(), mPaint)
        canvas.drawLine(0f, measuredHeight.toFloat() / 2, measuredWidth.toFloat(), measuredHeight.toFloat() / 2, mPaint)
    }

    /**
     * 绘制刻度的文本
     */
    private fun drawScaleValue(canvas: Canvas) {
        drawText(canvas, DrawString(mProgressMin.toString(), scaleValueColor, 315.0f, scaleValuePercent, scaleValueSize))
        drawText(canvas, DrawString(((mProgressMin + mProgressMax) / 2).toString(), scaleValueColor, 180f, scaleValuePercent, scaleValueSize))
        drawText(canvas, DrawString(mProgressMax.toString(), scaleValueColor, 45f, scaleValuePercent, scaleValueSize))
    }

    /**
     * 绘制文本
     */
    private fun drawText(canvas: Canvas, text: DrawString) {
        mPaint.style = Paint.Style.FILL
        mPaint.apply {
            color = text.color
            textSize = text.size
            canvas.drawText(text.string, text.getPosition(mRect).x, text.getPosition(mRect).y, this)
        }
        // 恢复画笔的颜色
        mPaint.color = mPaintColor
    }
}
