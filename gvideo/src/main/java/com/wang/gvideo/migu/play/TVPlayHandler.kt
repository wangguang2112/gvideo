package com.wang.gvideo.migu.play

import android.content.Context
import android.content.Intent
import com.hpplay.callback.HpplayWindowPlayCallBack
import com.hpplay.link.HpplayLinkControl
import com.wang.gvideo.common.base.BaseActivity
import com.wang.gvideo.common.net.OneSubScriber
import com.wang.gvideo.migu.setting.Prefences
import com.wang.gvideo.migu.ui.VideoPlayActivity
import java.util.*


/**
 * Date:2018/6/5
 * Description:
 *
 * @author wangguang.
 */
class TVPlayHandler : IPlayHandler {


    override fun startNativeVideo(context: Context, path: String, name: String, contId: String) {
        val intent = Intent(context, VideoPlayActivity::class.java)
        intent.putExtra(VIDEO_NATIVE_NAME, name)
        intent.putExtra(VIDEO_CONT_ID, contId)
        intent.putExtra(VIDEO_NATIVE_NAME_PATH, path)
        context.startActivity(intent)
    }


    override fun startSingleVideoPlay(context: Context, contId: String) {
        getUrlAndPush(context, contId)
    }

    override fun startSingleVideoWithPos(context: Context, contId: String, pos: Int) {
        getUrlAndPush(context, contId, pos)
    }

    override fun startListVideoPlay(context: Context, contId: String, seasonList: List<Pair<String, String>>, pos: Int, update: Boolean) {
        getUrlAndPush(context, contId, pos)
    }


    private fun getUrlAndPush(context: Context, condId: String, pos: Int = 0) {
        var activity = context
        if (activity is BaseActivity) {
            activity.setOnBusy(true)
            CidUrlConverter.getVideoObservibe(condId)
                    .map {
                        it.definitionUrl(Prefences.getDefiniitionPrefence())
                    }
                    .doOnTerminate { activity.setOnBusy(false) }
                    .subscribe(object : OneSubScriber<String>() {
                        override fun onNext(t: String) {
                            super.onNext(t)
                            pushToTv(activity, t, pos)
                        }
                    })
        }
    }

    private fun pushToTv(activity: BaseActivity, url: String, pos: Int) {
        if(HpplayLinkControl.getInstance().isConnect){
            val code = Random().nextInt() % 100
            HpplayLinkControl.getInstance().castStartMediaPlay({obj,port->
                if(code == port){
                    if(obj is Boolean){
                        activity.showMsg("推送成功")
                    }else{
                        activity.showMsg("推送失败")
                    }
                }
            },code,url,HpplayLinkControl.PUSH_VIDEO)
        }else {
            HpplayLinkControl.getInstance().showHpplayWindow(activity, url, pos / 1000, object : HpplayWindowPlayCallBack {
                override fun onIsConnect(p0: Boolean) {
                    var msg = ""
                    if (p0) {
                        msg = "已连接"
                    } else {
                        msg = "连接已断开"
                    }
                    activity.showMsg(msg)
                }

                override fun onIsPlaySuccess(p0: Boolean) {
                    activity.showMsg("正在播放中")
                }

                override fun onHpplayWindowDismiss() {
                }

            }, HpplayLinkControl.PUSH_VIDEO)
        }
    }
}