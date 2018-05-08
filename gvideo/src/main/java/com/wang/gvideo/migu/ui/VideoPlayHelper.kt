package com.wang.gvideo.migu.ui

import android.content.Context
import android.content.Intent

/**
 * Date:2018/4/25
 * Description:
 *
 * @author wangguang.
 */
object VideoPlayHelper {
    val VIDEO_CONT_ID = "video_cont_id"
    val VIDEO_CONT_SEASON_IDS = "video_cont_season_ids"
    val VIDEO_CONT_ID_POS = "video_cont_id_pos"
    val VIDEO_CONT_PLAY_POS = "video_cont_play_pos"
    val VIDEO_WITH_SEASON_UPDATE = "video_with_season_update"
    val VIDEO_NATIVE_NAME = "video_native_name"
    val VIDEO_NATIVE_NAME_PATH = "video_native_name_path"

    fun startSingleVideoPlay(context: Context, contId: String) {
        val intent = Intent(context, VideoPlayActivity::class.java)
        intent.putExtra(VIDEO_CONT_ID, contId)
        context.startActivity(intent)
    }

    fun startSingleVideoWithPos(context: Context, contId: String, pos: Int) {
        val intent = Intent(context, VideoPlayActivity::class.java)
        intent.putExtra(VIDEO_CONT_ID, contId)
        intent.putExtra(VIDEO_CONT_PLAY_POS, pos)
        context.startActivity(intent)
    }

    fun startListVideoPlay(context: Context, contId: String, seasonList: List<Pair<String, String>>, pos: Int,update:Boolean = false) {
        val intent = Intent(context, VideoPlayActivity::class.java)
        intent.putExtra(VIDEO_CONT_ID, contId)
        intent.putExtra(VIDEO_CONT_SEASON_IDS, seasonList.toTypedArray())
        intent.putExtra(VIDEO_CONT_ID_POS, pos)
        intent.putExtra(VIDEO_WITH_SEASON_UPDATE, update)
        context.startActivity(intent)
    }

    fun startNativeVideo(context: Context,path: String, name: String = "",contId: String = "") {
        val intent = Intent(context, VideoPlayActivity::class.java)
        intent.putExtra(VIDEO_NATIVE_NAME, name)
        intent.putExtra(VIDEO_CONT_ID, contId)
        intent.putExtra(VIDEO_NATIVE_NAME_PATH, path)
        context.startActivity(intent)
    }
}