package com.wang.gvideo.migu.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wang.gvideo.R
import com.wang.gvideo.migu.model.SeasonItem

/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
class CollectSeasonAdapter(val context: Context, var data: MutableList<SeasonItem>) : RecyclerView.Adapter<CollectSeasonAdapter.SelectDownHolder>() {

    var onItemClick: ((View, SeasonItem, Int) -> Unit)? = null

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
        holder?.titleView?.tag = position
        holder?.titleView?.setOnClickListener {
            val pos = it.tag
            if (pos is Int) {
                onItemClick?.invoke(it, data[pos], pos)
            }
        }
    }

    class SelectDownHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        var titleView: TextView? = null
    }
}

