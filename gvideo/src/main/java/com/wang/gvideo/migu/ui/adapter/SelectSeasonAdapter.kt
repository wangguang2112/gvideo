package com.wang.gvideo.migu.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wang.gvideo.R
import com.wang.gvideo.common.utils.safeGet

/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
class SelectSeasonAdapter(val context: Context, val data: List<Pair<String, String>>, val pos: Int,
                          val listener: ((Int, Int, Pair<String, String>) -> Unit) = { _, _, _ -> }) : RecyclerView.Adapter<SelectSeasonAdapter.SelectSeasonHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SelectSeasonHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.video_season_select_item, parent, false)
        val holder = SelectSeasonHolder(view)
        holder.titleView = view.findViewById(R.id.season_select_text)
        return holder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SelectSeasonHolder?, position: Int) {
        holder?.titleView?.text = "${position + 1} ${data.safeGet(position) { Pair("", "undefine") }.second}"
        holder?.titleView?.isSelected = position == pos
        holder?.titleView?.setOnClickListener {
            listener?.invoke(pos, position, data[position])
        }
    }


    class SelectSeasonHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        var titleView: TextView? = null
    }
}

