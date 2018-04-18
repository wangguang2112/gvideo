package com.wang.gvideo.common.base

/**
 * Date:2018/4/12
 * Description:
 *
 * @author wangguang.
 */

interface IPresenter{
    fun onCreate()
    fun onResume()
    fun onPause()
    fun onDestory()
    fun onStop()
    fun onStart()
}
