package com.abelhu.stepdemo.bar

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.Canvas.ALL_SAVE_FLAG
import android.graphics.Color.*
import android.text.TextUtils
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
     * 动态刻度线前景的画笔
     */
    private var mScaleForePaint: Paint? = null

    /**
     * 动态刻度线背景的画笔
     */
    private var mScaleBackPaint: Paint? = null

    /**
     * 静态刻度线的画笔
     */
    private var mScaleStaticPaint: Paint? = null

    /**
     * 静态进度条的画笔
     */
    private var mProgressStaticPaint: Paint? = null
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
     * 刻度线前景图片
     */
    private var scaleForeBitmap: Bitmap? = null
    /**
     * 刻度线背景图片
     */
    private var scaleBackBitmap: Bitmap? = null
    /**
     * 刻度线前景画布
     */
    private var scaleForeCanvas: Canvas? = null
    /**
     * 刻度线背景画布
     */
    private var scaleBackCanvas: Canvas? = null
    /**
     * 可使用的矩形区域
     */
    private var mArcRect = RectF()
    /**
     * 可使用的矩形区域
     */
    private var mProgressRect = RectF()
    /**
     * 刻度需要绘制圆角矩形
     */
    private var mScale = RectF()
    private var mCenter = PointF()


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
    //    private var mProgress = 0
    private var mExternalDottedLineRadius = 0f
    private var mInsideDottedLineRadius = 0f
    /**
     * 绘图的中心点
     */
    private var mArcCenterX = 0f
    private var mArcCenterY = 0f
    private var mArcRadius // 圆弧半径
            = 0
    private var bDistance = 0.0
    private var aDistance = 0.0
    private var isRestart = false
    private var mRealProgress = 0

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
        typedArray.recycle()
    }

    private fun initView() {
        // 设置刻度线前景的画笔
        mScaleForePaint = Paint()
        mScaleForePaint?.isAntiAlias = true
        mScaleForePaint?.isDither = true
        mScaleForePaint?.isFilterBitmap = true
        // 设置刻度线背景的画笔
        mScaleBackPaint = Paint()
        mScaleBackPaint?.isAntiAlias = true
        mScaleBackPaint?.isDither = true
        mScaleBackPaint?.isFilterBitmap = true
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
        mArcRect.apply {
            left = (measuredWidth - size).toFloat() / 2
            top = (measuredHeight - size).toFloat() / 2
            right = left + size
            bottom = top + size
        }
        // 计算中心点坐标
        mCenter.apply {
            x = size.toFloat() / 2
            y = size.toFloat() / 2
        }
        // 计算刻度线的半径
        mScaleInsideRadius = (mScaleInsidePercent * 0.5 * size).toFloat()
        mScaleOutsideRadius = (mScaleOutsidePercent * 0.5 * size).toFloat()
        mScaleOutsideSpecialRadius = (mScaleOutsideSpecialPercent * 0.5 * size).toFloat()
        // 计算进度条的半径
        mProgressBarRadius = (mProgressBarPercent * 0.5 * size).toFloat()
        // 设置进度条的区域
        mProgressRect.apply {
            left = mArcRect.centerX() - mProgressBarRadius
            top = mArcRect.centerY() - mProgressBarRadius
            right = mArcRect.centerX() + mProgressBarRadius
            bottom = mArcRect.centerY() + mProgressBarRadius
        }
    }

