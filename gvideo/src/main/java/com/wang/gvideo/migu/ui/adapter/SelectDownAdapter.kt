package com.wang.gvideo.migu.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wang.gvideo.R
import com.wang.gvideo.common.utils.safeGet
import com.wang.gvideo.common.utils.safeGetRun

/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
class SelectDownAdapter(val context: Context, val data: List<Pair<String, Boolean>>) : RecyclerView.Adapter<SelectDownAdapter.SelectDownHolder>() {

    val newSelectItem = HashSet<Pair<String, Boolean>>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SelectDownHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.video_download_season_text, parent, false)
        val holder = SelectDownHolder(view)
        holder.titleView = view.findViewById(R.id.video_search_detail_text)
        return holder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SelectDownHolder?, position: Int) {
        holder?.titleView?.text = "${position + 1}"
        holder?.titleView?.isEnabled =  data.safeGet(position) { Pair("", false) }.second
        holder?.titleView?.tag = position
        holder?.titleView?.setOnClickListener {
            val pos = it.tag
            if(pos is Int) {
                if (it.isEnabled) {
                    it.isSelected = !it.isSelected
                    if (it.isSelected) {
                        data.safeGetRun(pos) {
                            newSelectItem.add(it)
                        }
                    } else {
                        data.safeGetRun(pos) {
                            newSelectItem.remove(it)
                        }
                    }
                }
            }
        }
    }

    class SelectDownHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        var titleView: TextView? = null
    }
}

