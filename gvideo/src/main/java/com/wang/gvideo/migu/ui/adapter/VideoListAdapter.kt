package com.wang.gvideo.migu.ui.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.facebook.drawee.view.SimpleDraweeView
import com.wang.gvideo.R
import com.wang.gvideo.common.base.BaseActivity
import com.wang.gvideo.common.bus.RxBus
import com.wang.gvideo.common.net.OneSubScriber
import com.wang.gvideo.common.utils.empty
import com.wang.gvideo.common.utils.nil
import com.wang.gvideo.common.view.HorizontalListView
import com.wang.gvideo.migu.constant.BusKey
import com.wang.gvideo.migu.dao.CollectManager
import com.wang.gvideo.migu.model.*
import com.wang.gvideo.migu.ui.OnVideoItemClickListener
import javax.inject.Inject

/**
 * Date:2018/4/2
 * Description:
 *
 * @author wangguang.
 */
class VideoListAdapter @Inject constructor(private val context: Context) : RecyclerView.Adapter<VideoListAdapter.VideoListHolder>() {

    var data: List<AppSearchListItem>? = null
    var mListener: OnVideoItemClickListener? = null
    var itemListener: ((View, Int, Int, AppSearchListItem, AppSeasonItem?) -> Unit)? = null

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        val type = data?.get(position)?.contType ?: 0
        return if (type == 4 || type == 1) {
            1
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: VideoListHolder?, position: Int) {
        holder?.let {
            if (holder is GoodListHolder) {
                handGoodListHolder(holder, position)
            } else if (holder is NormalListHolder) {
                handNormalListHolder(holder, position)
            }
        }
    }

