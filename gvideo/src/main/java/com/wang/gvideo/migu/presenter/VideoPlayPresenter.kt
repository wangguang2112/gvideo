package com.wang.gvideo.migu.presenter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import com.hpplay.callback.HpplayWindowPlayCallBack
import com.hpplay.link.HpplayLinkControl
import com.leo.player.media.IjkVideoManager
import com.leo.player.media.StateChangeListener
import com.leo.player.media.controller.OnMoreInfoClickListener
import com.wang.gvideo.common.base.BasePresenter
import com.wang.gvideo.common.bus.RxBus
import com.wang.gvideo.common.dao.DataCenter
import com.wang.gvideo.common.net.ApiFactory
import com.wang.gvideo.common.net.OneSubScriber
import com.wang.gvideo.common.utils.empty
import com.wang.gvideo.common.utils.nil
import com.wang.gvideo.common.utils.safeGetRun
import com.wang.gvideo.migu.api.WapMiGuInter
import com.wang.gvideo.migu.cache.CacheManager
import com.wang.gvideo.migu.constant.BusKey
import com.wang.gvideo.migu.dao.model.ViewVideoDao
import com.wang.gvideo.migu.model.AppSearchListItem
import com.wang.gvideo.migu.model.VideoInfoModel
import com.wang.gvideo.migu.setting.Prefences
import com.wang.gvideo.migu.ui.dialog.SelectDefinitionDialog
import com.wang.gvideo.migu.ui.dialog.SelectDownloadDialog
import com.wang.gvideo.migu.ui.dialog.SelectSeasonDialog
import com.wang.gvideo.migu.ui.VideoPlayActivity
import com.wang.gvideo.migu.ui.VideoPlayHelper.VIDEO_CONT_ID
import com.wang.gvideo.migu.ui.VideoPlayHelper.VIDEO_CONT_ID_POS
import com.wang.gvideo.migu.ui.VideoPlayHelper.VIDEO_CONT_PLAY_POS
import com.wang.gvideo.migu.ui.VideoPlayHelper.VIDEO_CONT_SEASON_IDS
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.URLDecoder
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.inject.Inject

/**
 * Date:2018/4/12
 * Description:
 *
 * @author wangguang.
 */
class VideoPlayPresenter @Inject constructor(activity: VideoPlayActivity) : BasePresenter<VideoPlayActivity>(activity), OnMoreInfoClickListener, StateChangeListener,View.OnClickListener {

    var infoModel: VideoInfoModel? = null
    var seasonList: List<Pair<String, String>>? = null
    var position = 0
    var playPosition = 0
    override fun onCreate() {
        super.onCreate()
        val contId = activity.intent.getStringExtra(VIDEO_CONT_ID)
        activity.intent.getSerializableExtra(VIDEO_CONT_SEASON_IDS)?.let {
            seasonList = ( it as Array<Pair<String, String>>).toList()
        }
        playPosition = activity.intent.getIntExtra(VIDEO_CONT_PLAY_POS,0)
        position = activity.intent.getIntExtra(VIDEO_CONT_ID_POS, 0)
        if (contId.isNotEmpty()) {
            getVideoInfo(contId)
        }
    }

    override fun onInfoClick(type: Int) {
        when (type) {
            OnMoreInfoClickListener.TYPE_DEFINITION -> showSelectDefinitionDialog()
            OnMoreInfoClickListener.TYPE_DOWNLOAD -> showDownloadDialog()
            OnMoreInfoClickListener.TYPE_SEASON -> showSelectSeasonDialog()
        }
    }

    private fun showSelectDefinitionDialog() {
        SelectDefinitionDialog(activity)
                .setSelectListener { pos, url ->
                    Log.d(TAG, "pos" + pos)
                    activity.changeDefinition(VideoInfoModel.definitionName(pos), url)
                    Prefences.selectDefiniitionPrefence(pos)
                }
                .setModel(infoModel)
                .show()
    }

    private fun showSelectSeasonDialog() {
        seasonList?.let {
            SelectSeasonDialog(activity, it, position) { pos, dataItem ->
                setOnBusy(true)
                position = pos
                saveHistory()
                getVideoInfo(dataItem.first)
            }.show()
        }.nil { Toast.makeText(activity, "木有了~", Toast.LENGTH_SHORT).show() }
    }

    private fun showDownloadDialog() {
        setOnBusy(true,true)
        seasonList?.let {
            CacheManager.intance().checkIsDownload(it)
                    .doOnTerminate { setOnBusy(false) }
                    .subscribe(object :OneSubScriber<List<Pair<String, Boolean>>>(){
                        override fun onNext(t: List<Pair<String, Boolean>>) {
                            super.onNext(t)
                            SelectDownloadDialog(activity, t) {
                                Log.d(TAG, it.toString())
                                CacheManager.intance().downloadNew(t)
                            }.show()
                        }
                    })
        }
    }

