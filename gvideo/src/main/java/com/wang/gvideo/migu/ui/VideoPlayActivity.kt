package com.wang.gvideo.migu.ui

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.leo.player.media.IjkVideoManager
import com.leo.player.media.controller.MediaController
import com.leo.player.media.controller.OnMoreInfoClickListener
import com.wang.gvideo.R
import com.wang.gvideo.common.base.BaseActivity
import com.wang.gvideo.migu.component.DaggerVideoComponent
import com.wang.gvideo.migu.model.VideoInfoModel
import com.wang.gvideo.migu.presenter.VideoPlayPresenter
import kotlinx.android.synthetic.main.activity_video_play.*
import javax.inject.Inject

class VideoPlayActivity : BaseActivity() {

    @Inject
    lateinit var presenter: VideoPlayPresenter

    lateinit var controller: MediaController


    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
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

    fun startPlay(title: String, url: String,definition:String,playPos:Int = 0) {
        controller.setTitle(title)
        controller.setDefinition(definition)
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

    fun changeDefinition(newD: Int, url: String) {
        controller.setDefinition(VideoInfoModel.definitionName(newD))
        var lastPosition = IjkVideoManager.getInstance().currentPosition
//        IjkVideoManager.getInstance().pause()
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

    override fun onDestroy() {
        super.onDestroy()
        IjkVideoManager.getInstance().release()
    }

    override fun onPause() {
        super.onPause()
        IjkVideoManager.getInstance().pause()
    }

}
