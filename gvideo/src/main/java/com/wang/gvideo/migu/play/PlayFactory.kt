package com.wang.gvideo.migu.play

import com.wang.gvideo.migu.setting.MODE_NORMAL
import com.wang.gvideo.migu.setting.MODE_TV

/**
 * Date:2018/6/5
 * Description:
 *
 * @author wangguang.
 */
class PlayFactory {

    fun products(mode: Int): IPlayHandler {
        return when (mode) {
            MODE_NORMAL -> PhonePlayHandler()
            MODE_TV -> TVPlayHandler()
            else -> PhonePlayHandler()
        }
    }

}