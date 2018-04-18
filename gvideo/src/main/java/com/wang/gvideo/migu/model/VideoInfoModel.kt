package com.wang.gvideo.migu.model

import android.util.Log


/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
data class VideoInfoModel(val name: String,
                          val star: String,
                          val imgV: String,
                          val imgH: String,
                          val DisplayName: String,
                          val mediaType: String,
                          val contId: String,
        //JS 解密函数
                          val func: String,
                          val playList: PlayListModel,
                          val Variety:List<SeasonItem>,
                          val pilotPlayList: PilotPlayListModel,
                          var definPos: Int) {
    companion object {
        fun getKey(func: String): String {
            val result = "CryptoJS.enc.Utf8.parse\\('[\\w]*'\\)".toRegex()
                    .find(func)?.value ?: ""
            return result.substring(result.indexOf("('") + 2, result.indexOf("')"))
        }


        fun definitionName(pos: Int): String {
            return when (pos) {
                0 -> "普清"
                1 -> "高清"
                2 -> "720P"
                3 -> "1080P"
                else -> "普清"
            }
        }

    }

    fun getLow(): String {
        Log.d("VideoPlayPresenter","low:play1")
        return if (playList.play1.isNotEmpty()) {
            playList.play1
        } else {
//            pilotPlayList.play41
            ""
        }
    }

    fun getHigh(): String {
        Log.d("VideoPlayPresenter","high:play2")
        return if (playList.play2.isNotEmpty()) {
            playList.play2
        } else {
//            pilotPlayList.play42
            ""
        }
    }

    fun get720(): String {
        Log.d("VideoPlayPresenter","720:play3")
        return if (playList.play3.isNotEmpty()) {
            playList.play3
        } else {
//            pilotPlayList.play43
            ""
        }
    }

    fun get1080(): String {
        Log.d("VideoPlayPresenter","1080:play3")
        return if (playList.play5.isNotEmpty()) {
            playList.play5
        } else {
//            pilotPlayList.play45
            ""
        }
    }

    /**
     * 如果没有高清的往低清走
     */
    fun definitionUrl(pos: Int, callback: (Int) -> Unit = {}): String {
        val url = when (pos) {
            0 -> {
                getLow()
            }
            1 -> {
                getHigh()
            }
            2 -> {
                get720()
            }
            3 -> {
                get1080()
            }
            else -> {
                getLow()
            }
        }
        return if (pos in 1..3) {
            if(url != null && url.isNotEmpty()){
                 callback(pos)
                 url
            }else{
                definitionUrl(pos - 1, callback)
            }
        } else {
            callback(pos)
            url
        }
    }

    fun getSeasonPairs():List<Pair<String,String>>{
        val result = mutableListOf<Pair<String,String>>()
        Variety.forEach {
            result.add(it.changePair())
        }
        return result
    }
}