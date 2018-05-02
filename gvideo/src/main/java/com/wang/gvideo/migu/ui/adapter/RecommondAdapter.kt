package com.wang.gvideo.migu.ui.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.wang.gvideo.R
import com.wang.gvideo.common.utils.nil
import com.wang.gvideo.common.utils.safeGetRun
import com.wang.gvideo.migu.dao.model.ViewVideoDao
import com.wang.gvideo.migu.model.MovieItem
import javax.inject.Inject

/**
 * Date:2018/4/2
 * Description:
 *
 * @author wangguang.
 */
class RecommondAdapter @Inject constructor(val context: Context, var recommondList: MutableList<MovieItem>,
                                           var itemListener: ((View, MovieItem, Int) -> Unit))
    : RecyclerView.Adapter<RecommondAdapter.HistoryHolder>() {

    override fun getItemCount(): Int {
        return recommondList?.size.nil { 0 }
    }

    override fun onBindViewHolder(holder: HistoryHolder?, position: Int) {
        recommondList.safeGetRun(position) { current ->
            holder?.let { ho ->
                ho.iconView.setImageURI(Uri.parse(current.img))
                ho.nameView.text = current.contentName
                ho.scoreView.text = current.contentScore
                ho.parent.setOnClickListener {
                    itemListener(ho.parent, current, position)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): HistoryHolder {
        val contentView = LayoutInflater.from(context).inflate(R.layout.video_recommond_item, parent, false)
        return HistoryHolder(contentView)
    }

    class HistoryHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconView = itemView.findViewById<ImageView>(R.id.video_recommond_image)!!
        val nameView = itemView.findViewById<TextView>(R.id.video_recomond_name)
        val parent: View = itemView
        val scoreView = itemView.findViewById<TextView>(R.id.video_recommond_score)


    }

}


