package com.wang.gvideo.migu.ui

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.tapadoo.alerter.Alerter
import com.wang.gvideo.R
import com.wang.gvideo.common.base.BaseActivity
import com.wang.gvideo.common.net.ApiFactory
import com.wang.gvideo.common.net.OneSubScriber
import com.wang.gvideo.common.utils.*
import com.wang.gvideo.common.view.RecyclerViewDivider
import com.wang.gvideo.common.view.alert.AlertView
import com.wang.gvideo.migu.api.MiGuCmInter
import com.wang.gvideo.migu.api.MiGuMovieInter
import com.wang.gvideo.migu.component.DaggerVideoSearchComponent
import com.wang.gvideo.migu.constant.Config
import com.wang.gvideo.migu.dao.CollectManager
import com.wang.gvideo.migu.model.*
import com.wang.gvideo.migu.ui.adapter.RecommondAdapter
import com.wang.gvideo.migu.ui.adapter.SingleAdapter
import com.wang.gvideo.migu.ui.adapter.VideoListAdapter
import kotlinx.android.synthetic.main.activity_video_search.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject


class VideoSearchActivity : BaseActivity() {

    private val VIDEO_SEARCH_KEY = "last_video_search"

    @BindView(R.id.video_search_text)
    lateinit var searchET: EditText

    @BindView(R.id.video_search_result_list)
    lateinit var resultList: RecyclerView

    @Inject
    lateinit var mResultListAdatper: VideoListAdapter

    @BindView(R.id.video_search_recom_list)
    lateinit var recommondList: ListView

    var movieList  = mutableListOf<MovieItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        setContentView(R.layout.activity_video_search)
        title = "搜索"
        ButterKnife.bind(this)
        DaggerVideoSearchComponent.builder().setContext(this).build().inject(this)
        resultList.adapter = mResultListAdatper
        resultList.layoutManager = LinearLayoutManager(this)
        resultList.addItemDecoration(RecyclerViewDivider(this, LinearLayoutManager.VERTICAL, 30, R.color.transparent))
        mResultListAdatper.mListener = object : OnVideoItemClickListener {
            override fun onItemClick(view: View, position: Int, seasonPosition: Int, parent: AppSearchListItem, season: AppSeasonItem?) {
                season?.let {
                    Log.d(TAG, "body : position = $position,seasonPosition = $seasonPosition,season cid = ${season.param}")
                } ?: kotlin.run {
                    Log.d(TAG, "body : position = $position,seasonPosition = $seasonPosition,season cid = ${parent.contParam}")
                }
            }

        }

        mResultListAdatper.itemListener = { _, _, pos, parent, season ->
            if (parent.subList.empty()) {
                VideoPlayHelper.startSingleVideoPlay(this@VideoSearchActivity,
                        AppSearchListItem.getContId(season?.param ?: parent.contParam))
            } else {
                VideoPlayHelper.startListVideoPlay(this@VideoSearchActivity,
                        AppSearchListItem.getContId(season?.param ?: parent.contParam), parent.getSeasonPairs(), pos)
            }
        }

        showRecomond()
        searchET.setOnEditorActionListener(TextView.OnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                getData(textView.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
        resultList.visibility = View.GONE
        video_search_movie_list.visibility = View.GONE
        video_search_movie_list.adapter = RecommondAdapter(this,movieList){ _,item,_ ->
            VideoPlayHelper.startSingleVideoPlay(this,item.contentId)
        }
        video_search_movie_list.layoutManager = GridLayoutManager(this,3,LinearLayoutManager.VERTICAL,false)
        movie_search_list_bt.setOnClickListener {
            if(video_search_movie_list.visibility == View.VISIBLE) {
                video_search_movie_list.visibility = View.GONE
                resultList.visibility = View.VISIBLE
                this.title = "视频列表"
            }else{
                video_search_movie_list.visibility = View.VISIBLE
                this.title = "电影列表"
                resultList.visibility = View.GONE
                if(needRefresh){
                   val adapter = video_search_movie_list.adapter as RecommondAdapter
//                    adapter.recommondList.clear()
//                    adapter.recommondList.addAll(movieList)
                    adapter.notifyDataSetChanged()
                    needRefresh  = false
                }
            }
        }
    }


    private fun showRecomond() {
        var str = SharedPreferencesUtil.getInstance(applicationContext).getString(VIDEO_SEARCH_KEY).split(",")
        if (str.isNotEmpty()) {
            recommondList.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, str)
            recommondList.visibility = View.VISIBLE
            recommondList.setOnItemClickListener { _, view, _, _ ->
                if (view is TextView) {
                    searchET.setText(view.text)
                    getData(view.text.toString())
                }
            }
        } else {
            recommondList.visibility = View.GONE
        }
    }

    private fun getData(value: String) {
        if (value.isEmpty()) {
            return
        }
        hideIMSoftKeyboard()
        setOnBusy(true)
        setSharePerfence(value)
        doHttp(MiGuCmInter::class.java)
                .getSearchList(4, DensityUtil.getDensityStr(this), Config.CLIENT_ID, 0, value, 1)
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
                    ApiFactory.INSTANCE().getGson().fromJson(result, AppVideoListInfo::class.java)
                }
                .map {
                    it.apply {
                        it.searchresult2.forEach {
                            it.isCollect = CollectManager.manager.checkExit(AppSearchListItem.getContId(it.contParam))
                            it.subList.notEmptyRun { it.forEachIndexed { index, appSeasonItem -> appSeasonItem.position = index } }
                        }
                    }
                }
                .subscribe(object : OneSubScriber<AppVideoListInfo>() {
                    override fun onNext(t: AppVideoListInfo) {
                        super.onNext(t)
                        mResultListAdatper.data = t.searchresult2
                        mResultListAdatper.notifyDataSetChanged()
                        resultList.scrollToPosition(0)
                        resultList.visibility = View.VISIBLE
                        video_search_movie_list.visibility = View.GONE
                        recommondList.visibility = View.GONE
                        resultList.requestFocusFromTouch()
                    }

                    override fun onError(e: Throwable) {
                        super.onError(e)
                        showMsg("网络异常，请重试")
                    }
                })
        getMovieData(value)
    }

    var needRefresh = false

    private fun getMovieData(value: String){
        doHttp(MiGuMovieInter::class.java)
                .getMovieSearchData(value)
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
                }.subscribe(object :OneSubScriber<MovieItemList>(){
                    override fun onNext(t: MovieItemList) {
                        super.onNext(t)
                        movieList.clear()
                        t.searchResult.notEmptyRun {
                            if( it is List){
                                movieList.addAll(it)
                            }
                        }
                        if(movieList.isNotEmpty()){
                            movie_search_list_bt.visibility = View.VISIBLE
                            needRefresh = true
                        }else{
                            movie_search_list_bt.visibility = View.GONE
                        }
                    }
                })
    }

    @OnClick(R.id.video_search_bt)
    fun onSearchClick(v: View) {
        var text = searchET.text.toString()
        getData(text)
    }

    private fun setSharePerfence(key: String) {
        val keys = SharedPreferencesUtil.getInstance(applicationContext).getString(VIDEO_SEARCH_KEY).split(",")
        if (keys.isNotEmpty() && !keys.contains(key)) {
            val result = keys.toMutableList()
            if (result.size >= 7) {
                result.removeAt(result.size - 1)
            }
            result.add(0, key)
            SharedPreferencesUtil.getInstance(applicationContext).setString(VIDEO_SEARCH_KEY, result.joinToString(separator = ","))

        }

    }
}
