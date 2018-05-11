package com.wang.gvideo

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import com.wang.gvideo.common.base.BaseActivity
import com.wang.gvideo.common.utils.SharedPreferencesUtil
import com.wang.gvideo.migu.cache.CacheManager
import com.wang.gvideo.migu.constant.SpKey
import com.wang.gvideo.migu.ui.VideoFirstAcitivity
import kotlinx.android.synthetic.main.activity_launch.*

class LaunchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setWindowStatusBarColor(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        SpKey.MIGU_VIDEO_LAUNCH_ANIM_POS
        showAnim()
        initConfig()
    }

    private fun initConfig() {
        CacheManager.intance()
    }

    fun showAnim() {
        val animName = getAnimName()
        Log.d(TAG,animName)
        animation_name.text = animName
        animation_view.setAnimation(animName)
        animation_view.playAnimation()
        mainHandler.postDelayed({
            startLaunch()
        }, 2000)
        animation_view.setOnClickListener {
            startLaunch()
        }
    }

    var hasStart = false
    private fun startLaunch() {
        if(!hasStart) {
            animation_view.pauseAnimation()
            val intent = Intent(this, VideoFirstAcitivity::class.java)
            intent.flags += Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
            hasStart = true
            this.finish()
        }
    }

    private fun getAnimName(): String {
        val pos = SharedPreferencesUtil.instance.getInt(SpKey.MIGU_VIDEO_LAUNCH_ANIM_POS, 0)

        val animName = assets.list("")
                .filter {
                    it.endsWith(".json")
                }
        return if (animName.isNotEmpty()) {
            if (pos < animName.size) {
                SharedPreferencesUtil.instance.setInt(SpKey.MIGU_VIDEO_LAUNCH_ANIM_POS, pos + 1)
                animName[pos]
            } else {
                SharedPreferencesUtil.instance.setInt(SpKey.MIGU_VIDEO_LAUNCH_ANIM_POS, pos % animName.size + 1)
                animName[pos % animName.size]
            }
        } else {
            ""
        }

    }

    private fun setWindowStatusBarColor(activity: Activity) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = activity.window
//                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//                window.statusBarColor = Color.parseColor("#2A2F36")
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 如果是返回键,直接返回到桌面
            val intent = Intent(Intent.ACTION_MAIN)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK// 注意
            intent.addCategory(Intent.CATEGORY_HOME)
            startActivity(intent)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        animation_view.cancelAnimation()
    }
}
