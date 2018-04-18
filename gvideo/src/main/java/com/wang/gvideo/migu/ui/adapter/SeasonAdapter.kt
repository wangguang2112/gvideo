package com.wang.gvideo.migu.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.wang.gvideo.R
import com.wang.gvideo.migu.model.AppSeasonItem

/**
 * Date:2018/4/3
 * Description:
 *
 * @author wangguang.
 */
class SeasonAdapter(val context: Context) : BaseAdapter() {
    var seasonData: List<AppSeasonItem> = listOf()

    override fun getItem(position: Int): Any {
        return seasonData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return seasonData.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView?.let { it } ?: LayoutInflater.from(context).inflate(R.layout.video_season_text, parent, false)!!
        var textView = view.tag?.let { it as TextView } ?: view.findViewById(R.id.video_search_detail_text) as TextView
        textView.text = (position + 1).toString()
        view.tag = textView
        return view
    }


}