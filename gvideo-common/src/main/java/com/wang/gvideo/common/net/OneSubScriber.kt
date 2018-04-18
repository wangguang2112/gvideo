package com.wang.gvideo.common.net

import android.util.Log

import rx.Subscriber

/**
 * Created by wangguang.
 * Date:2017/1/4
 * Description:
 */

open class OneSubScriber<T> : Subscriber<T>() {

    override fun onCompleted() {

    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        Log.w("OneSubScriber", "onError: " + e.message)
    }

    override fun onNext(t: T) {

    }


}
