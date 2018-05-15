package com.wang.gvideo.migu.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.transition.Fade
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import com.code19.library.SPUtils
import com.github.jdsjlzx.interfaces.OnLoadMoreListener
import com.github.jdsjlzx.interfaces.OnRefreshListener
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.tapadoo.alerter.Alerter
import com.wang.gvideo.R
import com.wang.gvideo.common.base.BaseActivity
import com.wang.gvideo.common.bus.RxBus
import com.wang.gvideo.common.bus.event.SimpleEvent
import com.wang.gvideo.common.dao.DataCenter
import com.wang.gvideo.common.net.ApiFactory
import com.wang.gvideo.common.net.OneSubScriber
import com.wang.gvideo.common.utils.DensityUtil
import com.wang.gvideo.common.utils.SharedPreferencesUtil
import com.wang.gvideo.common.utils.empty
import com.wang.gvideo.common.utils.nil
import com.wang.gvideo.common.view.alert.AlertView
import com.wang.gvideo.migu.api.MiGuMovieInter
import com.wang.gvideo.migu.cache.CacheManager
import com.wang.gvideo.migu.constant.BusKey
import com.wang.gvideo.migu.constant.SpKey
import com.wang.gvideo.migu.dao.CollectManager
import com.wang.gvideo.migu.dao.model.CacheTaskDao
import com.wang.gvideo.migu.dao.model.RecommondDao
import com.wang.gvideo.migu.dao.model.ViewVideoDao
import com.wang.gvideo.migu.model.MovieItem
import com.wang.gvideo.migu.model.MovieItemList
import com.wang.gvideo.migu.ui.adapter.CollectAdapter
import com.wang.gvideo.migu.ui.adapter.HistoryAdapter
import com.wang.gvideo.migu.ui.adapter.RecommondAdapter
import com.wang.gvideo.migu.ui.view.GRefreshHeader
import kotlinx.android.synthetic.main.activity_video_first.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.util.*


/**
 * Date:2018/4/4
 * Description:
 *
 * @author wangguang.
 */

class VideoFirstAcitivity : BaseActivity() {

    var historyAdapter: HistoryAdapter? = null
    var collectAdapter: CollectAdapter? = null
    var recommondAdapter: RecommondAdapter? = null

    private var isNeedRefreshHistory = false
    private var isNeedRefreshCollect = false

    lateinit var firstView: View
    lateinit var secondView: View
    var optionWindow :PopupWindow? =null

