package com.wang.gvideo.common.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.wang.gvideo.common.net.ApiFactory
import com.wang.gvideo.common.view.BusyDialog
import rx.Observable
import rx.Subscription
import java.util.*

open class BaseActivity : AppCompatActivity() {
    protected open var list: MutableList<Subscription> = ArrayList()
    private var mDialog: BusyDialog? = null

    protected open val TAG = this.javaClass.simpleName

    private var presenterHolder: IPresenter? = null

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

    protected fun setPresenter(holder: IPresenter) {
        presenterHolder = holder
    }

    protected fun addHandlerCallBack(callback: Handler.Callback) {
        this.mCallback = callback
    }

    override fun onDestroy() {
        super.onDestroy()
        presenterHolder?.onDestory()
        for (s in list) {
            s.unsubscribe()
        }
        list.clear()
        presenterHolder = null
    }

    fun addSubscription(s: Subscription) {
        list.add(s)
    }

    fun autoUnSubscribe(s: () -> Subscription) {
        list.add(s())
    }

    fun setOnBusy(isbusy: Boolean, cancancle: Boolean = false) {
        if (mDialog == null && isbusy) {
            mDialog = BusyDialog(this)
            mDialog?.show()
            mDialog?.canCancle(cancancle)
        } else if (isbusy) {
            mDialog?.canCancle(cancancle)
            mDialog?.show()
        } else {
            mDialog?.dismiss()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenterHolder?.onCreate()
    }

    override fun onPause() {
        super.onPause()
        presenterHolder?.onPause()
    }

    override fun onResume() {
        super.onResume()
        presenterHolder?.onResume()
    }

    override fun onStart() {
        super.onStart()
        presenterHolder?.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenterHolder?.onStop()
    }

    fun showMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


    protected fun <T> doHttp(cls: Class<T>): T {
        return ApiFactory.INSTANCE().createApi(cls)
    }

    fun hideIMSoftKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            val focusView = currentFocus
            if (focusView != null) {
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
            }
        }
    }

    private var lastClick = 0L
    lateinit var toast: Toast
    /**
     * 此处控制是否连续点击两次才推出
     */
    protected var isDoubleReturn = false

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val nowTime = System.currentTimeMillis()
        if (keyCode == KeyEvent.KEYCODE_BACK && isDoubleReturn) {
            return if (nowTime - lastClick > 0
                    && nowTime - lastClick < 2 * 1000) {
                toast.cancel()
                super.onKeyDown(keyCode, event)
            } else {
                lastClick = nowTime
                toast.cancel()
                toast = Toast.makeText(this, "再点击一次退出", Toast.LENGTH_SHORT)
                toast.show()
                true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}

