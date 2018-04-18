package com.wang.gvideo.common.base


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.os.Build


open class BaseDialog : Dialog {

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


