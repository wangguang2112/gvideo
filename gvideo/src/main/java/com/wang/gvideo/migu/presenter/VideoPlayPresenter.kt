package com.wang.gvideo.migu.presenter

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
import com.wang.gvideo.common.dao.IDaoAdapter
import com.wang.gvideo.common.net.OneSubScriber
import com.wang.gvideo.common.utils.empty
import com.wang.gvideo.common.utils.limit
import com.wang.gvideo.common.utils.notEmptyRun
import com.wang.gvideo.common.utils.safeGetRun
import com.wang.gvideo.migu.cache.CacheManager
import com.wang.gvideo.migu.cache.CacheTask
import com.wang.gvideo.migu.constant.BusKey
import com.wang.gvideo.migu.dao.CollectManager
import com.wang.gvideo.migu.dao.model.SeasonInfoDao
import com.wang.gvideo.migu.dao.model.ViewVideoDao
import com.wang.gvideo.migu.model.VideoInfoModel
import com.wang.gvideo.migu.play.*
import com.wang.gvideo.migu.setting.Prefences
import com.wang.gvideo.migu.ui.VideoPlayActivity
import com.wang.gvideo.migu.ui.dialog.SelectDefinitionDialog
import com.wang.gvideo.migu.ui.dialog.SelectDownloadDialog
import com.wang.gvideo.migu.ui.dialog.SelectSeasonDialog
import rx.Observable
import javax.inject.Inject

/**
 * Date:2018/4/12
 * Description:
 *
 * @author wangguang.
 */
class VideoPlayPresenter @Inject constructor(activity: VideoPlayActivity) : BasePresenter<VideoPlayActivity>(activity), OnMoreInfoClickListener, StateChangeListener, View.OnClickListener {

    var infoModel: VideoInfoModel? = null
    var seasonList: List<Pair<String, String>>? = null
    var position = 0
    var playPosition = 0
    /** 剧集更新时是否更新数据库 */
    var needUpdateSeason = false

    var nativiePath:String? = null
    var nativieName:String? = null

