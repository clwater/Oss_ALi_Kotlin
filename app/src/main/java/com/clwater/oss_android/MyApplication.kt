package com.clwater.oss_android

import android.app.Application
import com.alibaba.sdk.android.oss.common.OSSLog

/**
 * @author: gengzhibo
 * @date: 2022/3/17
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initALiOss()
    }

    private fun initALiOss() {


        OSSLog.enableLog()
    }
}