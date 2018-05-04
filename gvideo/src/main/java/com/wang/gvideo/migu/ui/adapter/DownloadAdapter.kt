package com.wang.gvideo.migu.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.wang.gvideo.R
import com.wang.gvideo.migu.cache.CacheTask
import com.wang.gvideo.migu.cache.ITask

/**
 * Date:2018/5/4
 * Description:
 *
 * @author wangguang.
 */
class DownloadAdapter(val context: Context) : RecyclerView.Adapter<DownloadAdapter.DownloadHolder>() {

    private val TYPE_COMPLETE = 0
    private val TYPE_NORMAL = 1
    var taskList: MutableList<ITask> = mutableListOf()

    override fun getItemViewType(position: Int): Int =
            if (taskList[position].state() == ITask.STATE.STATE_COMPLETE) {
                TYPE_COMPLETE
            } else {
                TYPE_NORMAL
            }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DownloadHolder =
            when (viewType) {
                TYPE_COMPLETE -> {
                    val v = LayoutInflater.from(context).inflate(R.layout.download_complete_item, parent)
                    CompleteDownloadHolder(v)
                }
                TYPE_NORMAL -> {
                    val v = LayoutInflater.from(context).inflate(R.layout.download_normal_item, parent)
                    NormalDownloadHolder(v)
                }
                else -> {
                    throw IllegalStateException("illegal viewType $viewType")
                }
            }


    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: DownloadHolder?, position: Int) {
        if (holder is CompleteDownloadHolder) {
            val current = taskList[position]
            if (current is CacheTask) {
                val oldTask = holder.parent.tag
                if(oldTask != null && oldTask is CacheTask) {
                    oldTask.clearListener()
                }
                holder.img.setImageURI(Uri.parse(current.getImg()), null)
                holder.name.text = current.getName()
                holder.size.text =  "${current.size()/1024/1024}MB"
                holder.parent.tag = current
            }
        }
    }


open class DownloadHolder(val parent: View) : RecyclerView.ViewHolder(parent)

class CompleteDownloadHolder(parent: View) : DownloadHolder(parent) {
    val img = parent.findViewById<SimpleDraweeView>(R.id.complete_download_item_icon)
    val name = parent.findViewById<TextView>(R.id.complete_download_item_name)
    val size = parent.findViewById<TextView>(R.id.complete_download_item_size)
    val time = parent.findViewById<TextView>(R.id.complete_download_item_time)
}

class NormalDownloadHolder(parent: View) : DownloadHolder(parent) {
    val img = parent.findViewById<SimpleDraweeView>(R.id.complete_download_item_icon)
    val name = parent.findViewById<TextView>(R.id.complete_download_item_name)
}
}