    private fun getVideoInfo(contId: String) {
        val s = ApiFactory.INSTANCE()
                .createApi(WapMiGuInter::class.java)
//                        ApiFactory.createCookie("www.miguvideo.com", "UserInfo", "982324173|772E524A40FA31D9F78D"))
                .getVideoInfo(contId)
                .subscribeOn(Schedulers.io())
                .map {
                    it[0]
                }
                .map {
                    val newModel = it.copy()
                    val key = VideoInfoModel.getKey(it.func)
                    newModel.playList.play1 = getRealUrl(it.playList.play1, key)
                    newModel.playList.play2 = getRealUrl(it.playList.play2, key)
                    newModel.playList.play3 = getRealUrl(it.playList.play3, key)
                    newModel.playList.play4 = getRealUrl(it.playList.play4, key)
                    newModel.playList.play5 = getRealUrl(it.playList.play5, key)
                    newModel.pilotPlayList.play41 = getRealUrl(it.pilotPlayList.play41, key)
                    newModel.pilotPlayList.play42 = getRealUrl(it.pilotPlayList.play42, key)
                    newModel.pilotPlayList.play43 = getRealUrl(it.pilotPlayList.play43, key)
                    newModel.pilotPlayList.play44 = getRealUrl(it.pilotPlayList.play44, key)
                    newModel.pilotPlayList.play45 = getRealUrl(it.pilotPlayList.play45, key)
                    newModel
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate { setOnBusy(false) }
                .subscribe(object : OneSubScriber<VideoInfoModel>() {
                    override fun onNext(t: VideoInfoModel) {
                        super.onNext(t)
                        infoModel = t
                        //使用内置的分集
                        if(seasonList == null){
                            seasonList = infoModel?.getSeasonPairs()
                            if(!seasonList.empty()){
                                seasonList?.forEachIndexed { index, i ->
                                    if(i.first == contId){
                                        position = index
                                        return@forEachIndexed
                                    }
                                }
                            }
                        }
                        val url = t.definitionUrl(Prefences.getDefiniitionPrefence()) {
                            infoModel?.definPos = it
                        }
                        Log.d(TAG, url)
                        activity.startPlay(t.name, url, VideoInfoModel.definitionName(infoModel?.definPos ?: 0),playPosition)
                        playPosition = 0
                    }
                })
        addSubscription(s)
    }

    private fun getRealUrl(content: String, key: String): String {
        if (content.isNotEmpty()) {
            return URLDecoder.decode(decrypt(content, key))
        } else {
            return ""
        }

    }

    private fun decrypt(content: String, key: String): String {
        val cipher = Cipher.getInstance("DES/ECB/PKCS7Padding")
        val spec = DESKeySpec(key.toByteArray())
        val keys = SecretKeyFactory.getInstance("DES").generateSecret(spec)
        cipher.init(Cipher.DECRYPT_MODE, keys)
        return String(cipher.doFinal(Base64.decode(content, 0)))
    }

    override fun notifyPlayState(statue: Int) {
        if (statue == IjkVideoManager.STATE_PLAYBACK_COMPLETED) {
            seasonList.safeGetRun(position + 1) {
                saveHistory()
                Toast.makeText(activity, "马上下一集，稍后~", Toast.LENGTH_SHORT).show()
                setOnBusy(true)
                position += 1
                getVideoInfo(it.first)
                return@safeGetRun
            }
            Toast.makeText(activity, "播完啦~", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onStop() {
        super.onStop()
        saveHistory()
    }

    private fun saveHistory(){
        //存储播放位置
        infoModel?.let {
            val pos = IjkVideoManager.getInstance().currentPosition
            val all = IjkVideoManager.getInstance().duration
            val prec = if(all == 0){0}else{ pos * 100 /all}
            //大于95的 统统去掉
            if(prec in 1..100) {
                val info = ViewVideoDao(it.contId, it.name, it.imgH, pos, prec,System.currentTimeMillis())
                DataCenter.instance().insert(info)
                RxBus.instance().postEmptyEvent(BusKey.UPDATE_HISTORY_LIST)
            }else if(prec == 0 && pos > 0){
                val info = ViewVideoDao(it.contId, it.name, it.imgH, pos, prec,System.currentTimeMillis())
                DataCenter.instance().insert(info)
                RxBus.instance().postEmptyEvent(BusKey.UPDATE_HISTORY_LIST)
            }
        }
    }
    private fun showWirelessToTvDialog() {
        infoModel?.let { model ->
            activity.pauseVideo()
            HpplayLinkControl.getInstance().showHpplayWindow(activity,model.definitionUrl(Prefences.getDefiniitionPrefence()) , activity.getNowPosition() / 1000, object : HpplayWindowPlayCallBack {
                override fun onIsConnect(p0: Boolean) {
                    var msg = ""
                    if (p0) {
                        msg = "已连接"
                    } else {
                        msg = "连接已断开"
                    }
                    showMsg(msg)
                }

                override fun onIsPlaySuccess(p0: Boolean) {
                    showMsg("正在播放中")
                }

                override fun onHpplayWindowDismiss() {
//                    showMsg("关闭")
                }

            }, HpplayLinkControl.PUSH_VIDEO)
        }
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (p0.tag) {
                VideoPlayActivity.TAG_WIRELESS -> {
                    showWirelessToTvDialog()
                }
                else -> {
                }
            }
        }
    }


}