package com.abelhu.stepdemo.bar

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.Canvas.ALL_SAVE_FLAG
import android.graphics.Color.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.abelhu.stepdemo.R
import com.abelhu.stepdemo.extension.TAG
import kotlin.math.max
import kotlin.math.min


class ArcProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    /**
     * 前景的画笔
     */
    private var mForePaint: Paint? = null

    /**
     * 背景（渐变颜色）的画笔
     */
    private var mGradientColorPaint: Paint? = null

    /**
     * 静态刻度线的画笔
     */
    private var mScaleStaticPaint: Paint? = null

    /**
     * 静态进度条的画笔
     */
    private var mProgressStaticPaint: Paint? = null

    /**
     * 静态进度条的画笔
     */
    private var mTextPaint: Paint? = null
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
     * 刻度线外侧特殊半径,指明那几个刻度线需要特殊化处理，用非数字支付分割
     */
    private var mSpecialScaleString: String? = null
    /**
     * 刻度线外侧特殊半径,指明那几个刻度线需要特殊化处理
     */
    private var mSpecialScales: List<Int>? = null
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
     *
     */
    private var mTitle: DrawString? = DrawString("主标题", BLACK, 180f, 0.5f, 40f)
    private var mTitleDesc: DrawString? = DrawString("主标题描述", BLACK, 180f, 0.5f, 40f)
    private var mSubTitle: DrawString? = DrawString("副标题", BLACK, 180f, 0.5f, 40f)
    private var mSubTitleDesc: DrawString? = DrawString("副标题描述", BLACK, 180f, 0.5f, 40f)

    /**
     * 外层背景圆弧画笔
     */
    private var mArcBgPaint: Paint? = null
    /**
     * 外层前景圆弧画笔
     */
    private var mArcForePaint = Paint()
    /**
     * 内层虚线画笔
     */
    private var mDottedLinePaint = Paint()
    /**
     * 底部字矩形背景画笔
     */
    private var mRoundRectPaint: Paint? = null
    /**
     * 中间进度画笔
     */
    private var mProgressPaint: Paint? = null
    /**
     * 圆弧宽度
     */
    private val mArcWidth = 11.0f
    /**
     * 背景圆弧颜色
     */
    private var mArcBgColor = -0x362814
    /**
     * 前景圆弧结束颜色
     */
    private var mArcForeEndColor = -0xb92f06
    /**
     * 前景圆弧开始颜色
     */
    private var mArcForeStartColor = -0xcb7929
    /**
     * 虚线默认颜色
     */
    private var mDottedDefaultColor = -0x68665f
    /**
     * 虚线变动颜色
     */
    private var mDottedRunColor = -0xc76a15
    /**
     * 圆弧两边的距离
     */
    private var mPdDistance = 50
    /**
     * 线条数
     */
    private var mDottedLineCount = 59
    /**
     * 线条宽度
     */
    private var mDottedLineWidth = 40
    /**
     * 线条高度
     */
    private var mDottedLineHeight = 2
    /**
     * 圆弧跟虚线之间的距离
     */
    private var mLineDistance = 8
    /**
     * 进度文字大小
     */
    private var mProgressTextSize = 35
    /**
     * 进度文字颜色
     */
    private var mProgressTextRunColor = -0x25917f
    /**
     * 进度描述
     */
    private var mProgressDesc: String? = null
    //是否使用渐变
    protected var useGradient = true
    private var mScressWidth = 0

    init {
        intiAttributes(context, attrs)
        initView()
    }

    private fun intiAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar)
        mPdDistance = typedArray.getInteger(R.styleable.ArcProgressBar_arcDistance, mPdDistance)
        mArcBgColor = typedArray.getColor(R.styleable.ArcProgressBar_arcBgColor, mArcBgColor)
        mArcForeStartColor = typedArray.getColor(R.styleable.ArcProgressBar_arcForeStartColor, mArcForeStartColor)
        mArcForeEndColor = typedArray.getColor(R.styleable.ArcProgressBar_arcForeEndColor, mArcForeEndColor)
        mDottedDefaultColor = typedArray.getColor(R.styleable.ArcProgressBar_dottedDefaultColor, mDottedDefaultColor)
        mDottedRunColor = typedArray.getColor(R.styleable.ArcProgressBar_dottedRunColor, mDottedRunColor)
        mDottedLineCount = typedArray.getInteger(R.styleable.ArcProgressBar_dottedLineCount, mDottedLineCount)
        mDottedLineWidth = typedArray.getInteger(R.styleable.ArcProgressBar_dottedLineWidth, mDottedLineWidth)
        mDottedLineHeight = typedArray.getInteger(R.styleable.ArcProgressBar_dottedLineHeight, mDottedLineHeight)
        mLineDistance = typedArray.getInteger(R.styleable.ArcProgressBar_lineDistance, mLineDistance)
        mProgressTextSize = typedArray.getInteger(R.styleable.ArcProgressBar_progressTextSize, mProgressTextSize)
        mProgressDesc = typedArray.getString(R.styleable.ArcProgressBar_progressDesc)
        mProgressTextRunColor = typedArray.getColor(R.styleable.ArcProgressBar_progressTextRunColor, mProgressTextRunColor)
        useGradient = typedArray.getBoolean(R.styleable.ArcProgressBar_arcUseGradient, useGradient)
        mScaleInsidePercent = typedArray.getFraction(R.styleable.ArcProgressBar_scaleInsidePercent, 1, 1, mScaleInsidePercent)
        mScaleOutsidePercent = typedArray.getFraction(R.styleable.ArcProgressBar_scaleOutsidePercent, 1, 1, mScaleOutsidePercent)
        mScaleOutsideSpecialPercent = typedArray.getFraction(R.styleable.ArcProgressBar_scaleOutsideSpecialPercent, 1, 1, mScaleOutsideSpecialPercent)
        mSpecialScaleString = typedArray.getString(R.styleable.ArcProgressBar_specialScales)
        mProgressMax = typedArray.getInteger(R.styleable.ArcProgressBar_progressMax, mProgressMax)
        mProgressMin = typedArray.getInteger(R.styleable.ArcProgressBar_progressMin, mProgressMin)
        mProgress = typedArray.getInteger(R.styleable.ArcProgressBar_progress, mProgress)

        mTitle?.apply {
            string = typedArray.getString(R.styleable.ArcProgressBar_title) ?: ""
            color = typedArray.getColor(R.styleable.ArcProgressBar_titleColor, BLACK)
            size = typedArray.getDimension(R.styleable.ArcProgressBar_titleSize, 20.0f)
            angle = typedArray.getFloat(R.styleable.ArcProgressBar_titleAngle, 0f)
            percent = typedArray.getFraction(R.styleable.ArcProgressBar_titlePercent, 1, 1, 0.0f)
        }
        mTitleDesc?.apply {
            string = typedArray.getString(R.styleable.ArcProgressBar_titleDesc) ?: ""
            color = typedArray.getColor(R.styleable.ArcProgressBar_titleDescColor, BLACK)
            size = typedArray.getDimension(R.styleable.ArcProgressBar_titleDescSize, 20.0f)
            angle = typedArray.getFloat(R.styleable.ArcProgressBar_titleDescAngle, 0f)
            percent = typedArray.getFraction(R.styleable.ArcProgressBar_titleDescPercent, 1, 1, 0.0f)
        }
        mSubTitle?.apply {
            string = typedArray.getString(R.styleable.ArcProgressBar_subTitle) ?: ""
            color = typedArray.getColor(R.styleable.ArcProgressBar_subTitleColor, BLACK)
            size = typedArray.getDimension(R.styleable.ArcProgressBar_subTitleSize, 20.0f)
            angle = typedArray.getFloat(R.styleable.ArcProgressBar_subTitleAngle, 0f)
            percent = typedArray.getFraction(R.styleable.ArcProgressBar_subTitlePercent, 1, 1, 0.0f)
        }
        mSubTitleDesc?.apply {
            string = typedArray.getString(R.styleable.ArcProgressBar_subTitleDesc) ?: ""
            color = typedArray.getColor(R.styleable.ArcProgressBar_subTitleDescColor, BLACK)
            size = typedArray.getDimension(R.styleable.ArcProgressBar_subTitleDescSize, 20.0f)
            angle = typedArray.getFloat(R.styleable.ArcProgressBar_subTitleDescAngle, 0f)
            percent = typedArray.getFraction(R.styleable.ArcProgressBar_subTitleDescPercent, 1, 1, 0.0f)
        }
        typedArray.recycle()
    }

    private fun initView() {
        // 设置刻度线前景的画笔
        mForePaint = Paint()
        mForePaint?.isAntiAlias = true
        mForePaint?.isDither = true
        mForePaint?.isFilterBitmap = true
        // 设置刻度线背景的画笔
        mGradientColorPaint = Paint()
        mGradientColorPaint?.isAntiAlias = true
        mGradientColorPaint?.isDither = true
        mGradientColorPaint?.isFilterBitmap = true
        // 设置静态刻度线的画笔
        mScaleStaticPaint = Paint()
        mScaleStaticPaint?.isAntiAlias = true
        mScaleStaticPaint?.isDither = true
        mScaleStaticPaint?.isFilterBitmap = true
        mScaleStaticPaint?.color = WHITE
        // 设置静态进度条的画笔：描边
        mProgressStaticPaint = Paint()
        mProgressStaticPaint?.isAntiAlias = true
        mProgressStaticPaint?.isDither = true
        mProgressStaticPaint?.isFilterBitmap = true
        mProgressStaticPaint?.color = WHITE
        mProgressStaticPaint?.style = Paint.Style.STROKE
        mProgressStaticPaint?.strokeWidth = 10f
        mProgressStaticPaint?.strokeCap = Paint.Cap.ROUND
        // 设置文字的画笔
        mTextPaint = Paint()
        mTextPaint?.isAntiAlias = true
        mTextPaint?.isDither = true
        mTextPaint?.isFilterBitmap = true
        mTextPaint?.textAlign = Paint.Align.CENTER
        // 设置特殊的刻度线
        mSpecialScales = mSpecialScaleString?.split(Regex("[^0-9]+"))?.map { it.toInt() }


        val screenWH = screenWH
        mScressWidth = screenWH[0]
        // 外层背景圆弧的画笔
        mArcBgPaint = Paint()
        mArcBgPaint!!.isAntiAlias = true
        mArcBgPaint!!.style = Paint.Style.STROKE
        mArcBgPaint!!.strokeWidth = dp2px(resources, mArcWidth)
        mArcBgPaint!!.color = mArcBgColor
        mArcBgPaint!!.strokeCap = Paint.Cap.ROUND
        // 外层前景圆弧的画笔
        mArcForePaint = Paint()
        mArcForePaint!!.isAntiAlias = true
//        mArcForePaint!!.style = Paint.Style.STROKE
//        mArcForePaint!!.strokeWidth = dp2px(resources, mArcWidth)
//        mArcForePaint!!.strokeCap = Paint.Cap.ROUND
        // 内测虚线的画笔
        mDottedLinePaint!!.isAntiAlias = true
        mDottedLinePaint!!.strokeWidth = dp2px(resources, mDottedLineHeight.toFloat())
        mDottedLinePaint!!.color = mDottedDefaultColor
        //
        mRoundRectPaint = Paint()
        mRoundRectPaint!!.isAntiAlias = true
        mRoundRectPaint!!.color = mDottedRunColor
        mRoundRectPaint!!.style = Paint.Style.FILL
        // 中间进度画笔
        mProgressPaint = Paint()
        mProgressPaint!!.isAntiAlias = true
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
        drawCoordinate(canvas)
        // 绘制静态刻度线
        drawStaticScale(canvas)
        // 绘制静态进度条
        drawStaticProgressBar(canvas)
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

    private val screenWH: IntArray
        get() {
            val windowManager =
                    context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return intArrayOf(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }

    private fun dp2px(resources: Resources, dp: Float): Float {
        val scale = resources.displayMetrics.density
        return dp * scale + 0.5f
    }

    /**
     * 因为xfermode为DST_IN时只接受bitmap对象，所以先更新背景和前景的bitmap
     */
    private fun drawScale(canvas: Canvas) {
        // 判断绘制点
        val size = min(measuredHeight, measuredWidth)
        val top = ((measuredHeight - size) / 2).toFloat()
        val left = ((measuredWidth - size) / 2).toFloat()
        // 保存当前图层
        val layer = canvas.saveLayer(mRect, null, ALL_SAVE_FLAG)
        // 绘制背景图片
        mColorBitmap?.also { canvas.drawBitmap(it, left, top, mGradientColorPaint) }
        // 设置画笔样式
        mForePaint?.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        // 绘制前景图片
        mScaleBitmap?.also { canvas.drawBitmap(it, left, top, mForePaint) }
        // 恢复图层
        canvas.restoreToCount(layer)
    }

    private fun drawStaticScale(canvas: Canvas) {
        drawForeScale(canvas, mRect.centerX(), mRect.centerY(), mScaleStaticPaint)
    }

    private fun drawForeScale(canvas: Canvas?, centerX: Float, centerY: Float, paint: Paint?, percent: Float = 1f) {
        // 记录已经旋转的角度
        var degree = 0f
        // 弧度结束3π/2
        val totalDegrees = 180f * 3 / 2
        // 确定刻度之间的角度180 * 3/2数量
        val gapDegrees = totalDegrees / (mDottedLineCount - 1)
        // 开始旋转的角度
        val startDegree = (180 * 3 / 4).toFloat()
        // 刻度的圆角矩形
        val scaleRect = RectF()
        // 旋转到开始的位置
        canvas?.rotate(startDegree, centerX, centerY)
        degree += startDegree
        for (i in 0 until mDottedLineCount) {
            if (i > (mDottedLineCount - 1) * percent) break
            scaleRect.apply {
                left = centerX + mScaleInsideRadius
                top = centerY - 6
                // 设置特殊的刻度线
                right = centerX + if (mSpecialScales?.contains(i) == true) mScaleOutsideSpecialRadius else mScaleOutsideRadius
                bottom = centerY + 6
            }
            // 绘制圆角矩形
            paint?.also { canvas?.drawRoundRect(scaleRect, 6f, 6f, it) }
            // 旋转画布，为下次绘制做准备
            canvas?.rotate(gapDegrees, centerX, centerY)
            degree += gapDegrees
            Log.i(TAG(), "draw scale for:$i")
        }
        canvas?.rotate(-degree, centerX, centerY)
    }

    private fun drawProgressBar(canvas: Canvas?, rect: RectF, paint: Paint?, percent: Float = 1f) {
        val angle = 270f * percent
        canvas?.rotate(180f * 3 / 4, rect.centerX(), rect.centerY())
        paint?.also { canvas?.drawArc(rect, 0f, angle, false, it) }
        canvas?.rotate(-180f * 3 / 4, rect.centerX(), rect.centerY())
    }

    /**
     * 绘制参考坐标
     */
    private fun drawCoordinate(canvas: Canvas) {
        val paint = Paint().apply { color = BLACK }
        canvas.drawLine(measuredWidth.toFloat() / 2, 0f, measuredWidth.toFloat() / 2, measuredHeight.toFloat(), paint)
        canvas.drawLine(0f, measuredHeight.toFloat() / 2, measuredWidth.toFloat(), measuredHeight.toFloat() / 2, paint)
    }

    private fun drawStaticProgressBar(canvas: Canvas) {
        // 绘制进度条的边界
        val rectF = RectF().apply {
            left = (mRect.width() - mRect.width() * mProgressBarPercent) / 2 + mRect.left
            top = (mRect.height() - mRect.height() * mProgressBarPercent) / 2 + mRect.top
            bottom = top + mRect.height() * mProgressBarPercent
            right = left + mRect.width() * mProgressBarPercent
        }
        drawProgressBar(canvas, rectF, mProgressStaticPaint)
    }

    private fun drawProgressBar(canvas: Canvas) {
        // 判断绘制点
//        val size = min(measuredHeight, measuredWidth)
        val top = (measuredHeight - mRect.height()) / 2
        val left = (measuredWidth - mRect.width()) / 2
        // 保存当前图层
        val layer = canvas.saveLayer(mRect, null, ALL_SAVE_FLAG)
        // 绘制背景图片
        mColorBitmap?.also { canvas.drawBitmap(it, left, top, mGradientColorPaint) }
        // 设置画笔样式
        mForePaint?.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        // 绘制前景图片
        mProgressBarBitmap?.also { canvas.drawBitmap(it, left, top, mForePaint) }
        // 恢复图层
        canvas.restoreToCount(layer)
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
            mGradientColorPaint?.shader = SweepGradient(size / 2, size / 2, intArrayOf(RED, GREEN, BLUE), null)
            // 旋转画布
            mColorCanvas?.rotate(90f, size / 2, size / 2)
            // 绘制渐变色
            mGradientColorPaint?.also { mColorCanvas?.drawCircle(size / 2, size / 2, size, it) }
            // 恢复画布的旋转
            mColorCanvas?.rotate(-90f, size / 2, size / 2)
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
        mScaleCanvas?.also { drawForeScale(it, mRect.width() / 2, mRect.height() / 2, mForePaint, mProgressPercent) }
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
        mProgressBarCanvas?.also { drawProgressBar(it, rectF, mProgressStaticPaint, mProgressPercent) }
    }

    private fun drawText(canvas: Canvas, text: DrawString) {
        mTextPaint?.apply {
            color = text.color
            textSize = text.size
            canvas.drawText(text.string, text.getPosition(mRect).x, text.getPosition(mRect).y, this)
        }
    }
}
