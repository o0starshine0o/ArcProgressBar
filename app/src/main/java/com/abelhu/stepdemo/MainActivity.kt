package com.abelhu.stepdemo

import android.animation.AnimatorInflater
import android.graphics.Color
import android.graphics.Path
import android.graphics.Region
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
    companion object {
        // 暂定圆圈半径为20
        private val defaultBubbleSize = 20.dp
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initContainer()
        initProgress()
        initButton()
        initDecoration()

        recyclerView.layoutManager = BubbleLayoutManager()
        recyclerView.adapter = BubbleAdapter()
    }

    private fun initContainer() {
        container.apply {
            // 地面颜色
            groundColor = Color.WHITE
            // 基地高度
            groundHeight = 40.dp
            // 地面高度
            groundSize = 26.dp
            // 圆滑程度
            smooth = 0.2f
        }
        container.post {
            val region = Region(0, 0, container.measuredWidth, container.measuredHeight)
            region.setPath(container.getPath(Path(), defaultBubbleSize), region)
            (recyclerView.layoutManager as? BubbleLayoutManager)?.exclude(region)
        }
    }

    private fun initProgress() {
        randomProgress.setOnClickListener(this::randomProgress)
        randomSubProgress.setOnClickListener(this::randomSubProgress)
        progressBar.apply {
            // 中间各类文本
            title = DrawString("今日步数", Color.BLACK, 180f, 0.30f, 20.sp)
            titleDesc = DrawString("6666", Color.BLACK, 180f, 0.06f, 50.sp)
            subTitle = DrawString("运动步数", Color.BLACK, 0f, 0.12f, 20.sp)
            subTitleDesc = DrawString("6363", Color.BLACK, 0f, 0.30f, 30.sp)
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
        progressBar.post {
            progressBar.apply {
                val region = Region(0, 0, container.measuredWidth, container.measuredHeight)
                region.setPath(progressBar.getPath(Path(), defaultBubbleSize), region)
                (recyclerView.layoutManager as? BubbleLayoutManager)?.exclude(region)
            }
        }
    }

    private fun initButton() {
        button.text = "领取奖励"
        val animator = AnimatorInflater.loadAnimator(this, R.animator.scale_90_2s_repeat)
        animator.setTarget(button)
        animator.start()
        button.setOnClickListener { recyclerView.requestLayout() }
    }

    private fun initDecoration() {
        val uri = Uri.parse("https://img.zcool.cn/community/01908756ca6bc332f875520fac8ae4.gif")
        Glide.with(this).load(uri).asGif().into(imageView)
        imageView.post {
            imageView.apply {
                val region = Region(left - defaultBubbleSize.toInt(), top - defaultBubbleSize.toInt(), right + defaultBubbleSize.toInt(),
                        bottom + defaultBubbleSize.toInt())
                (recyclerView.layoutManager as? BubbleLayoutManager)?.exclude(region)
            }
        }
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
