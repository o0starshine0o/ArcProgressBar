package com.abelhu.stepdemo

import android.animation.AnimatorInflater
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.qicode.arcprogressbar.DrawString
import com.qicode.bubbleview.BubbleLayoutManager
import com.qicode.extension.dp
import com.qicode.extension.sp
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initContainer()
        initProgress()
        initButton()
        initDecoration()
        initBubble()
        initFunc()
    }

    private fun initContainer() {
        container.apply {
            // 地面颜色
            groundColor = Color.WHITE
            // 基地高度
            groundHeight = 56.dp
            // 地面高度
            groundSize = 26.dp
            // 圆滑程度
            smooth = 0.2f
            // 需要排除的气泡半径
            excludeRadius = 20.dp
        }
    }

    private fun initProgress() {
        progressBar.apply {
            // 中间各类文本
            title = DrawString("今日步数", Color.BLACK, 180f, 0.30f, 20.sp)
            titleDesc = DrawString("6666", Color.BLACK, 180f, 0.06f, 50.sp)
            subTitle = DrawString("运动步数", Color.BLACK, 0f, 0.12f, 20.sp)
            subTitleDesc = DrawString("6363", Color.BLACK, 0f, 0.30f, 30.sp)
            // 角度
            startAngle = 0.83f
            drawAngle = 1.33f
            // 刻度线数量
            scaleCount = 59
            // 进度
            progressMin = 1
            progressMax = 15000
            progress = 12345
            // 进度
            subProgressMin = 1
            subProgressMax = 15000
            subProgress = 1234
            // 画笔宽度
            paintWidth = 5.dp
            // 画笔颜色
            paintColor = Color.WHITE
            // 刻度线半径百分比
            scaleInsidePercent = 0.7f
            scaleOutsidePercent = 0.78f
            scaleOutsideSpecialPercent = 0.81f
            // 进度条半径百分比
            progressBarPercent = 0.65f
            // 特殊刻度
            specialScales = listOf(9, 29, 49)
            // 是否显示坐标
            showCoordinate = false
            // 是否显示渐变色
            showColorGradient = false
            // 是否显示刻度值
            showScaleValue = true
            // 刻度值字体大小
            scaleValueSize = 16.sp
            // 刻度值字体颜色
            scaleValueColor = Color.WHITE
            // 刻度值半径百分比
            scaleValuePercent = 0.52f
            // 背景渐变色
            backColors = context.resources.getIntArray(com.qicode.arcprogressbar.R.array.color_gradient)
            // 背景渐变色分布(默认平均分布)
            backColorPositions = context.resources.getStringArray(com.qicode.arcprogressbar.R.array.position_gradient).map { it.toFloat() }.toFloatArray()
        }
    }

    private fun initButton() {
        button.text = "领取奖励"
        val animator = AnimatorInflater.loadAnimator(this, R.animator.scale_90_2s_repeat)
        animator.setTarget(button)
        animator.start()
        button.setOnClickListener {
            progressBar.progressMax = Random().nextInt(15000)
            progressBar.postInvalidate()
            // 更新气泡位置
            container.updateBubble()
        }
    }

    private fun initDecoration() {
        val uri = Uri.parse("https://img.zcool.cn/community/01908756ca6bc332f875520fac8ae4.gif")
        Glide.with(this).load(uri).asGif().into(imageView)
    }

    private fun initBubble() {
        val adapter = BubbleAdapter()
        bubbleView.layoutManager = BubbleLayoutManager(50, adapter)
        bubbleView.adapter = adapter
    }

    private fun initFunc(){
        randomProgress.setOnClickListener(this::randomProgress)
        randomSubProgress.setOnClickListener(this::randomSubProgress)
        addBubble.setOnClickListener { (bubbleView.adapter as BubbleAdapter).addBubble() }
        reduceBubble.setOnClickListener { (bubbleView.adapter as BubbleAdapter).reduceBubble() }
        layoutBubble.setOnClickListener { container.updateBubble() }
    }

    private fun randomProgress(view: View) {
        // 500ms到达指定位置
        progressBar.progress(Random().nextInt(progressBar.progressMax - progressBar.progressMin + 1) + progressBar.progressMin, false, 500)
    }

    private fun randomSubProgress(view: View) {
        // 500ms到达指定位置
        progressBar.subProgress(Random().nextInt(progressBar.subProgressMax - progressBar.subProgressMin + 1) + progressBar.subProgressMin, false, 500)
    }
}
