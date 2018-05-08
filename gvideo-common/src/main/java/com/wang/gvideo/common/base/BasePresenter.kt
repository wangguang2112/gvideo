package com.wang.gvideo.common.base

import android.widget.Toast
import com.wang.gvideo.common.net.ApiFactory
import rx.Subscription

/**
 * Date:2018/4/12
 * Description:
 *
 * @author wangguang.
 */

open class BasePresenter<out AC> constructor(protected val activity: AC) : IPresenter {


    protected val TAG = this::class.simpleName
    override fun onCreate() {

    }

    override fun onStart() {

    }

    override fun onStop() {

    }
    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun onDestory() {

    }

    fun setOnBusy(isbusy: Boolean,cancancle: Boolean = false) {
        if (activity is BaseActivity) {
            activity.setOnBusy(isbusy,cancancle)
        }
    }

    fun <T> doHttp(cls: Class<T>): T {
        return ApiFactory.INSTANCE().createApi(cls)
    }

   fun addSubscription(s: Subscription) {
       if (activity is BaseActivity) {
           activity.addSubscription(s)
       }
    }

    fun autoUnSubscribe(s: () -> Subscription) {
        if (activity is BaseActivity) {
            activity.addSubscription(s())
        }
    }

    fun showMsg(msg:String){
        if (activity is BaseActivity) {
            activity.mainHandler.post {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

}
