package com.wang.gvideo.migu.setting

import com.wang.gvideo.common.bus.RxBus
import com.wang.gvideo.common.utils.SharedPreferencesUtil
import com.wang.gvideo.migu.constant.BusKey

/**
 * Date:2018/4/14
 * Description:
 *
 * @author wangguang.
 */

const val MODE_NORMAL = 0
const val MODE_TV = 1

object Prefences {
    const val DEFINIITION_PREFENCE_KEY = "definiition_prefence_key"
    const val DEFINIITION_RATE_PREFENCE_KEY = "definiition_rate_prefence_key"
    const val IS_ALWAYS_TV_MODE = "is_always_tv_mode"

    fun selectDefiniitionPrefence(pos: Int) {
        SharedPreferencesUtil.instance.setInt(DEFINIITION_PREFENCE_KEY, pos)
    }

    fun getDefiniitionPrefence(): Int {
        return SharedPreferencesUtil.instance.getInt(DEFINIITION_PREFENCE_KEY, 0)
    }

    fun selectDefiniitionRate(rate: Int) {
        SharedPreferencesUtil.instance.setInt(DEFINIITION_RATE_PREFENCE_KEY, rate)
    }

    fun getDefiniitionRate(): Int {
        return SharedPreferencesUtil.instance.getInt(DEFINIITION_RATE_PREFENCE_KEY, 50)
    }

    fun getVideoMode(): Int {
        return SharedPreferencesUtil.instance.getInt(IS_ALWAYS_TV_MODE, 0)
    }

    fun setVideoMode(mode:Int) {
        SharedPreferencesUtil.instance.setInt(IS_ALWAYS_TV_MODE, mode)
        RxBus.instance().postEmptyEvent(BusKey.UPDATE_PLAY_MODE)
    }
}