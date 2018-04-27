package com.wang.gvideo.migu.presenter

import android.view.View
import android.util.Log
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
import com.wang.gvideo.common.net.ApiFactory
import com.wang.gvideo.common.net.OneSubScriber
import com.wang.gvideo.common.utils.empty
import com.wang.gvideo.common.utils.nil
import com.wang.gvideo.common.utils.notEmptyRun
import com.wang.gvideo.common.utils.safeGetRun
import com.wang.gvideo.migu.api.MiGuMovieInter
import com.wang.gvideo.migu.cache.CacheManager
import com.wang.gvideo.migu.constant.BusKey
import com.wang.gvideo.migu.dao.CollectManager
import com.wang.gvideo.migu.dao.model.SeasonInfoDao
import com.wang.gvideo.migu.dao.model.ViewVideoDao
import com.wang.gvideo.migu.model.MovieInfoModel
import com.wang.gvideo.migu.setting.Prefences
import com.wang.gvideo.migu.ui.VideoPlayActivity
import com.wang.gvideo.migu.ui.VideoPlayHelper.VIDEO_CONT_ID
import com.wang.gvideo.migu.ui.VideoPlayHelper.VIDEO_CONT_ID_POS
import com.wang.gvideo.migu.ui.VideoPlayHelper.VIDEO_CONT_PLAY_POS
import com.wang.gvideo.migu.ui.VideoPlayHelper.VIDEO_CONT_SEASON_IDS
import com.wang.gvideo.migu.ui.VideoPlayHelper.VIDEO_WITH_SEASON_UPDATE
import com.wang.gvideo.migu.ui.dialog.SelectDownloadDialog
import com.wang.gvideo.migu.ui.dialog.SelectMovieDefiDialog
import com.wang.gvideo.migu.ui.dialog.SelectSeasonDialog
import org.apache.commons.lang3.StringEscapeUtils
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Date:2018/4/12
 * Description:
 *
 * @author wangguang.
 */
class MoviePlayPresenter @Inject constructor(activity: VideoPlayActivity) : BasePresenter<VideoPlayActivity>(activity), OnMoreInfoClickListener, StateChangeListener, View.OnClickListener {


    var infoModel: MovieInfoModel? = null
    var seasonList: List<Pair<String, String>>? = null

    /** 视频在剧集中的位置 */
    var position = 0

    /** 视频播放的时间位置 */
    var playPosition = 0

    /** 剧集更新时是否更新数据库 */
    var needUpdateSeason = false
    override fun onCreate() {
        super.onCreate()
        val contId = activity.intent.getStringExtra(VIDEO_CONT_ID)
        activity.intent.getSerializableExtra(VIDEO_CONT_SEASON_IDS)?.let {
            seasonList = (it as Array<Pair<String, String>>).toList()
        }
        playPosition = activity.intent.getIntExtra(VIDEO_CONT_PLAY_POS, 0)
        position = activity.intent.getIntExtra(VIDEO_CONT_ID_POS, 0)
        needUpdateSeason = activity.intent.getBooleanExtra(VIDEO_WITH_SEASON_UPDATE, false)
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
        SelectMovieDefiDialog(activity)
                .setSelectListener { rate ->
                    Log.d(TAG, "rate" + rate)
                    infoModel?.let {
                        getVideoInfo(it.contentId, rate, true)
                        Prefences.selectDefiniitionRate(rate)
                    }
                }
                .setModel(infoModel?.getSupportRate() ?: listOf())
                .selectRate(infoModel?.rate ?: 50)
                .show()
    }

    private fun showSelectSeasonDialog() {
        seasonList.notEmptyRun {list ->
            SelectSeasonDialog(activity, list as List, position) { pos, dataItem ->
                setOnBusy(true)
                position = pos
                saveHistory()
                getVideoInfo(dataItem.first)
            }.show()
        }.nil { Toast.makeText(activity, "就这一集~", Toast.LENGTH_SHORT).show() }
    }

