package com.wang.gvideo.migu.ui

import android.view.View
import com.wang.gvideo.migu.model.AppSearchListItem
import com.wang.gvideo.migu.model.AppSeasonItem

/**
 * Date:2018/4/4
 * Description:
 *
 * @author wangguang.
 */
interface OnVideoItemClickListener {
    fun onItemClick(view: View, position: Int, seasonPosition: Int, parent: AppSearchListItem, season: AppSeasonItem?)
}