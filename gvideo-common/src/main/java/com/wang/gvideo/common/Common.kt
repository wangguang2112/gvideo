package com.wang.gvideo.common

import android.content.Context

/**
 * Date:2018/4/11
 * Description:
 *
 * @author wangguang.
 */

@SuppressWarnings("")
object Common {
    lateinit var appContext: Context

    fun initCommon(context: Context) {
        appContext = context
    }
}
