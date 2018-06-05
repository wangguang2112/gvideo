package com.wang.gvideo.migu.play

import android.content.Context

/**
 * Date:2018/6/5
 * Description:
 *
 * @author wangguang.
 */
interface IPlayHandler {
    fun startListVideoPlay(context: Context, contId: String, seasonList: List<Pair<String, String>>, pos: Int, update:Boolean)
    fun startSingleVideoPlay(context: Context, contId: String)
    fun startSingleVideoWithPos(context: Context, contId: String, pos: Int)
    fun startNativeVideo(context: Context,path: String, name: String,contId: String)
}