    private fun showWirelessToTvDialog() {
        infoModel?.let { model ->
            activity.pauseVideo()
            HpplayLinkControl.getInstance().showHpplayWindow(activity, model.playUrl, activity.getNowPosition() / 1000, object : HpplayWindowPlayCallBack {
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

    private fun showDownloadDialog() {
        setOnBusy(true, true)
        seasonList?.let {
            CacheManager.intance().checkIsDownload(it)
                    .doOnTerminate { setOnBusy(false) }
                    .subscribe(object : OneSubScriber<List<Pair<String, Boolean>>>() {
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


    private fun getVideoInfo(contId: String, requestRate: Int = Prefences.getDefiniitionRate(), change: Boolean = false) {
        val s = doHttp(MiGuMovieInter::class.java)
                .getMovieData(contId, requestRate)
                .subscribeOn(Schedulers.io())
                .map {
                    val result = it.replace(Regex(",[\\s]*\\}|,[\\s]*\\]")) {
                        if (it.value.takeLast(1) == "}") {
                            "}"
                        } else {
                            "]"
                        }
                    }
                    ApiFactory.INSTANCE().getGson().fromJson(result, MovieInfoModel::class.java)
                }
                .doOnNext {
                    //去除&amp;
                    it.playUrl = StringEscapeUtils.unescapeHtml4(it.playUrl)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate { setOnBusy(false) }
                .subscribe(object : OneSubScriber<MovieInfoModel>() {
                    override fun onNext(t: MovieInfoModel) {
                        super.onNext(t)
                        infoModel = t
                        infoModel?.rate = infoModel?.selectSupportRate(requestRate) ?: 50
                        infoModel?.let {
                            updateSeason(it.contentId,needUpdateSeason)
                        }
                        val url = t.playUrl
                        Log.d(TAG, url)
                        if (change) {
                            activity.changeDefinition(MovieInfoModel.definitionName(t.rate), url)
                        } else {
                            activity.startPlay(t.titleName, url, MovieInfoModel.definitionName(t.rate), playPosition)
                        }
                        playPosition = 0
                    }
                })
        addSubscription(s)
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
                        Triple(pair.first,pair.second,position) } )
                }
                needUpdateSeason = false
                RxBus.instance().postSingleEvent(BusKey.UPDATE_COLLECT_LIST,contId)
            }
        }else{
            if(updateDao) {
                val newSeasonList = infoModel?.getSeasonPairs()
                newSeasonList.notEmptyRun {
                    if (it is List) {
                        if (seasonList!!.size < it.size) {
                            val tripleList = it.mapIndexed { index, pair ->
                                Triple(pair.first,pair.second,position) }
                            val subList = tripleList.subList(seasonList!!.size, tripleList.size - 1)
                            addSeasonList(contId, subList)
                        }
                    }
                }
                RxBus.instance().postSingleEvent(BusKey.UPDATE_COLLECT_LIST,contId)
                needUpdateSeason = false
            }
        }
    }

    private fun addSeasonList(nodeId: String, list: List<Triple<String, String,Int>>) {
        CollectManager.manager.collectSeasonList(list, object : IDaoAdapter<Triple<String, String,Int>, SeasonInfoDao> {
            override fun adapt(t: Triple<String, String,Int>): SeasonInfoDao {
                return SeasonInfoDao(t.first, t.second, nodeId,t.third)
            }

            override fun reAdapt(t: SeasonInfoDao?): Triple<String, String,Int>? {
                if (t != null) {
                    return Triple(t.contId, t.name,t.position)
                }
                return null
            }

        })

    }



override fun notifyPlayState(statue: Int) {
    if (statue == IjkVideoManager.STATE_PLAYBACK_COMPLETED) {
        seasonList.safeGetRun(position + 1) {
            saveHistory()
            Toast.makeText(activity, "马上下一集，稍后~", Toast.LENGTH_SHORT).show()
            setOnBusy(true)
            position += 1
            getVideoInfo(it.first)
            return
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
            val info = ViewVideoDao(it.contentId, it.titleName, it.srcH, pos, prec, System.currentTimeMillis())
            DataCenter.instance().insert(info)
            RxBus.instance().postEmptyEvent(BusKey.UPDATE_HISTORY_LIST)
        } else if (prec == 0 && pos > 0) {
            val info = ViewVideoDao(it.contentId, it.titleName, it.srcH, pos, prec, System.currentTimeMillis())
            DataCenter.instance().insert(info)
            RxBus.instance().postEmptyEvent(BusKey.UPDATE_HISTORY_LIST)
        }
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