    private var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDoubleReturn = true
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        setContentView(R.layout.activity_video_first)
        toast = Toast.makeText(this, "再点击一次退出", Toast.LENGTH_SHORT)
        video_first_history_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        video_first_collect_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        video_first_history_list.overScrollMode = View.OVER_SCROLL_NEVER
        video_first_collect_list.overScrollMode = View.OVER_SCROLL_NEVER
        initHeadBar()
        getHistoryData()
        getCollectData()
        initEvent()
        initRecommond()
    }

    private fun initRecommond() {
        video_first_recommond_list.layoutManager = GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false)
        video_first_recommond_list.setRefreshHeader(GRefreshHeader(this))
        video_first_recommond_list.setOnRefreshListener(OnRefreshListener {
            Log.d(TAG, "OnRefreshListeneer")
            currentPage = 1
            getRecommondList(currentPage, true)
        })
        video_first_recommond_list.setOnLoadMoreListener(OnLoadMoreListener {
            Log.d(TAG, "OnRefreshListener")
            getRecommondList(++currentPage)
        })
        setOnBusy(true)
        getRecommondCache()
    }

    private fun initEvent() {
        autoUnSubscribe {
            RxBus.instance().toObservableOnMain(BusKey.UPDATE_HISTORY_LIST)
                    .subscribe {
                        if (isResume) {
                            getHistoryData()
                        } else {
                            isNeedRefreshHistory = true
                        }
                    }
        }
        autoUnSubscribe {
            RxBus.instance().toObservableOnMain(BusKey.UPDATE_COLLECT_LIST)
                    .subscribe { event ->
                        if (event is SimpleEvent<*>) {
                            val contId = event.attachObj as String
                            updateSingleCollectData(contId)
                        } else {
                            if (isResume) {
                                getCollectData()
                            } else {
                                isNeedRefreshCollect = true
                            }
                        }

                    }
        }
    }

    private fun getHistoryData() {
//        setOnBusy(true)
        autoUnSubscribe {
            DataCenter.instance().queryListWithSort(ViewVideoDao::class, "time")
                    .doOnTerminate {
                        //                        setOnBusy(false)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (it.isNotEmpty()) {
                            if (historyAdapter == null) {
                                historyAdapter = HistoryAdapter(this, it.toMutableList()) { _, data, childPos ->
                                    VideoPlayHelper.startSingleVideoWithPos(this@VideoFirstAcitivity, data?.contId, data.postion)
                                }
                                historyAdapter?.deleteListner = { _, data, _ ->
                                    val newIndex = historyAdapter?.historyList?.indexOf(data)!!
                                    historyAdapter?.historyList?.remove(data)
                                    deleteHistory(data.contId)
                                    if(newIndex >= 0) {
                                        historyAdapter?.notifyItemRemoved(newIndex)
                                    }
                                    if(historyAdapter?.historyList?.size == 0){
                                        video_first_history_list.visibility = View.GONE
                                        history_error.visibility = View.VISIBLE
                                    }
                                }
                                video_first_history_list.adapter = historyAdapter
                            } else {
                                historyAdapter?.historyList?.clear()
                                historyAdapter?.historyList?.addAll(it)
                            }
                            if (it.isNotEmpty()) {
                                video_first_history_list.visibility = View.VISIBLE
                                history_error.visibility = View.GONE
                            } else {
                                video_first_history_list.visibility = View.GONE
                                history_error.visibility = View.VISIBLE
                            }
                            historyAdapter?.notifyDataSetChanged()
                            return@subscribe
                        } else {
                            video_first_history_list.visibility = View.GONE
                            history_error.visibility = View.VISIBLE
                        }
                    }
        }
    }

    private fun updateSingleCollectData(contId: String) {
        collectAdapter?.let { adaper ->
            val pos = adaper.collectList.indexOfFirst {
                it.contId == contId
            }
            if (pos != -1) {
                adaper.notifyItemChanged(pos)
            }
        }
    }

    private fun getCollectData() {
//        setOnBusy(true)
        autoUnSubscribe {
            CollectManager.manager.getCollectData()
                    .doOnTerminate {
                        //                        setOnBusy(false)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        //                        Log.d(TAG, it.string())
                        if (it.isNotEmpty()) {
                            if (collectAdapter == null) {
                                collectAdapter = CollectAdapter(this, it.toMutableList()) { _, parent, data, _, childPos ->
                                    VideoPlayHelper.startListVideoPlay(this@VideoFirstAcitivity, data?.contId ?: parent.contId,
                                            parent.subList.map { it.changePair() }, childPos)
                                }
                                collectAdapter?.deleteListner = { _, data, _ ->
                                    val newIndex = collectAdapter?.collectList?.indexOf(data)!!
                                    collectAdapter?.collectList?.remove(data)
                                    deleteCollect(data.contId)
                                    if(newIndex >= 0) {
                                        collectAdapter?.notifyItemRemoved(newIndex)
                                    }
                                    if(collectAdapter?.collectList?.size == 0){
                                        video_first_collect_list.visibility = View.GONE
                                        collect_error.visibility = View.VISIBLE
                                    }
                                }
                                video_first_collect_list.adapter = collectAdapter
                            } else {
                                collectAdapter?.collectList?.clear()
                                collectAdapter?.collectList?.addAll(it)
                            }
                            if (it.isNotEmpty()) {
                                video_first_collect_list.visibility = View.VISIBLE
                                collect_error.visibility = View.GONE
                            } else {
                                video_first_collect_list.visibility = View.GONE
                                collect_error.visibility = View.VISIBLE
                            }
                            collectAdapter?.notifyDataSetChanged()
                            return@subscribe
                        } else {
                            video_first_collect_list.visibility = View.GONE
                            collect_error.visibility = View.VISIBLE
                        }
                    }
        }

    }

    private fun initHeadBar() {
        window.enterTransition = Fade()
        window.exitTransition = Fade()
        supportActionBar?.apply {
            val headerView = LayoutInflater.from(this@VideoFirstAcitivity).inflate(R.layout.header_first_layout, null)
            firstView = headerView.findViewById<ImageView>(R.id.header_right_ic)
            secondView = headerView.findViewById<ImageView>(R.id.header_right_ic2)
            firstView.setOnClickListener {
                showMoreOptions(it)
            }
            secondView.setOnClickListener {
                startActivity(Intent(this@VideoFirstAcitivity, VideoSearchActivity::class.java)
                        , ActivityOptions.makeSceneTransitionAnimation(this@VideoFirstAcitivity, it, "transition")
                        .toBundle())
            }
            var parmas = ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER_VERTICAL or Gravity.RIGHT)
            displayOptions = displayOptions or ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(headerView, parmas)
            title = "主页"
        }
    }

    private fun showMoreOptions(v: View) {
        if(optionWindow == null){
            optionWindow = PopupWindow(LayoutInflater.from(this@VideoFirstAcitivity).inflate(R.layout.dialog_more_pop, null))
            optionWindow!!.width = ViewGroup.LayoutParams.WRAP_CONTENT
            optionWindow!!.height = ViewGroup.LayoutParams.WRAP_CONTENT
            optionWindow!!.isFocusable = true
            optionWindow!!.isTouchable= true
            optionWindow!!.setBackgroundDrawable(getDrawable(R.color.dialog_background))
            optionWindow!!.animationStyle = R.style.PopowAnim
            val ic1 = optionWindow!!.contentView.findViewById<LinearLayout>(R.id.options_list_1)
            ic1.setOnClickListener {
                showOpenCidDialog()
                optionWindow?.dismiss()
            }
            val ic2 = optionWindow!!.contentView.findViewById<LinearLayout>(R.id.options_list_2)
            ic2.setOnClickListener {
                showOpenUrlDialog()
                optionWindow?.dismiss()
            }
            val ic3 = optionWindow!!.contentView.findViewById<LinearLayout>(R.id.options_list_3)
            ic3.setOnClickListener {
                optionWindow?.dismiss()
                startActivity(Intent(this@VideoFirstAcitivity, DownloadActivity::class.java))
            }
            optionWindow!!.isOutsideTouchable = true
        }
        optionWindow?.let {
            if(it.isShowing){
                it.dismiss()
            }else{
                it.showAsDropDown(v,0,DensityUtil.dip2px(this,16f))
            }
        }
    }


    private fun showOpenCidDialog() {
     /*   val file = this.getExternalFilesDir("videoCache").absolutePath + "/637595622/637595622.mp4"
        val input = File(file)
        if(input.exists()) {
            val buffer = ByteArray(1024)
            input.inputStream().read(buffer)
            val size = buffer[3]
            Log.d("wanggaung", "size $size")
            val scaleStart = size.toInt() + 3 + 4 + 1 + 3 + 4 + 4
            val scale = Arrays.copyOfRange(buffer, scaleStart, scaleStart + 4)
            val duration = Arrays.copyOfRange(buffer, scaleStart + 4, scaleStart + 8)
            Log.d("wanggaung", "scale:$scale,duration$duration")
        }else{
            Log.d("wanggaung", "exists")
        }*/
        val alertView = AlertView.Builder()
                .setContext(this@VideoFirstAcitivity)
                .setStyle(AlertView.Style.Alert)
                .setTitle("添加视频cid")


                .setMessage(null)
                .setCancelText("取消")
                .setDestructive("播放")
                .setWithEditor("","请输入cid") { text ->
                    text.toIntOrNull()?.let { VideoPlayHelper.startSingleVideoPlay(this@VideoFirstAcitivity, it.toString()) }.nil {
                        Alerter.create(this@VideoFirstAcitivity)
                                .setTitle("提示")
                                .setText("请输入正确的cid")
                                .setBackgroundColorRes(R.color.toast_background)
                                .setDuration(1000)
                                .show()
                    }
                }
                .build()
        alertView.show()
        firstView.isClickable = false
        secondView.isClickable = false
        alertView.setOnDismissListener {
            firstView.isClickable = true
            secondView.isClickable = true
        }
    }

    private fun showOpenUrlDialog() {
        var default = SharedPreferencesUtil.instance.getString(SpKey.MIGU_VIDEO_URL_PLAY_HISTORY)
        val alertView = AlertView.Builder()
                .setContext(this@VideoFirstAcitivity)
                .setStyle(AlertView.Style.Alert)
                .setTitle("添加视频地址")
                .setMessage(null)
                .setCancelText("取消")
                .setDestructive("播放")
                .setWithEditor(default,"请输入url") { text ->
                    if (text.toString().isNotEmpty()) {
                        SharedPreferencesUtil.instance.setString(SpKey.MIGU_VIDEO_URL_PLAY_HISTORY,text)
                        VideoPlayHelper.startNativeVideo(this@VideoFirstAcitivity, text.toString())
                    } else {
                        Alerter.create(this@VideoFirstAcitivity)
                                .setTitle("提示")
                                .setText("请输入正确的url")
                                .setBackgroundColorRes(R.color.toast_background)
                                .setDuration(1000)
                                .show()
                    }
                }
                .build()
        alertView.show()
        firstView.isClickable = false
        secondView.isClickable = false
        alertView.setOnDismissListener {
            firstView.isClickable = true
            secondView.isClickable = true
        }
    }

    private fun deleteHistory(conId: String) {
        DataCenter.instance().delete(ViewVideoDao::class, conId)
    }

    private fun deleteCollect(conId: String) {
        CollectManager.manager.unCollectSeasonVideo(conId)
    }

    override fun onPause() {
        super.onPause()
        isResume = false
    }

    var isResume = false
    override fun onResume() {
        super.onResume()
        isResume = true
        if (isNeedRefreshHistory) {
            getHistoryData()
            isNeedRefreshHistory = false
        }
        if (isNeedRefreshCollect) {
            getCollectData()
            isNeedRefreshCollect = false
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            var result = false
            if (historyAdapter?.showDelete == true) {
                historyAdapter?.showDelete = false
                historyAdapter?.notifyDataSetChanged()
                result = true
            }
            if (collectAdapter?.showDelete == true) {
                collectAdapter?.showDelete = false
                collectAdapter?.notifyDataSetChanged()
                result = true
            }
            if(optionWindow?.isShowing == true){
                optionWindow?.dismiss()
                result = true
            }
            if (result) {
                return result
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun getRecommondCache() {
        DataCenter.instance().queryWithConditionSort(RecommondDao::class, MovieItem::class,"time","", MovieItem.adapter)
                .subscribe(object : OneSubScriber<List<MovieItem>>() {
                    override fun onNext(t: List<MovieItem>) {
                        super.onNext(t)
                        if (t.empty()) {
                            currentPage = 1
                            getRecommondList(currentPage, true)
                        } else {
                            setOnBusy(false)
                            updateRecommondList(currentPage, t)
                        }
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        currentPage = 1
                        getRecommondList(currentPage, true)
                    }
                })

    }

    private fun getRecommondList(page: Int, updateDao: Boolean = false) {
        doHttp(MiGuMovieInter::class.java)
                .getMovieFilteData(page)
                .subscribeOn(Schedulers.io())
                .doOnTerminate { setOnBusy(false) }
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    val result = it.replace(Regex(",[\\s]*\\}|,[\\s]*\\]")) {
                        if (it.value.takeLast(1) == "}") {
                            "}"
                        } else {
                            "]"
                        }
                    }
                    ApiFactory.INSTANCE().getGson().fromJson(result, MovieItemList::class.java)
                }.subscribe(object : OneSubScriber<MovieItemList>() {
                    override fun onNext(t: MovieItemList) {
                        super.onNext(t)
                        updateRecommondList(page, t.searchResult, updateDao)
                        video_first_recommond_list.refreshComplete(t.searchResult.size)
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        video_first_recommond_list.refreshComplete(0)
                    }
                })
    }

    private fun updateRecommondList(page: Int, data: List<MovieItem>, updateDao: Boolean = false) {
        if (recommondAdapter == null) {
            recommondAdapter = RecommondAdapter(this@VideoFirstAcitivity, data.toMutableList())
            { _: View, movieItem: MovieItem, _: Int ->
                VideoPlayHelper.startSingleVideoPlay(this@VideoFirstAcitivity, movieItem.contentId)
            }
            video_first_recommond_list.adapter = LRecyclerViewAdapter(recommondAdapter)
            video_first_recommond_list.setLoadMoreEnabled(true)
            if (updateDao) {
                DataCenter.instance().insertList(RecommondDao::class, data, MovieItem.adapter)
            }
        } else {
            if (page == 1) {
                recommondAdapter?.recommondList?.clear()
                recommondAdapter?.recommondList?.addAll(data)
                recommondAdapter?.notifyDataSetChanged()
                if (updateDao) {
                    DataCenter.instance().insertList(RecommondDao::class, data, MovieItem.adapter)
                }
            } else {
                val start = recommondAdapter?.recommondList?.size ?: 0
                recommondAdapter?.recommondList?.addAll(data)
                recommondAdapter?.notifyItemRangeInserted(start, data.size)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CacheManager.intance().desotry()
    }
}
