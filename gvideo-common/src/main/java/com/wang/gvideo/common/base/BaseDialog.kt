package com.wang.gvideo.common.base


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.os.Build
import android.os.Handler
import android.os.Message


open class BaseDialog : Dialog {

    protected var mCallback: Handler.Callback? = null
    var mainHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            mCallback?.let {
                if (it.handleMessage(msg)) {
                    return
                } else {
                    super.handleMessage(msg)
                }
            }
        }
    }

    protected fun addHandlerCallBack(callback: Handler.Callback) {
        this.mCallback = callback
    }
    constructor(context: Context) : super(context)

    constructor(context: Context, theme: Int) : super(context, theme)

    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener) : super(context, cancelable, cancelListener)

    override fun show() {
        if (checkDialogCanShow(context)) {
            super.show()
        }
    }

    private fun checkDialogCanShow(context: Context?): Boolean {
        return context?.let { it ->
            var ctx = it
            if (ctx is ContextWrapper) {
                ctx = ctx.baseContext
            }
            if (ctx is Activity) {
                if (Build.VERSION.SDK_INT >= 18) !ctx.isDestroyed else !ctx.isFinishing
            } else {
                false
            }

        } == true
    }
}


