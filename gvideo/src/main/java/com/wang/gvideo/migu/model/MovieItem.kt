package com.wang.gvideo.migu.model

/**
 * Date:2018/4/27
 * Description:
 *
 * @author wangguang.
 */
data class MovieItemList(var searchResult:List<MovieItem>)
data class MovieItem(var contentId:String,var contentName:String)