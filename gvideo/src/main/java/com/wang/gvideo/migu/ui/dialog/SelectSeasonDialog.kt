package com.wang.gvideo.migu.ui.dialog

import android.content.Context
import android.view.View
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.WindowManager
import com.wang.gvideo.R
import com.wang.gvideo.common.base.BaseDialog
import com.wang.gvideo.migu.ui.adapter.SelectSeasonAdapter
import kotlinx.android.synthetic.main.dialog_select_season_layout.*

/**
 * Date:2018/4/13
 * Description:
 *
 * @author wangguang.
 */
class SelectSeasonDialog(ctx: Context, private val dataList: List<Pair<String, String>>, val pos: Int,
                         private val listener: ((Int, Pair<String, String>) -> Unit) = { _, _ -> }) : BaseDialog(ctx, R.style.TranslucentDialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        window.setWindowAnimations(R.style.DialogRightAnim)
        setContentView(R.layout.dialog_select_season_layout)
        select_item_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        select_item_list.adapter = SelectSeasonAdapter(context, dataList, pos) { _, pos, itemData ->
            dismiss()
            listener(pos, itemData)
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