//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
//        mArcCenterX = (w / 2f)
//        mArcRect = RectF()
//        mArcRect!!.top = 0f
//        mArcRect!!.left = 0f
//        mArcRect!!.right = w.toFloat()
//        mArcRect!!.bottom = h.toFloat()
//        mArcRect!!.inset(
//                dp2px(resources, mArcWidth) / 2,
//                dp2px(resources, mArcWidth) / 2
//        ) //设置矩形的宽度
//        mArcRadius = (mArcRect!!.width() / 2).toInt()
//        val sqrt =
//                Math.sqrt(mArcRadius * mArcRadius + mArcRadius * mArcRadius.toDouble())
//        bDistance = Math.cos(Math.PI * 45 / 180) * mArcRadius
//        aDistance = Math.sin(Math.PI * 45 / 180) * mArcRadius
//        // 内部虚线的外部半径
//        mExternalDottedLineRadius = mArcRadius - dp2px(resources, mArcWidth) / 2 - dp2px(resources, mLineDistance.toFloat())
//        // 内部虚线的内部半径
//        mInsideDottedLineRadius = mExternalDottedLineRadius - dp2px(resources, mDottedLineWidth.toFloat())
//        if (useGradient) {
//            val gradient = LinearGradient(
//                    0f,
//                    0f,
//                    measuredWidth.toFloat(),
//                    measuredHeight.toFloat(),
//                    mArcForeEndColor,
//                    mArcForeStartColor,
//                    Shader.TileMode.CLAMP
//            )
//            mArcForePaint!!.shader = gradient
//        } else {
//            mArcForePaint!!.color = mArcForeStartColor
//        }
//        // 使用最大的内接正方形
//        val size = min(measuredWidth, measuredHeight)
//        mArcRect.apply {
//            left = (measuredWidth - size).toFloat() / 2
//            top = (measuredHeight - size).toFloat() / 2
//            right = left + size
//            bottom = top + size
//        }
//        mArcCenterX = (mArcRect.left + mArcRect.right) / 2
//        mArcCenterY = (mArcRect.top + mArcRect.bottom) / 2
//    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 绘制静态刻度线
        drawStaticScale(canvas)
        // 绘制动态刻度线
        drawScale(canvas)
        // 绘制坐标
        drawCoordinate(canvas)
        // 绘制进度条
        drawProgressBar(canvas)
//        mArcBgPaint?.color = mArcBgColor
//        canvas.drawText(
//                "信用额度", mArcCenterX - mProgressPaint!!.measureText("信用额度") / 2,
//                mArcCenterX - (mProgressPaint!!.descent() + mProgressPaint!!.ascent()) / 2 - dp2px(
//                        resources,
//                        20f
//                ), mProgressPaint!!
//        )
//        drawDottedLineArc(canvas)
//        drawRunDottedLineArc(canvas)
//        drawRunText(canvas)
//        canvas.rotate(135f, mArcCenterX.toFloat(), mArcCenterX.toFloat())
//        canvas.drawArc(mArcRect!!, 0f, 270f, false, mArcBgPaint!!) //画背景圆弧
        //        canvas.drawRect(mArcRect, mArcBgPaint);//画直角矩形
//        canvas.drawCircle(400, 400, 100, mArcForePaint);//画圆
//        canvas.drawArc(mArcRect!!, 0f, 0f, false, mArcForePaint!!) //画前景圆弧
        //        canvas.drawColor(Color.TRANSPARENT);//设置画布背景
