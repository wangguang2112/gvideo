package com.wang.gvideo.migu.ui.adapter

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.facebook.drawee.view.SimpleDraweeView
import com.wang.gvideo.R
import com.wang.gvideo.common.view.alert.AlertView
import com.wang.gvideo.migu.cache.CacheManager
import com.wang.gvideo.migu.cache.CacheTask
import com.wang.gvideo.migu.cache.ITask
import com.wang.gvideo.migu.ui.VideoPlayHelper

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
                    val v = LayoutInflater.from(context).inflate(R.layout.download_complete_item, parent, false)
                    CompleteDownloadHolder(v)
                }
                TYPE_NORMAL -> {
                    val v = LayoutInflater.from(context).inflate(R.layout.download_normal_item, parent, false)
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
        val current = taskList[position]
        if (current is CacheTask) {
            val oldTask = holder?.parent?.tag
            if (oldTask != null && oldTask is CacheTask) {
                oldTask.clearListener()
            }
            if (holder is CompleteDownloadHolder) {
                holder.img.setImageURI(Uri.parse(current.getImg()), null)
                holder.name.text = current.getName()
                holder.size.text = "${current.size()/1024/1024}MB"
                holder.parent.tag = current
                holder.parent.setOnClickListener {
                    if(CacheManager.intance().checkVaild(current)) {
                        VideoPlayHelper.startNativeVideo(context, current.path, current.getName())
                    }else{
                        Toast.makeText(context,"此文件已失效",Toast.LENGTH_SHORT).show()
                        deleteConfirm(current.contId(),position)
                    }
                }
                holder.parent.setOnLongClickListener {
                    deleteConfirm(current.contId(),position)
                    true
                }
            } else if (holder is NormalDownloadHolder) {
                holder.img.setImageURI(Uri.parse(current.getImg()), null)
                holder.name.text = current.getName()
                holder.size.text = "${current.ts}/${current.size()}"

                holder.state.apply {
                    text = getStateName(current.state)
                    setTextColor(getStateColor(current.state))
                    current.changeListener = { _, state ->
                        this.text = getStateName(state)
                        text = getStateName(state)
                    }
                }
                holder.progress.apply {
                    progress = current.percent
                    current.percentListener = {
                        progress = it
                        holder.size.text = "${current.ts}/${current.size()}"
                    }
                }
                holder.parent.tag = current
                holder.parent.setOnClickListener {
                    when (current.state) {
                        ITask.STATE.STATE_ERROR -> CacheManager.intance().resume(current.contId())
                        ITask.STATE.STATE_RUNNING -> CacheManager.intance().pause(current.contId())
                        ITask.STATE.STATE_PAUSE -> CacheManager.intance().resume(current.contId())
                    }
                }
                holder.parent.setOnLongClickListener {
                    deleteConfirm(current.contId(),position)
                    true
                }
            }
        }
    }

    fun getStateName(state: ITask.STATE): String {
        return when (state) {
            ITask.STATE.STATE_PARPERA -> "准备中"
            ITask.STATE.STATE_PAUSE -> "已暂停"
            ITask.STATE.STATE_WAITING -> "等待中"
            ITask.STATE.STATE_RUNNING -> "正在下载"
            ITask.STATE.STATE_COMPLETE -> "已完成"
            ITask.STATE.STATE_ERROR -> "下载错误"
        }
    }

    fun getStateColor(state: ITask.STATE): Int {
        return when (state) {
            ITask.STATE.STATE_PARPERA -> context.resources.getColor(R.color.gray_text)
            ITask.STATE.STATE_PAUSE -> context.resources.getColor(R.color.gray_text)
            ITask.STATE.STATE_WAITING -> context.resources.getColor(R.color.gray_text)
            ITask.STATE.STATE_RUNNING -> Color.BLACK
            ITask.STATE.STATE_COMPLETE -> Color.GREEN
            ITask.STATE.STATE_ERROR -> Color.RED
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
        val img = parent.findViewById<SimpleDraweeView>(R.id.normal_download_item_icon)
        val name = parent.findViewById<TextView>(R.id.normal_download_item_name)
        val progress = parent.findViewById<ProgressBar>(R.id.normal_download_item_progress)
        val state = parent.findViewById<TextView>(R.id.normal_download_item_state)
        val size = parent.findViewById<TextView>(R.id.normal_download_item_size)
    }

    fun deleteConfirm(conId:String,itemPos: Int){
        val alertView = AlertView.Builder()
                .setContext(context)
                .setStyle(AlertView.Style.Alert)
                .setTitle("是否删除")
                .setMessage(null)
                .setCancelText("取消")
                .setDestructive("确定")
                .setOnItemClickListener{ o, position ->
                    if(position == 0){
                        CacheManager.intance().delete(conId)
                        taskList.removeAt(itemPos)
                        notifyItemRangeRemoved(itemPos,1)
                    }
                }
                .build()
        alertView.show()
    }
}