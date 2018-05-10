package com.wang.gvideo.migu.ui

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import com.code19.library.DensityUtil
import com.leo.player.media.IjkVideoManager
import com.leo.player.media.controller.MediaController
import com.wang.gvideo.R
import com.wang.gvideo.common.base.BaseActivity
import com.wang.gvideo.migu.component.DaggerVideoComponent
import com.wang.gvideo.migu.presenter.VideoPlayPresenter
import kotlinx.android.synthetic.main.activity_video_play.*
import javax.inject.Inject

class VideoPlayActivity : BaseActivity() {
    companion object {
        const val TAG_WIRELESS = "wireless_TV"
    }

    @Inject
    lateinit var presenter: VideoPlayPresenter

    lateinit var controller: MediaController

    var isAddProject = false


    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        DaggerVideoComponent.builder().setContext(this).build().inject(this)
        setPresenter(presenter)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        initVideoView()
    }

    private fun initVideoView() {
        controller = MediaController(this)
        controller.setShowThumb(true)
        controller.setCheckWifi(true)
        controller.setPreparedPlay(true)
        controller.setEnableOrientation(false)
        controller.setShowFullscreenBt(false)
        controller.setFullScreenMode(MediaController.FULLSCREEN_VIEW)
        controller.setFullScreenViewEnableSlidePosition(false)
        controller.setShowTitle(true)
        controller.setShowMoreOption(true)
        controller.setDefinition("普清")
        controller.setOnMoreInfoClickListener(presenter)
        controller.setOutStateChangeListener(presenter)
        controller.setShowBottomProgress(false)
        video_view.setMediaController(controller)
    }

    fun playNative(title: String?, path: String,playPos:Int = 0){
        controller.setShowMoreOption(false)
        if(title?.isNotEmpty() == true){
            controller.setTitle(title)
        }else{
            controller.setShowTitle(false)
        }
        video_view.setVideoPath(path)
        if(playPos > 0) {
            IjkVideoManager.getInstance().seekTo(if (playPos > 5000) {
                playPos - 5000
            } else {
                playPos
            })
        }
        controller.setPreparedPlay(true)
        video_view.openVideo()
    }

    fun startPlay(title: String, url: String,definition:String,playPos:Int = 0) {
        controller.setTitle(title)
        controller.setDefinition(definition)
        if(!isAddProject){
            controller.addHeadMoreView(getProjectionScreenIcon())
            isAddProject = true
        }
        if(IjkVideoManager.getInstance().isPlaying){
            IjkVideoManager.getInstance().release()
            mainHandler.postDelayed({
                video_view.setVideoPath(url)
                controller.setPreparedPlay(true)
                video_view.openVideo()
                if(playPos > 0) {
                    IjkVideoManager.getInstance().seekTo(if (playPos > 5000) {
                        playPos - 5000
                    } else {
                        playPos
                    })
                }
            },300)
        }else{
            video_view.setVideoPath(url)
            video_view.openVideo()
            if(playPos > 0) {
                IjkVideoManager.getInstance().seekTo(if (playPos > 5000) {
                    playPos - 5000
                } else {
                    playPos
                })
            }
        }
    }

    fun getProjectionScreenIcon():View{
        val scr = ImageView(this)
        scr.scaleType = ImageView.ScaleType.CENTER_INSIDE
        scr.setImageResource(R.drawable.wirelesstv)
        val params = LinearLayout.LayoutParams(DensityUtil.dip2px(this,30f),DensityUtil.dip2px(this,30f))
        params.gravity = Gravity.CENTER_VERTICAL
        params.rightMargin = DensityUtil.dip2px(this,15f)
        scr.layoutParams = params
        scr.tag = TAG_WIRELESS
        scr.setOnClickListener(presenter)
        return scr
    }

    fun changeDefinition(definition: String, url: String) {
        controller.setDefinition(definition)
        var lastPosition = IjkVideoManager.getInstance().currentPosition
        IjkVideoManager.getInstance().release()
        mainHandler.postDelayed({
            video_view.setVideoPath(url)
            controller.setPreparedPlay(true)
            video_view.openVideo()
            Log.d(TAG,"1::"+video_view.currentUri.toString())
            IjkVideoManager.getInstance().seekTo(if (lastPosition > 5000) {
                lastPosition - 5000
            } else {
                lastPosition
            })
        }, 300)
    }

    fun pauseVideo(){
        if(IjkVideoManager.getInstance().isPlaying) {
            IjkVideoManager.getInstance().pause()
        }
    }

    fun resumeVideo(){
        if(!IjkVideoManager.getInstance().isPlaying){
            IjkVideoManager.getInstance().start()
        }
    }
    fun getNowPosition():Int{
        return IjkVideoManager.getInstance().currentPosition
    }
    override fun onDestroy() {
        super.onDestroy()
        IjkVideoManager.getInstance().release()
    }

    override fun onPause() {
        super.onPause()
        IjkVideoManager.getInstance().pause()
    }

    override fun onStop() {
        super.onStop()
        IjkVideoManager.getInstance().pause()
    }
}
