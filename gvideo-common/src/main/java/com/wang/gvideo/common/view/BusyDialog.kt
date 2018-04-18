package com.wang.gvideo.common.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.KeyEvent
import com.wang.gvideo.common.R
import com.wang.gvideo.common.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_busy_layout.*
import java.util.*

/**
 * Date:2018/4/13
 * Description:
 *
 * @author wangguang.
 */
class BusyDialog(ctx: Context) : BaseDialog(ctx, R.style.BaseTranslucentDialog) {

    var canCancle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)
        setContentView(R.layout.dialog_busy_layout)
        busy_animation_view.setAnimation(if (Random().nextInt() % 2 == 0) {
            "busy_anim_1.json"
        } else {
            "busy_anim_2.json"
        })
        busy_animation_view.playAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        busy_animation_view.cancelAnimation()
    }

    fun canCancle(can: Boolean) {
        canCancle = can
        setCanceledOnTouchOutside(canCancle)
        if (can) {
            val parent = busy_animation_continer.parent
            if (parent is View) {
                parent.setOnClickListener { dismiss() }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (!canCancle && keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}