//        canvas.drawLine(100, 100, 400, 400, mArcBgPaint);//画直线
//        canvas.drawPoint(500, 500, mArcBgPaint);//画点
//        mProgressPaint!!.color = resources.getColor(R.color.gray)
//        mProgressPaint!!.textSize = dp2px(resources, 18f)
//        drawRunFullLineArc(canvas)
//        if (isRestart) {
//            drawDottedLineArc(canvas)
//        }
    }

    private fun drawRunText(canvas: Canvas) {
        var progressStr: String? = (mRealProgress * 25).toString() + ""
        if (!TextUtils.isEmpty(mProgressDesc)) {
            progressStr = mProgressDesc
        }
        mProgressPaint!!.textSize = dp2px(resources, mProgressTextSize.toFloat())
        mProgressPaint!!.color = mProgressTextRunColor
        canvas.drawText(
                progressStr!!, mArcCenterX - mProgressPaint!!.measureText(progressStr) / 2,
                mArcCenterX - (mProgressPaint!!.descent() + mProgressPaint!!.ascent()) / 2 + dp2px(
                        resources,
                        13f
                ), mProgressPaint!!
        )
    }

    fun restart() {
        isRestart = true
        mRealProgress = 0
        mProgressDesc = ""
        invalidate()
    }

    /**
     * 设置中间进度描述
     *
     * @param desc
     */
    fun setProgressDesc(desc: String?) {
        mProgressDesc = desc
        postInvalidate()
    }

    /**
     * 设置最大进度
     *
     * @param max
     */
    fun setMaxProgress(max: Int) {
        mProgressMax = max
    }

    /**
     * 设置当前进度
     *
     * @param progress
     */
    fun setProgress(progress: Int) { // 进度100% = 控件的75%
        mRealProgress = progress
        isRestart = false
        mProgress = mDottedLineCount * 3 / 4 * progress / mProgressMax
        postInvalidate()
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
        // 更新刻度线的图片
        updateScaleImage()
        // 判断绘制点
        val size = min(measuredHeight, measuredWidth)
        val top = ((measuredHeight - size) / 2).toFloat()
        val left = ((measuredWidth - size) / 2).toFloat()
        // 保存当前图层
        val layer = canvas.saveLayer(mArcRect, null, ALL_SAVE_FLAG)
        // 绘制背景图片
        scaleBackBitmap?.also { canvas.drawBitmap(it, left, top, mScaleBackPaint) }
        // 设置画笔样式
        mScaleForePaint?.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        // 绘制前景图片
        scaleForeBitmap?.also { canvas.drawBitmap(it, left, top, mScaleForePaint) }
        // 恢复图层
        canvas.restoreToCount(layer)
    }

    private fun drawStaticScale(canvas: Canvas) {
        drawForeScale(canvas, PointF(measuredWidth.toFloat() / 2, measuredHeight.toFloat() / 2), mScaleStaticPaint)
    }

    /**
     * 更新刻度线背景和前景的bitmap
     */
    private fun updateScaleImage() {
        val size = min(measuredHeight, measuredWidth)
        if (size <= 0) return

        // 绘制刻度的背景，刻度线背景只需要绘制一次，因为背景几乎不变动
        if (scaleBackBitmap == null || scaleBackBitmap?.width != size || scaleBackBitmap?.height != size) {
            scaleBackBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            if (scaleBackCanvas == null) {
                scaleBackBitmap?.also { scaleBackCanvas = Canvas(it) }
            }
            scaleBackCanvas?.also { drawBackScale(it, size.toFloat()) }
        }

        // 绘制刻度的前景，每次更新都需要绘制
        if (scaleForeBitmap == null || scaleForeBitmap?.width != size || scaleForeBitmap?.height != size) {
            scaleForeBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        }
        if (scaleForeCanvas == null) {
            scaleForeBitmap?.also { scaleForeCanvas = Canvas(it) }
        }
        scaleForeCanvas?.also { drawForeScale(it, mCenter, mScaleForePaint, mProgressPercent) }
    }

    private fun drawBackScale(canvas: Canvas?, size: Float) {
        val colors = intArrayOf(RED, GREEN, BLUE)
        mScaleBackPaint?.shader = SweepGradient(size / 2, size / 2, colors, null)
        canvas?.rotate(90f, size / 2, size / 2)
        mScaleBackPaint?.also { canvas?.drawCircle(size / 2, size / 2, size, it) }
        canvas?.rotate(-90f, size / 2, size / 2)
    }

    private fun drawForeScale(canvas: Canvas?, center: PointF, paint: Paint?, percent: Float = 1f) {
        // 记录已经旋转的角度
        var degree = 0f
        // 弧度结束3π/2
        val totalDegrees = 180f * 3 / 2
        // 确定刻度之间的角度180 * 3/2数量
        val gapDegrees = totalDegrees / (mDottedLineCount - 1)
        // 开始旋转的角度
        val startDegree = (180 * 3 / 4).toFloat()
        // 旋转到开始的位置
        canvas?.rotate(startDegree, center.x, center.y)
        degree += startDegree
        for (i in 0 until mDottedLineCount) {
            if (i > (mDottedLineCount - 1) * percent) break
            mScale.apply {
                left = center.x + mScaleInsideRadius
                top = center.y - 6
                // 设置特殊的刻度线
                right = center.x + if (mSpecialScales?.contains(i) == true) mScaleOutsideSpecialRadius else mScaleOutsideRadius
                bottom = center.y + 6
            }
            // 绘制圆角矩形
            paint?.also { canvas?.drawRoundRect(mScale, 6f, 6f, it) }
            // 旋转画布，为下次绘制做准备
            canvas?.rotate(gapDegrees, center.x, center.y)
            degree += gapDegrees
            Log.i(TAG(), "draw scale for:$i")
        }
        canvas?.rotate(-degree, center.x, center.y)
    }

    /**
     * 绘制参考坐标
     */
    private fun drawCoordinate(canvas: Canvas) {
        val paint = Paint().apply { color = BLACK }
        canvas.drawLine(measuredWidth.toFloat() / 2, 0f, measuredWidth.toFloat() / 2, measuredHeight.toFloat(), paint)
        canvas.drawLine(0f, measuredHeight.toFloat() / 2, measuredWidth.toFloat(), measuredHeight.toFloat() / 2, paint)
    }

    private fun drawProgressBar(canvas: Canvas) {
        canvas.rotate(180f * 3 / 4, mProgressRect.centerX(), mProgressRect.centerY())
        mProgressStaticPaint?.also { canvas.drawArc(mProgressRect, 0f, 270f, false, it) }
        canvas.rotate(-180f * 3 / 4, mProgressRect.centerX(), mProgressRect.centerY())
    }
}
