package com.qicode.extension

import android.content.res.Resources

val Int.sp: Float
    get() = this * Resources.getSystem().displayMetrics.density
val Int.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density