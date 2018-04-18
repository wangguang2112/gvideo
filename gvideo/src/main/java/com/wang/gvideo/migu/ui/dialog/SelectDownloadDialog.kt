package com.wang.gvideo.migu.ui.dialog

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import com.wang.gvideo.R
import com.wang.gvideo.common.base.BaseDialog
import com.wang.gvideo.common.utils.empty
import com.wang.gvideo.migu.ui.adapter.SelectDownAdapter
import kotlinx.android.synthetic.main.dialog_select_down_layout.*

/**
 * Date:2018/4/13
 * Description:
 *
 * @author wangguang.
 */
class SelectDownloadDialog(ctx: Context, private val dataList: List<Pair<String, Boolean>>, private val listener:(Set<Pair<String, Boolean>>)->Unit) : BaseDialog(ctx,R.style.TranslucentDialog) {

    lateinit var adapter: SelectDownAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_select_down_layout)
        select_item_list.layoutManager = GridLayoutManager(context, 8,LinearLayoutManager.VERTICAL, false)
        adapter = SelectDownAdapter(context, dataList)
        select_item_list.adapter = adapter
        select_download.setOnClickListener {
            if(!adapter.newSelectItem.empty()){
                listener(adapter.newSelectItem)
            }
            this.dismiss()
        }
        val parent =  container_layout.parent
        if( parent is View){
            parent.setOnClickListener { dismiss() }
        }
        container_layout.setOnClickListener{
            dismiss()
        }
    }

}