    override fun onCreate() {
        super.onCreate()
        val contId = activity.intent.getStringExtra(VIDEO_CONT_ID)
        activity.intent.getSerializableExtra(VIDEO_CONT_SEASON_IDS)?.let {
            seasonList = (it as Array<Pair<String, String>>).toList()
        }
        playPosition = activity.intent.getIntExtra(VIDEO_CONT_PLAY_POS, 0)
        position = activity.intent.getIntExtra(VIDEO_CONT_ID_POS, 0)
        needUpdateSeason = activity.intent.getBooleanExtra(VIDEO_WITH_SEASON_UPDATE, false)
        nativiePath = activity.intent.getStringExtra(VIDEO_NATIVE_NAME_PATH)
        nativieName = activity.intent.getStringExtra(VIDEO_NATIVE_NAME)
        if(nativiePath?.isNotEmpty() == true){
            activity.mainHandler.post {
                activity.playNative(nativieName,nativiePath!!)
            }
        }else if (contId.isNotEmpty()) {
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
        seasonList?.notEmptyRun {
            SelectSeasonDialog(activity, it as List<Pair<String, String>>, position) { pos, dataItem ->
                setOnBusy(true)
                position = pos
                saveHistory()
                getVideoInfo(dataItem.first)
            }.show()
            return
        }
        Toast.makeText(activity, "木有了~", Toast.LENGTH_SHORT).show()
    }

    private fun showDownloadDialog() {
        activity.pauseVideo()
        if(seasonList?.isNotEmpty() == true){
            CacheManager.intance()
                    .checkIsDownload(seasonList!!.map { it.first })
                    .doOnTerminate { setOnBusy(false) }
                    .subscribe(object : OneSubScriber<List<Pair<String, Boolean>>>() {
                        override fun onNext(t: List<Pair<String, Boolean>>) {
                            super.onNext(t)
                            SelectDownloadDialog(activity, t) {
                                Log.d(TAG, it.toString())
                                downloadList(it.map { it.first })
                            }.show()
                        }
                    })
        }else{
            infoModel?.let {
                if(!CacheManager.intance().checkHasSubmit(it.contId)) {
                    showMsg("开始下载：${it.name.limit(5)}")
                }else{
                    showMsg("已经下载中，不要点了~")
                }
                CacheManager.intance().submitNewTask(CacheTask.Builder()
                        .contId(it.contId)
                        .img(it.imgH)
                        .name(it.name)
                        .url(it.definitionUrl(Prefences.getDefiniitionPrefence()))
                        .build())
            }

        }
    }

    private fun downloadList(list: List<String>) {
        autoUnSubscribe {
            setOnBusy(true)
            Observable.from(list)
                    .onBackpressureBuffer()
                    .flatMap {
                        CidUrlConverter.getVideoObservibe(it)
                    }
                    .doOnTerminate { setOnBusy(false) }
                    .subscribe(object : OneSubScriber<VideoInfoModel>() {
                        override fun onNext(t: VideoInfoModel) {
                            super.onNext(t)
                            showMsg("开始下载：${t.name.limit(5)}")
                            CacheManager.intance().submitNewTask(CacheTask.Builder()
                                    .contId(t.contId)
                                    .img(t.imgH)
                                    .name(t.name)
                                    .url(t.definitionUrl(Prefences.getDefiniitionPrefence()))
                                    .build())
                        }
                    })
        }
    }




    private fun getVideoInfo(contId: String) {
        val s = CidUrlConverter.getVideoObservibe(contId)
                .doOnTerminate { setOnBusy(false) }
                .subscribe(object : OneSubScriber<VideoInfoModel>() {
                    override fun onNext(t: VideoInfoModel) {
                        super.onNext(t)
                        infoModel = t
                        //使用内置的分集
                        infoModel?.let {
                            updateSeason(it.contId, needUpdateSeason)
                        }
                        val url = t.definitionUrl(Prefences.getDefiniitionPrefence()) {
                            infoModel?.definPos = it
                        }
                        Log.d(TAG, url)
                        activity.startPlay(t.name, url, VideoInfoModel.definitionName(infoModel?.definPos ?: 0), playPosition)
                        playPosition = 0
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        Toast.makeText(activity,"视频信息获取失败",Toast.LENGTH_SHORT).show()
                    }
                })
        addSubscription(s)
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

    private fun saveHistory() {
        //存储播放位置
        infoModel?.let {
            val pos = IjkVideoManager.getInstance().currentPosition
            val all = IjkVideoManager.getInstance().duration
            val prec = if (all == 0) {
                0
            } else {
                pos * 100 / all
            }
            //大于95的 统统去掉
            if (prec in 1..100) {
                val info = ViewVideoDao(it.contId, it.name, it.imgH, pos, prec, System.currentTimeMillis())
                DataCenter.instance().insert(info)
                RxBus.instance().postEmptyEvent(BusKey.UPDATE_HISTORY_LIST)
            } else if (prec == 0 && pos > 0) {
                val info = ViewVideoDao(it.contId, it.name, it.imgH, pos, prec, System.currentTimeMillis())
                DataCenter.instance().insert(info)
                RxBus.instance().postEmptyEvent(BusKey.UPDATE_HISTORY_LIST)
            }
        }
    }

    private fun showWirelessToTvDialog() {
        infoModel?.let { model ->
            activity.pauseVideo()
            HpplayLinkControl.getInstance().showHpplayWindow(activity, model.definitionUrl(Prefences.getDefiniitionPrefence()), activity.getNowPosition() / 1000, object : HpplayWindowPlayCallBack {
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


    /**
     * 更新剧集
     * @param updateDao 是否更新数据库
     */
    private fun updateSeason(contId: String, updateDao: Boolean = false) {
        //使用内置的分集
        if (seasonList == null) {
            seasonList = infoModel?.getSeasonPairs()
            if (!seasonList.empty()) {
                seasonList?.forEachIndexed { index, i ->
                    if (i.first == contId) {
                        position = index
                        return@forEachIndexed
                    }
                }
            }
            if (updateDao) {
                seasonList?.let { season ->
                    addSeasonList(contId, season.mapIndexed { index, pair ->
                        Triple(pair.first, pair.second, position)
                    })
                }
                needUpdateSeason = false
                RxBus.instance().postSingleEvent(BusKey.UPDATE_COLLECT_LIST, contId)
            }
        } else {
            if (updateDao) {
                val newSeasonList = infoModel?.getSeasonPairs()
                newSeasonList.notEmptyRun {
                    if (it is List) {
                        if (seasonList!!.size < it.size) {
                            val tripleList = it.mapIndexed { index, pair ->
                                Triple(pair.first, pair.second, position)
                            }
                            val subList = tripleList.subList(seasonList!!.size, tripleList.size - 1)
                            addSeasonList(contId, subList)
                        }
                    }
                }
                RxBus.instance().postSingleEvent(BusKey.UPDATE_COLLECT_LIST, contId)
                needUpdateSeason = false
            }
        }
    }

    private fun addSeasonList(nodeId: String, list: List<Triple<String, String, Int>>) {
        CollectManager.manager.collectSeasonList(list, object : IDaoAdapter<Triple<String, String, Int>, SeasonInfoDao> {
            override fun adapt(t: Triple<String, String, Int>): SeasonInfoDao {
                return SeasonInfoDao(t.first, t.second, nodeId, t.third)
            }

            override fun reAdapt(t: SeasonInfoDao?): Triple<String, String, Int>? {
                if (t != null) {
                    return Triple(t.contId, t.name, t.position)
                }
                return null
            }

        })

    }


}