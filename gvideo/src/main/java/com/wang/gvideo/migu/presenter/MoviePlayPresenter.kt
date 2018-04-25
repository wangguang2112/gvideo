package com.wang.gvideo.migu.presenter

import android.util.Log
import android.widget.Toast
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
import com.wang.gvideo.migu.api.MiGuMovieInter
import com.wang.gvideo.migu.cache.CacheManager
import com.wang.gvideo.migu.constant.BusKey
import com.wang.gvideo.migu.dao.model.ViewVideoDao
import com.wang.gvideo.migu.model.MovieInfoModel
import com.wang.gvideo.migu.setting.Prefences
import com.wang.gvideo.migu.ui.VideoPlayActivity
import com.wang.gvideo.migu.ui.VideoPlayHelper.VIDEO_CONT_ID
import com.wang.gvideo.migu.ui.VideoPlayHelper.VIDEO_CONT_ID_POS
import com.wang.gvideo.migu.ui.VideoPlayHelper.VIDEO_CONT_PLAY_POS
import com.wang.gvideo.migu.ui.VideoPlayHelper.VIDEO_CONT_SEASON_IDS
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
class MoviePlayPresenter @Inject constructor(activity: VideoPlayActivity) : BasePresenter<VideoPlayActivity>(activity), OnMoreInfoClickListener, StateChangeListener {

    var infoModel: MovieInfoModel? = null
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
        SelectMovieDefiDialog(activity)
                .setSelectListener { rate ->
                    Log.d(TAG, "rate" + rate)
                    infoModel?.let {
                        getVideoInfo(it.contentId,rate)
                        Prefences.selectDefiniitionRate(rate)
                    }
                }
                .setModel(infoModel?.getSupportRate()?: listOf())
                .selectRate(infoModel?.rate?:50)
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



    private fun getVideoInfo(contId: String,requestRate:Int = Prefences.getDefiniitionRate()) {
        val s = doHttp(MiGuMovieInter::class.java)
                .getMovieData(contId,requestRate)
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
                        infoModel?.rate = infoModel?.selectSupportRate(requestRate)?:50
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
                        val url = t.playUrl
                        Log.d(TAG, url)
                        activity.startPlay(t.titleName, url, MovieInfoModel.definitionName(t.rate),playPosition)
                        playPosition = 0
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

    private fun saveHistory(){
        //存储播放位置
        infoModel?.let {
            val pos = IjkVideoManager.getInstance().currentPosition
            val all = IjkVideoManager.getInstance().duration
            val prec = if(all == 0){0}else{ pos * 100 /all}
            //大于95的 统统去掉
            if(prec in 1..100) {
                val info = ViewVideoDao(it.contentId, it.titleName, it.srcH, pos, prec,System.currentTimeMillis())
                DataCenter.instance().insert(info)
                RxBus.instance().postEmptyEvent(BusKey.UPDATE_HISTORY_LIST)
            }else if(prec == 0 && pos > 0){
                val info = ViewVideoDao(it.contentId, it.titleName, it.srcH, pos, prec,System.currentTimeMillis())
                DataCenter.instance().insert(info)
                RxBus.instance().postEmptyEvent(BusKey.UPDATE_HISTORY_LIST)
            }
        }
    }

}