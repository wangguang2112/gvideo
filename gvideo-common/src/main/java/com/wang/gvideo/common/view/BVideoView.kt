package com.wang.gvideo.common.view

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
//import tv.danmaku.ijk.media.player.IMediaPlayer
//import tv.danmaku.ijk.media.player.IjkMediaPlayer


/**
 * Date:2018/4/2
 * Description:
 *
 * @author wangguang.
 */
/*
class BVideoView(ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(ctx, attrs, defStyleAttr) {

    val TAG = "BVideoView"

    var playPath = ""
    var mMediaPlayer: IMediaPlayer? = null
    var mListener: VideoPlayerListener? = null
        set(value) {
            mMediaPlayer?.setOnPreparedListener(value)
            field = value
        }

    lateinit var surfaceView: SurfaceView

    lateinit var playBT: ImageView

    init {
        this.isFocusable = true
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG)
        createSurfaceView()

    }

    constructor(ctx: Context, attrs: AttributeSet) : this(ctx, attrs, 0)
    constructor(ctx: Context) : this(ctx, null, 0)

    fun setVideoPath(path: String) {
        if (TextUtils.equals("", playPath)) {
            //如果是第一次播放视频，那就创建一个新的surfaceView
            playPath = path
        } else {
            //否则就直接load
            playPath = path
            load()
        }
    }


    private fun createSurfaceView() {
        surfaceView = SurfaceView(context)
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {

            }

            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                this@BVideoView.load()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {

            }
        })
        surfaceView.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        this.addView(surfaceView)
        playBT = ImageView(context)
        playBT.scaleType = ImageView.ScaleType.CENTER_INSIDE
        playBT.setImageResource(android.R.drawable.ic_media_play)
        var params = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
        playBT.layoutParams = params
        playBT.visibility = GONE
        addView(playBT)
    }

    private fun load() {
        createPlayer()
        mMediaPlayer?.apply {
            dataSource = playPath
            setDisplay(surfaceView.holder)
            prepareAsync()
        }
    }


    private fun createPlayer() {
        mMediaPlayer?.apply {
            stop()
            setDisplay(null)
            release()
        }
        val temp = IjkMediaPlayer()
        temp.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)
        mMediaPlayer = temp
        mListener?.let {
            mMediaPlayer?.apply {
                setOnPreparedListener(mListener)
                setOnInfoListener(mListener)
                setOnSeekCompleteListener(mListener)
                setOnBufferingUpdateListener(mListener)
                setOnErrorListener(mListener)
                setOnVideoSizeChangedListener(mListener)
            }
        }

    }

    fun start() {
        playBT.visibility = View.GONE
        mMediaPlayer?.start()
    }

    fun isPlaying(): Boolean {
        return mMediaPlayer?.isPlaying ?: false
    }


    fun release() {
        mMediaPlayer?.apply {
            reset()
            release()
        }
    }

    fun pause() {
        playBT.visibility = View.VISIBLE
        mMediaPlayer?.pause()
    }

    fun stop() {
        mMediaPlayer?.stop()
    }

    fun reset() {
        mMediaPlayer?.reset()
    }

    fun getDuration(): Long {
        return mMediaPlayer?.let { it.duration } ?: 0
    }

    fun getCurrentPosition(): Long {
        return mMediaPlayer?.let { it.currentPosition } ?: 0
    }

    fun seekTo(l: Long) {
        mMediaPlayer?.seekTo(l)
    }

    class VideoPlayerListener : IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnVideoSizeChangedListener {
        override fun onVideoSizeChanged(p0: IMediaPlayer?, p1: Int, p2: Int, p3: Int, p4: Int) {
            Log.d("VideoPlayerListener", "onVideoSizeChanged:p1=$p1,p2=$p2")
        }

        override fun onInfo(p0: IMediaPlayer?, p1: Int, p2: Int): Boolean {
            Log.d("VideoPlayerListener", "onInfo:p1=$p1,p2=$p2")
            return false
        }

        override fun onSeekComplete(p0: IMediaPlayer?) {
            Log.d("VideoPlayerListener", "onSeekComplete")
        }

        override fun onBufferingUpdate(p0: IMediaPlayer?, p1: Int) {
            Log.d("VideoPlayerListener", "onBufferingUpdate:p1=$p1")
        }

        override fun onError(p0: IMediaPlayer?, p1: Int, p2: Int): Boolean {
            Log.d("VideoPlayerListener", "onError:p1=$p1,p2=$p2")
            return false
        }

        override fun onPrepared(p0: IMediaPlayer?) {
            Log.d("VideoPlayerListener", "onPrepared")
            p0?.start()
        }

    }
}*/
