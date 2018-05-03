package com.wang.gvideo.migu.model

import com.wang.gvideo.common.dao.IDaoAdapter
import com.wang.gvideo.migu.dao.model.RecommondDao

/**
 * Date:2018/4/27
 * Description:
 *
 * @author wangguang.
 */
data class MovieItemList(var searchResult:List<MovieItem>)
data class MovieItem(var contentId:String,
                     var contentName:String,
                     var img:String,
                     var contentScore:String,
                     var actors:String,
                     var movieType:String) {
    companion object {
        val adapter = object : IDaoAdapter<MovieItem, RecommondDao> {
            override fun reAdapt(t: RecommondDao?): MovieItem? {
                t?.let {
                    return MovieItem(t.contentId, t.contentName, t.img, t.contentScore, "", t.movieType)
                }
                return null
            }

            override fun adapt(t: MovieItem): RecommondDao {
                return RecommondDao(t.contentId,t.contentName,t.img,t.contentScore,t.movieType,System.currentTimeMillis())
            }


        }
    }

}