    private fun handNormalListHolder(holder: NormalListHolder, position: Int) {
        data?.let { listData ->
            var currentItem = listData[position]
            holder.iconView?.setImageURI(Uri.parse(currentItem.img))
            holder.nameView?.text = currentItem.contName
            holder.collectView?.apply {
                isSelected = currentItem.isCollect
                setOnClickListener {
                    if (isSelected) {
                        CollectManager.manager.unCollectSeasonVideo(AppSearchListItem.getContId(currentItem.contParam))
                        if (CollectManager.manager.checkExit(AppSearchListItem.getContId(currentItem.contParam))) {
                            Toast.makeText(context, "删除收藏失败", Toast.LENGTH_SHORT).show()
                        } else {
                            holder.collectView?.isSelected = false
                            currentItem.isCollect = false
                            RxBus.instance().postEmptyEvent(BusKey.UPDATE_COLLECT_LIST)
                            Toast.makeText(context, "删除收藏成功", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        CollectManager.manager.collectSeasonVideo(currentItem, AppSearchListItem.getDaoAdapter(),
                                currentItem.subList, AppSeasonItem.getDaoAdapter())
                        if (CollectManager.manager.checkExit(AppSearchListItem.getContId(currentItem.contParam))) {
                            Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show()
                            holder.collectView?.isSelected = true
                            currentItem.isCollect = true
                            RxBus.instance().postEmptyEvent(BusKey.UPDATE_COLLECT_LIST)
                        } else {
                            Toast.makeText(context, "收藏失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            if (currentItem.subList?.isEmpty().nil { true }) {
                holder.typeView?.visibility = View.VISIBLE
                holder.typeView?.text = AppSearchListItem.getTypeName(currentItem.contType)
            } else {
                holder.typeView?.visibility = View.GONE
            }
            var adapter = holder.diversityView?.adapter
            if (adapter == null) {
                adapter = SeasonAdapter(context)
                holder.diversityView?.adapter = adapter
            }
            if (adapter is SeasonAdapter) {
                if (listData[position].subList.empty()) {
                    holder.diversityView?.visibility = View.GONE
                } else {
                    holder.diversityView?.visibility = View.VISIBLE
                    adapter.seasonData = listData[position].subList.nil { listOf() }
                    adapter.notifyDataSetChanged()
                }
            }
            holder.diversityView?.setOnItemClickListener { _, view, childPos, _ ->
                mListener?.onItemClick(view, position, childPos, listData[position], listData[position].subList[childPos])
                itemListener?.let {
                    it(view, position, childPos, listData[position], listData[position].subList[childPos])
                }
            }
            holder.parent.tag = position
            holder.parent.setOnClickListener { view ->
                view.tag?.let {
                    val pos = it as Int
                    mListener?.onItemClick(view, pos, 0, listData[pos], null)
                    itemListener?.let {
                        it(view, pos, 0, listData[pos], null)
                    }
                }
            }

        }
    }


    private fun handGoodListHolder(holder: GoodListHolder, position: Int) {
        data?.let { listData ->
            var currentItem = listData[position]
            holder.iconView?.setImageURI(Uri.parse(currentItem.img))
            holder.nameView?.text = currentItem.contName
            holder.collectView?.apply {
                isSelected = currentItem.isCollect
                setOnClickListener {
                    if (isSelected) {
                        CollectManager.manager.unCollectSeasonVideo(AppSearchListItem.getContId(currentItem.contParam))
                        if (CollectManager.manager.checkExit(AppSearchListItem.getContId(currentItem.contParam))) {
                            Toast.makeText(context, "删除收藏失败", Toast.LENGTH_SHORT).show()
                        } else {
                            currentItem.isCollect = false
                            holder.collectView?.isSelected = false
                            Toast.makeText(context, "删除收藏成功", Toast.LENGTH_SHORT).show()
                            RxBus.instance().postEmptyEvent(BusKey.UPDATE_COLLECT_LIST)
                        }
                    } else {
                        CollectManager.manager.collectSeasonVideo(currentItem, AppSearchListItem.getDaoAdapter(),
                                currentItem.subList, AppSeasonItem.getDaoAdapter())
                        if (CollectManager.manager.checkExit(AppSearchListItem.getContId(currentItem.contParam))) {
                            Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show()
                            holder.collectView?.isSelected = true
                            currentItem.isCollect = true
                            RxBus.instance().postEmptyEvent(BusKey.UPDATE_COLLECT_LIST)
                        } else {
                            Toast.makeText(context, "收藏失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            holder.actorView?.text = if (currentItem.actor.isNotEmpty()) {
                "主演：" + currentItem.actor
            } else {
                ""
            }
            if (currentItem.subList?.isEmpty().nil { true }) {
                holder.typeView?.visibility = View.VISIBLE
                holder.fearureView?.visibility = View.VISIBLE
                holder.categoryView?.visibility = View.VISIBLE
                holder.typeView?.text = "·${currentItem.contentType}"
                holder.fearureView?.text = if (currentItem.prop.isNotEmpty()) {
                    "·${currentItem.prop}"
                } else {
                    ""
                }
                holder.categoryView?.text = AppSearchListItem.getTypeName(currentItem.contType)
            } else {
                holder.typeView?.visibility = View.GONE
                holder.fearureView?.visibility = View.GONE
                holder.categoryView?.visibility = View.GONE
            }
            var adapter = holder.diversityView?.adapter
            if (adapter == null) {
                adapter = SeasonAdapter(context)
                holder.diversityView?.adapter = adapter
            }

            if (adapter is SeasonAdapter) {
                if (listData[position].subList.empty()) {
                    holder.diversityView?.visibility = View.GONE
                } else {
                    holder.diversityView?.visibility = View.VISIBLE
                    adapter.seasonData = listData[position].subList.nil { listOf() }
                    adapter.notifyDataSetChanged()
                }
            }
            holder.diversityView?.setOnItemClickListener { _, view, childPos, _ ->
                mListener?.onItemClick(view, position, childPos, listData[position], listData[position].subList[childPos])
                itemListener?.let {
                    it(view, position, childPos, listData[position], listData[position].subList[childPos])
                }
            }
            holder.parent.tag = position
            holder.parent.setOnClickListener { view ->
                view.tag?.let {
                    val pos = it as Int
                    mListener?.onItemClick(view, pos, 0, listData[pos], null)
                    itemListener?.let {
                        it(view, pos, 0, listData[pos], null)
                    }
                }
            }
            if (currentItem.isVip == 1) {
                holder.vipView?.visibility = View.VISIBLE
            } else {
                holder.vipView?.visibility = View.GONE
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VideoListHolder {
        return when (viewType) {
            1 -> {
                var contentView = LayoutInflater.from(context).inflate(R.layout.video_search_list_good_item, parent, false)
                var holder = GoodListHolder(contentView)
                holder.iconView = contentView.findViewById(R.id.video_item_icon) as SimpleDraweeView
                holder.nameView = contentView.findViewById(R.id.video_item_name) as TextView
                holder.actorView = contentView.findViewById(R.id.video_item_actor) as TextView
                holder.fearureView = contentView.findViewById(R.id.video_item_feature) as TextView
                holder.typeView = contentView.findViewById(R.id.video_item_display) as TextView
                holder.categoryView = contentView.findViewById(R.id.video_item_type) as TextView
                holder.vipView = contentView.findViewById(R.id.video_item_vip_msg) as TextView
                holder.collectView = contentView.findViewById(R.id.video_item_collect) as ImageView
                holder.diversityView = contentView.findViewById(R.id.video_item_diversity) as HorizontalListView
                holder
            }
            else -> {
                var contentView = LayoutInflater.from(context).inflate(R.layout.video_search_list_normal_item, parent, false)
                var holder = NormalListHolder(contentView)
                holder.iconView = contentView.findViewById(R.id.normal_video_item_icon) as SimpleDraweeView
                holder.nameView = contentView.findViewById(R.id.normal_video_item_name) as TextView
                holder.typeView = contentView.findViewById(R.id.normal_video_item_display) as TextView
                holder.collectView = contentView.findViewById(R.id.normal_video_item_collect) as ImageView
                holder.diversityView = contentView.findViewById(R.id.normal_video_item_diversity) as HorizontalListView
                holder
            }
        }
    }

    open class VideoListHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var parent: View = itemView
    }

    class GoodListHolder constructor(itemView: View) : VideoListHolder(itemView) {
        var iconView: SimpleDraweeView? = null
        var nameView: TextView? = null
        var actorView: TextView? = null
        var fearureView: TextView? = null
        var typeView: TextView? = null
        var categoryView: TextView? = null
        var vipView: TextView? = null
        var collectView: ImageView? = null
        var diversityView: HorizontalListView? = null
    }


    class NormalListHolder constructor(itemView: View) : VideoListHolder(itemView) {
        var iconView: SimpleDraweeView? = null
        var nameView: TextView? = null
        var typeView: TextView? = null
        var collectView: ImageView? = null
        var diversityView: HorizontalListView? = null
    }

}

