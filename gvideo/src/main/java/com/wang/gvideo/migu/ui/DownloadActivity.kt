package com.wang.gvideo.migu.ui

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.Fade
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.code19.library.DensityUtil
import com.github.jdsjlzx.ItemDecoration.DividerDecoration
import com.wang.gvideo.R
import com.wang.gvideo.common.base.BaseActivity
import com.wang.gvideo.common.view.RecyclerViewDivider
import com.wang.gvideo.migu.cache.CacheManager
import com.wang.gvideo.migu.cache.CacheTask
import com.wang.gvideo.migu.ui.adapter.DownloadAdapter
import kotlinx.android.synthetic.main.activity_download.*

/**
 * Date:2018/5/3
 * Description:
 *
 * @author wangguang.
 */
class DownloadActivity : BaseActivity() {

    lateinit var adapter:DownloadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initHeadBar()
        setContentView(R.layout.activity_download)
        download_list.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        download_list.addItemDecoration(getDivider())
        adapter = DownloadAdapter(this)
//        addTask()
        adapter.taskList.addAll(CacheManager.intance().allTask())
        download_list.adapter = adapter
    }

    // TODO
    private fun addTask() {
        repeat(5){
            CacheManager.intance().addITask(CacheTask.Builder()
                    .contId("123$it")
                    .img("http://wapx.cmvideo.cn:8080/publish/poms/image/5500/093/488/800033_HSJ720H.jpg")
                    .name("小人人$it")
                    .url("123")
                    .build())
        }

    }

    private fun getDivider():RecyclerView.ItemDecoration{
        return RecyclerViewDivider(this,LinearLayoutManager.VERTICAL,DensityUtil.dip2px(this,0.5f),
                resources.getColor(R.color.divider))
    }
    private fun initHeadBar() {
        window.enterTransition = Fade()
        window.exitTransition = Fade()
        supportActionBar?.apply {
            val headerView = LayoutInflater.from(this@DownloadActivity).inflate(R.layout.header_download_layout, null)
            val firstView = headerView.findViewById<ImageView>(R.id.header_right_ic)
            firstView.setOnClickListener {
                showMsg("edit")
            }
            var parmas = ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER_VERTICAL or Gravity.RIGHT)
            displayOptions = displayOptions or ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(headerView, parmas)
            title = "下载中心"
        }
    }
}