package com.wang.gvideo.migu.play

import android.content.Context
import android.content.Intent
import com.wang.gvideo.common.bus.RxBus
import com.wang.gvideo.migu.constant.BusKey
import com.wang.gvideo.migu.setting.MODE_NORMAL
import com.wang.gvideo.migu.setting.Prefences
import com.wang.gvideo.migu.ui.VideoPlayActivity

/**
 * Date:2018/4/25
 * Description:
 *
 * @author wangguang.
 */

object VideoPlayHelper {

    val playFactory = PlayFactory()
    var playHandler = playFactory.products(Prefences.getVideoMode())

    init {
        RxBus.instance().toObservableOnMain(BusKey.UPDATE_PLAY_MODE)
                .subscribe {
                    playHandler = playFactory.products(Prefences.getVideoMode())
                }
    }
    fun startSingleVideoPlay(context: Context, contId: String) {
        playHandler.startSingleVideoPlay(context, contId)
    }

    fun startSingleVideoWithPos(context: Context, contId: String, pos: Int) {
        playHandler.startSingleVideoWithPos(context, contId, pos)
    }

    fun startListVideoPlay(context: Context, contId: String, seasonList: List<Pair<String, String>>, pos: Int, update: Boolean = false) {
        playHandler.startListVideoPlay(context, contId, seasonList, pos, update)
    }

    fun startNativeVideo(context: Context, path: String, name: String = "", contId: String = "") {
        playHandler.startNativeVideo(context, path, name, contId)
    }
}