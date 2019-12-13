package com.abelhu.stepdemo.extension

inline fun <reified T> T.TAG(): String = T::class.java.simpleName