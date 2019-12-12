package com.abelhu.stepdemo.bar

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import com.abelhu.stepdemo.R

class ArcProgressBar @JvmOverloads constructor(context: Context,attrs: AttributeSet? = null,defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    /**
     * 外层背景圆弧画笔
     */
    private var mArcBgPaint: Paint? = null
    /**
     * 外层前景圆弧画笔
     */
    private var mArcForePaint: Paint? = null
    /**
     * 内层虚线画笔
     */
    private var mDottedLinePaint: Paint? = null
    /**
     * 底部字矩形背景画笔
     */
    private var mRoundRectPaint: Paint? = null
    /**
     * 中间进度画笔
     */
    private var mProgressPaint: Paint? = null
    /**
     * 外层圆弧需要
     */
    private var mArcRect: RectF? = null
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
    private var mDottedLineCount = 100
    /**
     * 线条宽度
     */
    private var mDottedLineWidth = 40
    /**
     * 线条高度
     */
    private var mDottedLineHeight = 6
    /**
     * 圆弧跟虚线之间的距离
     */
    private var mLineDistance = 8
    /**
     * 进度条最大值
     */
    private var mProgressMax = 100
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
    private var mProgress = 0
    private var mExternalDottedLineRadius = 0f
    private var mInsideDottedLineRadius = 0f
    private var mArcCenterX = 0
    private var mArcRadius // 圆弧半径
            = 0
    private var bDistance = 0.0
    private var aDistance = 0.0
    private var isRestart = false
    private var mRealProgress = 0
    //开放api//
    fun setmArcBgColor(mArcBgColor: Int) {
        this.mArcBgColor = mArcBgColor
    }

    fun setmArcForeEndColor(mArcForeEndColor: Int) {
        this.mArcForeEndColor = mArcForeEndColor
    }

    fun setmArcForeStartColor(mArcForeStartColor: Int) {
        this.mArcForeStartColor = mArcForeStartColor
    }

    fun setmDottedDefaultColor(mDottedDefaultColor: Int) {
        this.mDottedDefaultColor = mDottedDefaultColor
    }

    fun setmDottedRunColor(mDottedRunColor: Int) {
        this.mDottedRunColor = mDottedRunColor
    }

    fun setmPdDistance(mPdDistance: Int) {
        this.mPdDistance = mPdDistance
    }

    fun setmDottedLineCount(mDottedLineCount: Int) {
        this.mDottedLineCount = mDottedLineCount
    }

    fun setmDottedLineWidth(mDottedLineWidth: Int) {
        this.mDottedLineWidth = mDottedLineWidth
    }

    fun setmDottedLineHeight(mDottedLineHeight: Int) {
        this.mDottedLineHeight = mDottedLineHeight
    }

    fun setmLineDistance(mLineDistance: Int) {
        this.mLineDistance = mLineDistance
    }

    fun setmProgressMax(mProgressMax: Int) {
        this.mProgressMax = mProgressMax
    }

    fun setmProgressTextSize(mProgressTextSize: Int) {
        this.mProgressTextSize = mProgressTextSize
    }

    fun setmProgressTextRunColor(mProgressTextRunColor: Int) {
        this.mProgressTextRunColor = mProgressTextRunColor
    }

    fun setmProgressDesc(mProgressDesc: String?) {
        this.mProgressDesc = mProgressDesc
    }

    private fun intiAttributes(context: Context,attrs: AttributeSet?) {
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
        mProgressMax = typedArray.getInteger(R.styleable.ArcProgressBar_progressMax, mProgressMax)
        mProgressTextSize = typedArray.getInteger(R.styleable.ArcProgressBar_progressTextSize, mProgressTextSize)
        mProgressDesc = typedArray.getString(R.styleable.ArcProgressBar_progressDesc)
        mProgressTextRunColor = typedArray.getColor(R.styleable.ArcProgressBar_progressTextRunColor, mProgressTextRunColor)
        useGradient = typedArray.getBoolean(R.styleable.ArcProgressBar_arcUseGradient, useGradient)
        typedArray.recycle()
    }

    private fun initView() {
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
        mArcForePaint!!.style = Paint.Style.STROKE
        mArcForePaint!!.strokeWidth = dp2px(resources, mArcWidth)
        mArcForePaint!!.strokeCap = Paint.Cap.ROUND
        // 内测虚线的画笔
        mDottedLinePaint = Paint()
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

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        //        int width = mScressWidth - 2 * mPdDistance;
////        setMeasuredDimension(width, width);
//    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mArcCenterX = (w / 2f).toInt()
        mArcRect = RectF()
        mArcRect!!.top = 0f
        mArcRect!!.left = 0f
        mArcRect!!.right = w.toFloat()
        mArcRect!!.bottom = h.toFloat()
        mArcRect!!.inset(
            dp2px(resources, mArcWidth) / 2,
            dp2px(resources, mArcWidth) / 2
        ) //设置矩形的宽度
        mArcRadius = (mArcRect!!.width() / 2).toInt()
        val sqrt =
            Math.sqrt(mArcRadius * mArcRadius + mArcRadius * mArcRadius.toDouble())
        bDistance = Math.cos(Math.PI * 45 / 180) * mArcRadius
        aDistance = Math.sin(Math.PI * 45 / 180) * mArcRadius
        // 内部虚线的外部半径
        mExternalDottedLineRadius = mArcRadius - dp2px(resources, mArcWidth) / 2 - dp2px(
            resources,
            mLineDistance.toFloat()
        )
        // 内部虚线的内部半径
        mInsideDottedLineRadius =
            mExternalDottedLineRadius - dp2px(resources, mDottedLineWidth.toFloat())
        if (useGradient) {
            val gradient = LinearGradient(
                0,
                0,
                measuredWidth,
                measuredHeight,
                mArcForeEndColor,
                mArcForeStartColor,
                Shader.TileMode.CLAMP
            )
            mArcForePaint!!.shader = gradient
        } else {
            mArcForePaint!!.color = mArcForeStartColor
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mArcBgPaint!!.color = mArcBgColor
        canvas.drawText(
            "信用额度", mArcCenterX - mProgressPaint!!.measureText("信用额度") / 2,
            mArcCenterX - (mProgressPaint!!.descent() + mProgressPaint!!.ascent()) / 2 - dp2px(
                resources,
                20f
            ), mProgressPaint!!
        )
        drawDottedLineArc(canvas)
        drawRunDottedLineArc(canvas)
        drawRunText(canvas)
        canvas.rotate(135f, mArcCenterX.toFloat(), mArcCenterX.toFloat())
        canvas.drawArc(mArcRect!!, 0f, 270f, false, mArcBgPaint!!) //画背景圆弧
        //        canvas.drawRect(mArcRect, mArcBgPaint);//画直角矩形
//        canvas.drawCircle(400, 400, 100, mArcForePaint);//画圆
        canvas.drawArc(mArcRect!!, 0f, 0f, false, mArcForePaint!!) //画前景圆弧
        //        canvas.drawColor(Color.TRANSPARENT);//设置画布背景
//        canvas.drawLine(100, 100, 400, 400, mArcBgPaint);//画直线
//        canvas.drawPoint(500, 500, mArcBgPaint);//画点
        mProgressPaint!!.color = resources.getColor(R.color.gray)
        mProgressPaint!!.textSize = dp2px(resources, 18f)
        drawRunFullLineArc(canvas)
        if (isRestart) {
            drawDottedLineArc(canvas)
        }
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

    /**
     * 虚线变动
     *
     * @param canvas
     */
    private fun drawRunDottedLineArc(canvas: Canvas) {
        mDottedLinePaint!!.color = mDottedRunColor
        val evenryDegrees =
            (2.0f * Math.PI / mDottedLineCount).toFloat()
        val startDegress =
            (225 * Math.PI / 180).toFloat() + evenryDegrees / 2
        for (i in 0 until mProgress) {
            val degrees = i * evenryDegrees + startDegress
            val startX =
                mArcCenterX + Math.sin(degrees.toDouble()).toFloat() * mInsideDottedLineRadius
            val startY =
                mArcCenterX - Math.cos(degrees.toDouble()).toFloat() * mInsideDottedLineRadius
            val stopX =
                mArcCenterX + Math.sin(degrees.toDouble()).toFloat() * mExternalDottedLineRadius
            val stopY =
                mArcCenterX - Math.cos(degrees.toDouble()).toFloat() * mExternalDottedLineRadius
            canvas.drawLine(startX, startY, stopX, stopY, mDottedLinePaint!!)
        }
    }

    /**
     * 实线变动
     *
     * @param canvas
     */
    private fun drawRunFullLineArc(canvas: Canvas) {
        for (i in 0 until mProgress) {
            canvas.drawArc(
                mArcRect!!, 0f, 270 * mProgress / 75.toFloat(), false,
                mArcForePaint!!
            )
        }
    }

    private fun drawDottedLineArc(canvas: Canvas) {
        mDottedLinePaint!!.color = mDottedDefaultColor
        // 360 * Math.PI / 180
        val evenryDegrees =
            (2.0f * Math.PI / mDottedLineCount).toFloat()
        val startDegress = (135 * Math.PI / 180).toFloat()
        val endDegress = (225 * Math.PI / 180).toFloat()
        for (i in 0 until mDottedLineCount) {
            val degrees = i * evenryDegrees
            // 过滤底部90度的弧长
            if (degrees > startDegress && degrees < endDegress) {
                continue
            }
            val startX =
                mArcCenterX + Math.sin(degrees.toDouble()).toFloat() * mInsideDottedLineRadius
            val startY =
                mArcCenterX - Math.cos(degrees.toDouble()).toFloat() * mInsideDottedLineRadius
            val stopX =
                mArcCenterX + Math.sin(degrees.toDouble()).toFloat() * mExternalDottedLineRadius
            val stopY =
                mArcCenterX - Math.cos(degrees.toDouble()).toFloat() * mExternalDottedLineRadius
            canvas.drawLine(startX, startY, stopX, stopY, mDottedLinePaint!!)
        }
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

    init {
        intiAttributes(context, attrs)
        initView()
    }
}
