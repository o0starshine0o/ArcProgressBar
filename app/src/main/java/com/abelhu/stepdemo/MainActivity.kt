package com.abelhu.stepdemo

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.qicode.arcprogressbar.DrawString
import com.qicode.extension.dp
import com.qicode.extension.sp
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initProgress()

        randomProgress.setOnClickListener(this::randomProgress)
        randomSubProgress.setOnClickListener(this::randomSubProgress)
    }

    private fun initProgress() {
        progressBar.apply {
            // 中间各类文本
            title = DrawString("今日步数", Color.BLACK, 180f, 0.4f, 20.sp)
            titleDesc = DrawString("6666", Color.BLACK, 180f, 0.12f, 50.sp)
            subTitle = DrawString("运动步数", Color.BLACK, 0f, 0.08f, 20.sp)
            subTitleDesc = DrawString("6363", Color.BLACK, 0f, 0.28f, 30.sp)
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
            scaleInsidePercent = 0.8f
            scaleOutsidePercent = 0.88f
            scaleOutsideSpecialPercent = 0.91f
            // 进度条半径百分比
            progressBarPercent = 0.75f
            // 特殊刻度
            specialScales = listOf(9, 29, 49)
            // 是否显示坐标
            showCoordinate = false
            // 是否显示刻度值
            showScaleValue = true
            // 刻度值字体大小
            scaleValueSize = 16.sp
            // 刻度值字体颜色
            scaleValueColor = Color.WHITE
            // 刻度值半径百分比
            scaleValuePercent = 0.62f
            // 背景渐变色
            backColors = intArrayOf(Color.BLUE, Color.RED)
            // 背景渐变色分布(默认平均分布)
            backColorPositions = null
        }
    }

    private fun randomProgress(view: View) {
        // 500ms到达指定位置
        progressBar.progress(Random.nextInt(progressBar.progressMin, progressBar.progressMax), false, 500)
    }

    private fun randomSubProgress(view: View) {
        // 500ms到达指定位置
        progressBar.subProgress(Random.nextInt(progressBar.subProgressMin, progressBar.subProgressMax), false, 500)
    }
}
