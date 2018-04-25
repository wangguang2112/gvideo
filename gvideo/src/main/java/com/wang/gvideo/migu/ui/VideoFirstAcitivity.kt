package com.wang.gvideo.migu.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.widget.LinearLayoutManager
import android.transition.Fade
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import com.tapadoo.alerter.Alerter
import com.wang.gvideo.R
import com.wang.gvideo.common.base.BaseActivity
import com.wang.gvideo.common.bus.RxBus
import com.wang.gvideo.common.dao.DataCenter
import com.wang.gvideo.common.utils.nil
import com.wang.gvideo.common.utils.string
import com.wang.gvideo.common.view.alert.AlertView
import com.wang.gvideo.migu.constant.BusKey
import com.wang.gvideo.migu.dao.CollectManager
import com.wang.gvideo.migu.dao.model.ViewVideoDao
import com.wang.gvideo.migu.ui.adapter.CollectAdapter
import com.wang.gvideo.migu.ui.adapter.HistoryAdapter
import kotlinx.android.synthetic.main.activity_video_first.*
import rx.android.schedulers.AndroidSchedulers


/**
 * Date:2018/4/4
 * Description:
 *
 * @author wangguang.
 */

class VideoFirstAcitivity : BaseActivity() {

    var historyAdapter: HistoryAdapter? = null
    var collectAdapter: CollectAdapter? = null

    private var isNeedRefreshHistory = false
    private var isNeedRefreshCollect = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDoubleReturn = true
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        setContentView(R.layout.activity_video_first)
        toast = Toast.makeText(this, "再点击一次退出", Toast.LENGTH_SHORT)
        video_first_history_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        video_first_history_list.overScrollMode = View.OVER_SCROLL_NEVER
        video_first_collect_list.overScrollMode = View.OVER_SCROLL_NEVER
        video_first_collect_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        initHeadBar()
        getHistoryData()
        getCollectData()
        initEvent()
    }

    private fun initEvent() {
        autoUnSubscribe {
            RxBus.instance().toObservableOnMain(BusKey.UPDATE_HISTORY_LIST)
                    .subscribe {
                        isNeedRefreshHistory = true
                    }
        }
        autoUnSubscribe {
            RxBus.instance().toObservableOnMain(BusKey.UPDATE_COLLECT_LIST)
                    .subscribe {
                        isNeedRefreshCollect = true
                    }
        }
    }

    private fun getHistoryData() {
        setOnBusy(true)
        autoUnSubscribe {
            DataCenter.instance().queryListWithSort(ViewVideoDao::class, "time")
                    .doOnTerminate {
                        setOnBusy(false)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (it.isNotEmpty()) {
                            if (historyAdapter == null) {
                                historyAdapter = HistoryAdapter(this, it.toMutableList()) { _, data, childPos ->
                                    VideoPlayHelper.startSingleVideoWithPos(this@VideoFirstAcitivity, data?.contId, data.postion)
                                }
                                historyAdapter?.deleteListner = { _, data, childPos ->
                                    deleteHistory(data.contId)
                                    historyAdapter?.historyList?.removeAt(childPos)
                                    historyAdapter?.notifyItemRemoved(childPos)
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

    private fun getCollectData() {
        setOnBusy(true)
        autoUnSubscribe {
            CollectManager.manager.getCollectData()
                    .doOnTerminate {
                        setOnBusy(false)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        Log.d(TAG, it.string())
                        if (it.isNotEmpty()) {
                            if (collectAdapter == null) {
                                collectAdapter = CollectAdapter(this, it.toMutableList()) { _, parent, data, _, childPos ->
                                    VideoPlayHelper.startListVideoPlay(this@VideoFirstAcitivity, data?.contId ?: parent.contId,
                                            parent.subList.map { it.changePair() }, childPos)
                                }
                                collectAdapter?.deleteListner = { _, data, childPos ->
                                    deleteCollect(data.contId)
                                    collectAdapter?.collectList?.removeAt(childPos)
                                    collectAdapter?.notifyItemRemoved(childPos)
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
                                video_first_history_list.visibility = View.GONE
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
            val firstView = headerView.findViewById<ImageView>(R.id.header_right_ic)
            firstView.setOnClickListener {
                AlertView.Builder()
                        .setContext(this@VideoFirstAcitivity)
                        .setStyle(AlertView.Style.Alert)
                        .setTitle("添加视频cid")
                        .setMessage(null)
                        .setCancelText("取消")
                        .setDestructive("播放")
                        .setWithEditor("请输入cid") { text ->
                            text.toIntOrNull()?.let { VideoPlayHelper.startSingleVideoPlay(this@VideoFirstAcitivity, it.toString()) }.nil {
                                Alerter.create(this@VideoFirstAcitivity)
                                        .setTitle("提示")
                                        .setText("请输入正确的cid")
                                        .setBackgroundColorRes(R.color.toast_background)
                                        .setDuration(1000)
                                        .show()
                            }
                        }
                        .build().show()
            }
            val secondView = headerView.findViewById<ImageView>(R.id.header_right_ic2)
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

    private fun deleteHistory(conId: String) {
        DataCenter.instance().delete(ViewVideoDao::class, conId)
    }

    private fun deleteCollect(conId: String) {
        CollectManager.manager.unCollectSeasonVideo(conId)
    }

    override fun onResume() {
        super.onResume()
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
            if (result) {
                return result
            }
        }
        return super.onKeyDown(keyCode, event)
    }


}
