package com.wang.gvideo.migu.ui.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.wang.gvideo.R
import com.wang.gvideo.common.utils.nil
import com.wang.gvideo.common.utils.safeGetRun
import com.wang.gvideo.migu.dao.model.ViewVideoDao
import javax.inject.Inject

/**
 * Date:2018/4/2
 * Description:
 *
 * @author wangguang.
 */
class HistoryAdapter @Inject constructor(val context: Context, var historyList: MutableList<ViewVideoDao>,
                                         var itemListener: ((View, ViewVideoDao, Int) -> Unit))
    : RecyclerView.Adapter<HistoryAdapter.HistoryHolder>() {


    override fun getItemCount(): Int {
        return historyList?.size.nil { 0 }
    }

    override fun onBindViewHolder(holder: HistoryHolder?, position: Int) {
        historyList.safeGetRun(position) { current ->
            holder?.let { ho ->
                ho.iconView?.setImageURI(Uri.parse(current.image))
                ho.nameView?.text = current.name
                if (current.precent > 98) {
                    ho.precentView?.text = "已看完"
                } else {
                    ho.precentView?.text = "${current.precent}%"
                }
                ho.parent.setOnClickListener {
                    itemListener(ho.parent, current, position)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): HistoryHolder {
        val contentView = LayoutInflater.from(context).inflate(R.layout.video_history_item, parent, false)
        var holder = HistoryHolder(contentView)
        holder.iconView = contentView.findViewById(R.id.video_history_image) as SimpleDraweeView
        holder.nameView = contentView.findViewById(R.id.video_history_name) as TextView
        holder.precentView = contentView.findViewById(R.id.video_history_precent) as TextView
        return holder
    }

    class HistoryHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iconView: SimpleDraweeView? = null
        var nameView: TextView? = null
        var parent: View = itemView
        var precentView: TextView? = null
    }

}


